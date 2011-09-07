#!/bin/sh

# Main directory for Pro Active ScreenSaver 
sudo rm -r /usr/bin/ProActiveScreenSaver
echo "remove /usr/bin/ProActiveScreenSaver folder"

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
