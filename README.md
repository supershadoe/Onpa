<img src="assets/onpa.png" width="128" alt="App icon" align="right"/>

# Onpa

_Version 3.1_

**App that streams computer audio to an Android phone over LAN**

## Info
- 📄 Licensed under Apache-2.0
- ⚙️  Requires Android 7.0 or above. (and [v2](https://github.com/supershadoe/Onpa/releases/download/v2.0/Onpa.apk) runs on Android 5.0+)
- ⚙️  Uses PulseAudio module `module-simple-protocol-tcp` to stream PCM audio (signed 16 bit LE)

## How to use
Phone-side:
  - After installing the APK, add the quick setting tile from the menu.
  - Long press the tile to open settings.
  - Set the IP address, port number, sample rate and Mono/Stereo setting in settings.
  - Tap on the tile to play/pause playback.

PC side:
  - Place the `pashare` script in some folder in PATH(/usr/bin, /usr/local/bin or some other folder)
  - Set the port and sample rate in `pashare` script.(It's 8000 and 48000Hz by default)
  - Use `pashare start` to start streaming audio and `pashare stop` to stop streaming audio.

## Changelog
### Version 3.1
- **Different package name: If using an older version, it should be reinstalled**
- No feature updates.
- Upgraded to Java 8 in gradle
- Uses view binding except preferences screen
- Removed unused resources and res values
- Small changes like: changing build tools version and removing useless libraries

### Version 3.0
- Revamped and an easier to use UI.
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

## App updates
App can be updated by downloading APKs from the [Releases](https://www.github.com/supershadoe/Onpa/releases) page in GitHub.

## Alternatives
- Just using the `PulseDroid` app itself.
- Few forks of `PulseDroid` like [ferreum](https://gitlab.com/ferreum/PulseDroid)'s
- [PulseDroidRtp](https://github.com/wenxin-wang/PulseDroidRtp) - Uses RTP instead of TCP

## Future
No one knows what the future holds but for now, I don't plan on updating this for now.
