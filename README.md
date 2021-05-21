<p align="center"><img src="assets/onpa.png" width="100" style="border-radius:50%"/></p>
<h2 align="center"><b>Onpa</b></h2>
<h3 align="center"><b>Version 3.1</b></h3>
<h4 align="center">App to stream computer audio to phone over LAN.</h4>

<p align="center">
<a href="https://github.com/supershadoe/Onpa/releases"><img src="https://img.shields.io/badge/Version-v3.1-gold" alt="Github releases" /></a>
<a href="https://github.com/supershadoe/Onpa/releases/download/v3.1/Onpa.apk"><img src="https://img.shields.io/badge/Latest%20APK-v3.1-brightgreen" alt="Latest APK" /></a>
<a href="http://apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/License-Apache%202.0-blue" alt="License: Apache" /></a>
</p>
<hr />

## Description
This is an app to stream computer audio over LAN using PulseAudio module `module-simple-protocol-tcp`.

This app works on Android 7.0(Nougat) and above.

People with android versions less than 7 can use the [old version](https://github.com/supershadoe/Onpa/releases/download/v2.0/Onpa.apk) of the app.

## How to use
Phone-side:
  - After installing the APK, add the quick setting tile from the menu.
  - Long press the tile to open settings.
  - Set the IP address, port number, sample rate and Mono/Stereo setting in settings.
  - Tap the tile to play/pause playback.

PC side:
  - Place the `pashare` script in some folder in PATH(/usr/bin, /usr/local/bin or some other folder)
  - Set the port and sample rate in `pashare` script.(It's 8000 and 48000Hz by default)
  - Give `pashare start` to start streaming audio and `pashare stop` to stop streaming audio.

## Changelog
### Version 3.1

**IMPORTANT CHANGE: Changed the package name from <u>com.supershadoe.onpa</u> to <u>me.supershadoe.onpa</u>**
- No feature updates.
- Upgraded to Java 8 in gradle
- Uses view binding except preferences screen
- Removed unused resources and res values
- Small changes like: changing build tools version and removing useless libraries

### Version 3.0

- Revamped and a more easier to use UI.
  There is no app in menu now. Only exists as a quick settings tile.
- App listens to audio focus so pauses playback when another app starts playing audio.
- Reduced lag in audio.
- Uses wakelock and wifilock to see to that the playback doesn't stop

  Code-wise:
  - Code is documented well and has comments for most of the documents.
  - Instead of having the main activity play/pause, PlayTile service does it.
  - Removed most of the useless/old files.
  - Uses proguard to shrink resources so that the app is smaller in size.

### Version 2.0
- Added support for dark mode.
- Port is set to 8000 by default and port is now set from settings.
- Added a help view which helps new users to figure out things.
- Redesign of app and icon.

  Code-wise:
  - Converted code from Java to Kotlin.
  - Added wakelock support to enable Onpa to play audio even when the screen is off.(Doesn't work)

### Version 1.0
- Converted the [PulseDroid](https://github.com/dront78/PulseDroid) app to material design and made slight changes in code.
- Displays a numeric keypad by default(if the keyboard supports it) for easier typing.

## Bugs and PRs
No more updates will be made.
So, if you want any changes/improvements, you can do so by cloning/forking the repo.

## App updates
App can be updated by downloading APKs from the [Releases](https://www.github.com/supershadoe/Onpa/releases) page in GitHub.

## License
This app is licensed under Apache License, Version 2.0.
A copy of this license can be found in the [LICENSE](https://raw.githubusercontent.com/supershadoe/Onpa/master/LICENSE) file.
