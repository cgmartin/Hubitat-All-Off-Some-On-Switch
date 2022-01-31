# NOTES

## Hubitat Groovy Development References:

- <https://docs.hubitat.com/index.php?title=Developer_Documentation>
- <https://community.hubitat.com/t/app-and-driver-porting-to-hubitat/812>
- <https://github.com/hubitat/HubitatPublic>
- <https://github.com/HubitatCommunity>
- <https://github.com/InovelliUSA/Hubitat>
- <https://github.com/bptworld/Hubitat>
- <https://github.com/stephack/Hubitat>
- <https://github.com/joelwetzel?tab=repositories&q=Hubitat&type=&language=&sort=>
- <https://github.com/dcmeglio?tab=repositories&q=hubitat&type=&language=&sort=>

## Release Process:

1. Test code changes locally in HE UI via `Developer` -> `Apps Code`
2. Choose new release version number (major = breaking change, minor = compatible change)
3. Update CHANGELOG.txt with changes
4. Update packageManifest.json with new version number 
5. Update packageManifest.json releaseNotes with encoded CHANGELOG.txt: `cat CHANGELOG.txt | jq -Rs .`
6. Commit changes to git repo and push to remote
7. Create github release and tag with new version number
