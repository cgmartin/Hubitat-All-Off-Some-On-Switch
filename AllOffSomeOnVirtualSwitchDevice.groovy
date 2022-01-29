/**
 * All Off Some On Switch (Virtual Device)
 * NOTE: Do not use this without the All Off Some On Switch applications!
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

@Field static String EVENT_TYPE_SWITCH = 'switch'

metadata {
    definition(name: 'All Off Some On Virtual Switch', namespace: 'cgmartin', author: 'Christopher Martin') {
        capability 'Switch' // "on", "off"
        command 'markAsOn'
        command 'markAsOff'
    }

    preferences {
        section {
        }
    }
}

def log(msg) {
    if (parent.enableDebugLogging) {
        log.debug msg
    }
}

def installed() {
    log.info "${device.displayName}.installed()"
    updated()
}

def updated() {
    log.info "${device.displayName}.updated()"
}

// Tell the parent app to turn on the wrapped devices
def on() {
    log "${device.displayName}.on()"

    //def parent = getParent()
    if (parent == null) {
        return
    }
    parent.turnOnDevices()
}

// Tell the parent app to turn off the wrapped devices
def off() {
    log "${device.displayName}.off()"

    //def parent = getParent()
    if (parent == null) {
        return
    }
    parent.turnOffDevices()
}

// Mark as ON without sending the event back to the parent app.
// Called when wrapped switches have changed, to prevent cyclical firings.
def markAsOn() {
    log "${device.displayName}.markAsOn()"
    sendEvent(name: EVENT_TYPE_SWITCH, value: 'on')
}

// Mark as OFF without sending the event back to the parent app.
// Called when wrapped switches have changed, to prevent cyclical firings.
def markAsOff() {
    log "${device.displayName}.markAsOff()"
    sendEvent(name: EVENT_TYPE_SWITCH, value: 'off')
}
