# Onpa
This is an app to stream computer audio over LAN using Pulseaudio module `module-simple-protocol-tcp`.

## Changes done till now
Version 2.0
- Added support for dark mode.
- Port is set to 8000 by default and port is now set from settings.
- Kotlinization of code.
- Added a help view which helps new users to figure out things.
- Redesign of app and icon.
- Added wakelock support to enable Onpa to play audio even when the screen is off.

Version 1.0

- Converted the app to material design and made slight changes in code.
- Displays a numeric keypad by default(if the keyboard supports it) for easier typing.

## Installation
Run the `install.sh` script in the root directory of the repo to install a small script called `pashare` in `/usr/local/bin` location. This script enables the pulseaudio module on running it.

If you want to change the Monitor Source (if it doesn't work) or the port which `pashare` script by default sends audio via, open the script using vim or any other text editor and edit it.

Default port in pashare is 8000

Download the apk file of Onpa and install it to use it.
Works on Android 5 (Lollipop) or higher.

## License
This app is licensed under the GNU GPL v3.0
For more details, read the LICENSE file present along with the files.

## Credits
@dront78 - Inspiration for this app(Version 1 was made from his app [PulseDroid](https://github.com/dront78/pulsedroid))
