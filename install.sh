#! /bin/sh

# Install script for pashare
# Created by Supershadoe

echo "sudo privileges are necessary for installing this script. If you are not a sudoer, use su for installing the script(root password should be known to you to use su)."
echo "Select man if you are not a sudoer nor you don't know the root password."
read -p "Select the method of installing:(sudo/su/man) " x
case "$x" in
	sudo)
		sudo mv ./pashare /usr/local/bin/pashare
		sudo chmod a+x /usr/local/bin/pashare
		;;
	su)
		su -c "mv ./pashare /usr/local/bin/pashare && chmod a+x /usr/local/bin/pashare"
		;;
	man)
		read -p "Enter the install directory: " y
		mv ./pashare "$y/pashare"
		chmod +x ./pashare
		;;
	*)
		echo "Choose one of the given three options"
		exit 1
		;;
esac
