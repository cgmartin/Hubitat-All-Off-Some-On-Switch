/**
 * All Off Some On Switch (Parent App)
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

@Field static String PAGE_MAIN = 'mainPage'
@Field static String APP_INSTALLED_STATE_COMPLETE = 'COMPLETE'

definition(
    name: 'All Off Some On Switch',
    namespace: 'cgmartin',
    author: 'Christopher Martin',
    description: 'Creates a virtual switch to turn off an entire group of devices (but will only turn on some).',
    category: 'Convenience',
    iconUrl: '',
    iconX2Url: '',
    iconX3Url: ''
)

preferences {
    page(name: PAGE_MAIN, title: '', install: true, uninstall: true)
}

def installed() {
    log.info "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.info "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    log.info "There are ${childApps.size()} child apps"
    childApps.each { child ->
        log.info "Child app: ${child.label}"
    }
}

def installCheck() {
    state.appInstalled = app.getInstallationState()
    if (state.appInstalled != APP_INSTALLED_STATE_COMPLETE) {
        section { paragraph "Please hit 'Done' to install '${app.label}' parent app" }
    }
    else {
        log.info "Parent app '${app.label}' installed OK"
    }
}

def display() {
    section {
        paragraph "<hr style='background-color:#1A77C9; height: 1px; border: 0;'></hr>"
        paragraph "<div style='text-align:center'>${app.label} ~ @cgmartin</div>"
    }
}

def mainPage() {
    dynamicPage(name: PAGE_MAIN) {
        installCheck()

        if (state.appInstalled == APP_INSTALLED_STATE_COMPLETE) {
            section("<h2 style='font-weight: bold'>${app.label}</h2>") {
                paragraph 'Create a virtual switch to turn off an entire group of devices, and to only turn on some.'
            }
            section('<b>Manage your virtual switches:</b>') {
                app(
                    name: 'anyOpenApp',
                    namespace: 'cgmartin',
                    appName: 'All Off Some On Switch Child',
                    title: '<b>Add a new</b> "All Off Some On Switch"',
                    multiple: true
                )
            }
            display()
        }
    }
}
