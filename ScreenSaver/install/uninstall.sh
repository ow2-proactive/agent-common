#!/bin/sh

if [ $USER != 'root' ]; then
	echo "REQUIRES ROOT"
	exit 0
fi

#Check variable envirronement PROACTIVESS
if [ -z "$PROACTIVESS" ]; then
	echo "please set PROACTIVESS in your environment variables."
	exit 1
fi

# Main directory for Pro Active ScreenSaver 
sudo rm -r $PROACTIVESS
echo "remove $PROACTIVESS folder"

# The ProActive ScreenSaver 
sudo rm /usr/lib/xscreensaver/ProActive
echo "remove  /usr/lib/xscreensaver/ProActive file"

# The ProActive ScreenSaver  .desktop
sudo rm /usr/share/applications/screensavers/ProActive.desktop
echo "remove /usr/share/applications/screensavers/ProActive.desktop file"

# autostart proxy at system boot for all users
sudo rm /usr/share/gnome/autostart/proxyPAScreenSaver.desktop
echo "remove /usr/share/gnome/autostart/proxyPAScreenSaver.desktop file"

# autostart server at system boot for proactive user
sudo update-rc.d -f ServeurProActiveScreenSaver.sh remove
sudo rm /etc/init.d/ServeurProActiveScreenSaver.sh
echo "remove /etc/init.d/ServeurProActiveScreenSaver.sh file"
