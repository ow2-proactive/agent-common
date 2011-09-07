#!/bin/bash

#Check variable envirronement PROACTIVESS
if [ -z "$PROACTIVESS" ]; then
	echo "please set PROACTIVESS in your environment variables."
	exit 1
fi

#Check is proactive user exists
if [ -z $(getent passwd proactive) ]; then
 	echo "proactive user does not exist, please create it."
	exit 1

else
 	echo "user does exists"
fi

path="tmp"

if [ "${PROACTIVESS: (-1)}" == "/" ]; then
	
	len=${#PROACTIVESS}
	path=${PROACTIVESS:0:$len-1}
else
	path=$PROACTIVESS
fi

echo "PROACTIVESS is set as : $path"

# Main directory for Pro Active ScreenSaver 
sudo mkdir $path
sudo cp -r ./PAScreenSaver/* ${path}/
sudo chmod -R 700 $path
sudo chmod 755 $path
sudo chmod 755 $path/proxy.py
sudo chown -R proactive $path
echo "./PAScreenSaver/ => $path"


# The ProActive ScreenSaver 
sudo cp ./ProActiveScreenSaver /usr/lib/xscreensaver/ProActive
sudo chmod 755 /usr/lib/xscreensaver/ProActive
sudo chown root /usr/lib/xscreensaver/ProActive
echo "./ProActiveScreenSaver => /usr/lib/xscreensaver/ProActive"

# The ProActive ScreenSaver  .desktop
sudo cp ./ProActiveScreenSaver.desktop /usr/share/applications/screensavers/ProActive.desktop
sudo chmod 755 /usr/share/applications/screensavers/ProActive.desktop
sudo chown root /usr/share/applications/screensavers/ProActive.desktop
echo "./ProActiveScreenSaver.desktop => /usr/share/applications/screensavers/ProActive.desktop"

# autostart proxy at system boot for all users
sudo cp ./proxyPAScreenSaver.desktop /usr/share/gnome/autostart/proxyPAScreenSaver.desktop
sudo chmod 744 /usr/share/gnome/autostart/proxyPAScreenSaver.desktop
sudo chown root /usr/share/gnome/autostart/proxyPAScreenSaver.desktop
echo "./proxyPAScreenSaver.desktop => /usr/share/gnome/autostart/proxyPAScreenSaver.desktop"

# autostart server at system boot for proactive user
sudo cp ServeurProActiveScreenSaver.sh /etc/init.d/
sudo update-rc.d ServeurProActiveScreenSaver.sh defaults
sudo chmod +x /etc/init.d/ServeurProActiveScreenSaver.sh
echo "./ServeurProActiveScreenSaver.sh => /etc/init.d/ServeurProActiveScreenSaver.sh"

