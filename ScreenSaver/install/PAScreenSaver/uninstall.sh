#!/bin/sh

if [ $USER != 'root' ]; then
	echo "REQUIRES ROOT"
	exit 0
fi

PROACTIVESS=`pwd`

# Main directory for Pro Active ScreenSaver 
rm -rf $PROACTIVESS
echo "remove $PROACTIVESS folder [OK]"

# The ProActive ScreenSaver 
rm /usr/lib/xscreensaver/ProActive
echo "remove  /usr/lib/xscreensaver/ProActive file [OK]"

# The ProActive ScreenSaver  .desktop
rm /usr/share/applications/screensavers/ProActive.desktop
echo "remove /usr/share/applications/screensavers/ProActive.desktop file [OK]"

# autostart proxy at system boot for all users
rm /usr/share/gnome/autostart/proxyPAScreenSaver.desktop
echo "remove /usr/share/gnome/autostart/proxyPAScreenSaver.desktop file [OK]"

# autostart server at system boot for proactive user
update-rc.d -f ServeurProActiveScreenSaver remove
rm /etc/init.d/ServeurProActiveScreenSaver
echo "remove /etc/init.d/ServeurProActiveScreenSaver file [OK]"

# remove export line in /etc/environment file
grep -v "PROACTIVESS" /etc/environment > /etc/environment2
rm /etc/environment
mv /etc/environment2 /etc/environment
echo "remove export line in /etc/environment file [OK]"