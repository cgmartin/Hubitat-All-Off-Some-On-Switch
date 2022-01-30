/**
 * All Off Some On Switch (Child App)
 *
 * Copyright 2022 Christopher Martin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
import groovy.transform.Field

@Field static String PAGE_MAIN = 'pageMain'
@Field static String EVENT_TYPE_SWITCH = 'switch'
@Field static int RESUBSCRIBE_PAUSE_MS = 1000

definition(
    namespace: 'cgmartin',
    name: 'All Off Some On Switch Child',
    parent: 'cgmartin:All Off Some On Switch',
    author: 'Christopher Martin',
    description: "Child app implementation of the 'All Off Some On Switch' parent app.",
    category: 'Convenience',
    iconUrl: '',
    iconX2Url: '',
    iconX3Url: ''
)

preferences {
    page(name: PAGE_MAIN)
}

def pageMain() {
    dynamicPage(
        name: PAGE_MAIN,
        title: '<b>New All Off Some On Switch</b>',
        install: true,
        uninstall: true
    ) {
        section {
            input(
                name: 'appName',
                type: 'text',
                title: 'Name the virtual switch device',
                submitOnChange: true,
                required: true
            )
            if (appName) {
                app.updateLabel(appName)
            } else {
                app.updateLabel('New All Off Some On Switch')
            }

            input(
                name: 'allOffSwitches',
                type: 'capability.switch',
                title: 'Switches to turn OFF',
                description: 'Select the devices that should ALL turn OFF as a group.',
                multiple: true,
                required: true,
                submitOnChange: true
            )

            def switchOptions = [:]
            allOffSwitches.each {
                switchOptions[it.id] = it.displayName
            }
            input(
                name: 'someOnSwitches',
                type: 'enum',
                title: 'Switches to turn ON',
                description: 'Select some devices in the group to turn on, when all are off.',
                multiple: true,
                required: true,
                options: switchOptions
            )
        }
        section {
            input(
                name: 'meter',
                type: 'number',
                title: 'Use metering (in milliseconds)',
                description: 'Set to 50 or 75 ms if switches not always turning off (large groups of devices)',
                defaultValue: 0,
                width: 4
            )
            input(
                type: 'bool',
                name: 'enableDebugLogging',
                title: 'Enable Debug Logging?',
                required: true,
                defaultValue: false
            )
        }
    }
}

def installed() {
    log.info "Installed with settings: ${settings}"
    addChildDevice(
        'cgmartin',
        'All Off Some On Virtual Switch',
        "AOSOS_${app.getId()}",
        null,
        [
            name: 'All Off Some On Virtual Switch',
            label: app.label,
            completedSetup: true,
            isComponent: true
        ]
    )
    initialize()
}

def uninstalled() {
    childDevices.each {
        log.info "Deleting child device: ${it.displayName}"
        deleteChildDevice(it.deviceNetworkId)
    }
}

def updated() {
    log.info "Updated with settings: ${settings}"

    // Update device name to match app name
    def virtualSwitch = getChildDevice("AOSOS_${app.getId()}")
    if (virtualSwitch) { virtualSwitch.name = app.label }

    unsubscribe()
    initialize()
}

def initialize() {
    log.info "There are ${childDevices.size()} child devices"

    def virtualSwitch = getChildDevice("AOSOS_${app.getId()}")
    app.updateLabel(virtualSwitch.displayName) // Use the device name

    subscribe(allOffSwitches, EVENT_TYPE_SWITCH, devicesChangeHandler)
    devicesChangeHandler(null) // sync state
}

// This handler will be called whenever any of the wrapped devices change.
// Determine the state of the virtual switch...
//   whether it should be on (if ANY are on),
//   or if it should be off (if ALL are off).
def devicesChangeHandler(evt) {
    if (evt != null) {
        logDebug "${evt.device} changed to ${evt.value}"
    }
    def virtualSwitch = getChildDevice("AOSOS_${app.getId()}")
    def virtualSwitchState = virtualSwitch.currentValue('switch')
    logDebug "Update the virtualSwitch state? ${virtualSwitch} = ${virtualSwitchState}"

    Boolean someOn = false
    for (device in allOffSwitches) {
        if (device.currentValue('switch') == 'on') {
            logDebug "Found one device that is ON: ${device}"
            someOn = true
            break
        }
    }
    if (someOn && virtualSwitchState == 'off') {
        virtualSwitch.markAsOn()
    } else if (!someOn && virtualSwitchState == 'on') {
        virtualSwitch.markAsOff()
    }
}

// Loop thru all devices in the "All Off" group,
// Check if the device is a subset of the "Some On" group,
// Turn on the device if it is not already on.
// Pause for metering, if set.
def turnOnDevices() {
    logDebug "turnOnDevices() ${someOnSwitches}"
    Boolean pauseForMetering = false

    for (device in allOffSwitches) {
        Boolean isDeviceInOnGroup = someOnSwitches.contains(device.id)
        logDebug "Is device in ON group? #${device.id} ${device} = ${isDeviceInOnGroup}"
        if (isDeviceInOnGroup) {
            // Device is in the On group.
            def devSwitchState = device.currentValue('switch')
            logDebug "Should turn ON device? #${device.id} ${device} = ${devSwitchState}"
            if (devSwitchState == 'off') {
                // Device should turn on
                if (pauseForMetering) { pause(meter) }
                logDebug "Turning on device: #${device.id} ${device}"
                device.on()
                if (meter) { pauseForMetering = true }
            }
        }
    }
}

// Loop thru all devices in the "All Off" group,
// Turn off the device if it is not already off.
// Pause for metering, if set.
def turnOffDevices() {
    logDebug "turnOffDevices() ${allOffSwitches}"
    Boolean pauseForMetering = false

    for (device in allOffSwitches) {
        def devSwitchState = device.currentValue('switch')
        logDebug "Should turn OFF device? #${device.id} ${device} = ${devSwitchState}"
        if (devSwitchState == 'on') {
            // Device should turn off
            if (pauseForMetering) { pause(meter) }
            logDebug "Turning off device: #${device.id} ${device}"
            device.off()
            if (meter) { pauseForMetering = true }
        }
    }
}

def logDebug(msg) {
    if (enableDebugLogging) {
        log.debug(msg)
    }
}
