# All Off Some On Switch app for Hubitat

An app that creates a virtual switch that...
1. When turned off, turns off ALL selected devices
2. When turned on, turns on only SOME of the devices

When some devices are on, the virtual switch will report as "ON".

Only when all devices are off will the virtual switch report as "OFF".

## Installation

The best way to install this code is by using [Hubitat Package Manager](https://community.hubitat.com/t/beta-hubitat-package-manager):
1. Click on Hubitat Package Manager application
2. Click "Install" (Install a new package)
3. Click "From a URL"
4. Enter the following link: `https://raw.githubusercontent.com/cgmartin/Hubitat-All-Off-Some-On-Switch/main/packageManifest.json`
5. Click "Next" to install

However, if you must install manually:

1. Go to the "Drivers Code" page in Hubitat
2. Click "+ New Driver"
3. Paste in the contents of the `AllOffSomeOnVirtualSwitchDevice.groovy` file
4. Click Save.  You've now set up the driver for the virtual device.
5. Go to the "Apps Code" page in Hubitat
6. Click "+ New App"
7. Paste in the contents of the `AllOffSomeOnSwitchAppParent.groovy` file
8. Click Save
9. Click "+ New App" again
10. Paste in the contents of the `AllOffSomeOnSwitchAppChild.groovy` file
11. Click Save.  You've now set up the parent and child apps.
12. Go to the "Apps" page in Hubitat
13. Click "+ Add User App"
14. Choose "All Off Some On Switches"
15. Click "Done"  You've now activated the parent app.

After installation, to add a new virtual switch:

1. Click on the app "All Off Some On Switch"
2. Click the "Add a new All Off Some On Switch" button
3. Name your switch, and select a set of devices to control. 
4. Click "Done" when finished with settings.

Now if you go to your "Devices" page, you'll find a new virtual device for your switch.

This virtual device is the one you should expose in your dashboards and other user interfaces, such as Alexa or HomeKit.
