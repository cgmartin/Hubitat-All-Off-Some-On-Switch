/**
 * Some On All Off Switch (Child App)
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

@Field static String PAGE_START = 'pageStart'
@Field static String PAGE_NEXT = 'pageNext'
@Field static String EVENT_TYPE_SWITCH = 'switch'

definition(
    namespace: 'cgmartin',
    name: 'All Off Some On Switch',
    parent: 'cgmartin:All Off Some On Switches',
    author: 'Christopher Martin',
    description: 'Child app of Some On All Off Switches.',
    category: 'Convenience',
    iconUrl: '',
    iconX2Url: '',
    iconX3Url: ''
)

preferences {
    page(name: PAGE_START)
    page(name: PAGE_NEXT)
}

def pageStart() {
    dynamicPage(name: PAGE_START, title: '', nextPage: PAGE_NEXT, install: false, uninstall: true) {
        section {
            input(
                name: 'appName',
                type: 'text',
                title: 'Name this instance of the Some On/All Off Switch',
                submitOnChange: true
            )
            if (appName) {
                app.updateLabel(appName)
            }

            input(
                name: 'allOffSwitches',
                type: 'capability.switch',
                title: 'Switches to turn OFF',
                description: 'Select the devices that should ALL turn OFF as a group.',
                multiple: true,
                required: true
            )
        }
    }
}

def pageSecond() {
    def switchOptions = [:]
    allOffSwitches.each {
        switchOptions[it.id] = it.displayName
    }

    dynamicPage(name: 'pageSecond', title: '', install: true, uninstall: true) {
        section {
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
                defaultValue: 50,
                submitOnChange: true,
                width: 4
            )
            input(
                type: 'bool',
                name: 'enableDebugLogging',
                title: 'Enable Debug Logging?',
                required: true,
                defaultValue: true
            )
        }
    }
}

def installed() {
    log.info "Installed with settings: ${settings}"
    addChildDevice(
        'hubitat',
        'Virtual Switch',
        "AOSOS_${app.id}",
        null,
        [
            name: 'Some On All Off Virtual Switch',
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
    unsubscribe()
    initialize()
}

def initialize() {
    log.info "There are ${childDevices.size()} child devices"

    def virtualSwitch = getChildDevice("AOSOS_${app.id}")
    app.updateLabel(virtualSwitch.displayName)

    subscribe(allOffSwitches, EVENT_TYPE_SWITCH, devicesChangeHandler)
    devicesChangeHandler(null) // sync state
}

// This handler will be called whenever any of the wrapped devices change.
// Determine the state of the virtual switch, wether it should be on (if ANY are on),
// or if it should be off (if ALL are off).
def devicesChangeHandler(evt) {
    if (evt != null) {
        logDebug "${evt.device} changed to ${evt.value}"
    }
    def virtualSwitch = getChildDevice("AOSOS_${app.id}")
    logDebug "virtualSwitch state: ${virtualSwitch.currentValue('switch')}"

    Boolean someOn = false
    for (device in allOffSwitches) {
        if (device.value.currentValue('switch') == 'on') {
            virtualSwitch.markAsOn()
            someOn = true
            break
        }
    }
    if (!someOn) {
        virtualSwitch.markAsOff()
    }
}

def turnOnDevices() {
    logDebug 'turnOnDevices()'
}

def turnOffDevices() {
    logDebug 'turnOffDevices()'
}

def logDebug(msg) {
    if (enableDebugLogging) {
        log.debug(msg)
    }
}
