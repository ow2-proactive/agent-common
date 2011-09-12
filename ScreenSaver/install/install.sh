#!/bin/bash

id=`id -u`

if [ $USER != 'root' -a $id==0 ]; then
	echo "REQUIRES ROOT"
	exit 0
fi

echo "Installation of ProActive ScreenSaver...."
echo "Path to install application : "
read PROACTIVESS

export PROACTIVESS=$PROACTIVESS

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
 	echo "Check proactive user [OK]"
fi

path="null"

if [ "${PROACTIVESS: (-1)}" == "/" ]; then
	
	len=${#PROACTIVESS}
	path=${PROACTIVESS:0:$len-1}
else
	path=$PROACTIVESS
fi

echo "PROACTIVESS is set as : $path"

# environnement variable for $PROACTIVESS
comm="PROACTIVESS=\"$path\" >> /etc/environment"
sh -c "echo $comm"
echo "add PROACTIVESS=\"$path\" at the end of /etc/environment [OK]"

# Main directory for Pro Active ScreenSaver 
mkdir $path
cp -r ./PAScreenSaver/* ${path}/
chmod -R 700 $path
chmod 755 $path
chmod 755 $path/proxy.py
chown -R proactive $path
echo "./PAScreenSaver/ => $path [OK]"

# The ProActive ScreenSaver 
cp ./ProActiveScreenSaver /usr/lib/xscreensaver/ProActive
chmod 755 /usr/lib/xscreensaver/ProActive
chown root /usr/lib/xscreensaver/ProActive
echo "./ProActiveScreenSaver => /usr/lib/xscreensaver/ProActive [OK]"

# The ProActive ScreenSaver  .desktop
cp ./ProActiveScreenSaver.desktop /usr/share/applications/screensavers/ProActive.desktop
chmod 755 /usr/share/applications/screensavers/ProActive.desktop
chown root /usr/share/applications/screensavers/ProActive.desktop
echo "./ProActiveScreenSaver.desktop => /usr/share/applications/screensavers/ProActive.desktop [OK]"

# autostart proxy at system boot for all users
cp ./proxyPAScreenSaver.desktop /usr/share/gnome/autostart/proxyPAScreenSaver.desktop
comm="Exec=python $path/proxy.py >> /usr/share/gnome/autostart/proxyPAScreenSaver.desktop"
sh -c "echo $comm"
chmod 744 /usr/share/gnome/autostart/proxyPAScreenSaver.desktop
chown root /usr/share/gnome/autostart/proxyPAScreenSaver.desktop
echo "./proxyPAScreenSaver.desktop => /usr/share/gnome/autostart/proxyPAScreenSaver.desktop [OK]"

# autostart server at system boot for proactive user
cp ./ServeurProActiveScreenSaver.sh /etc/init.d/ServeurProActiveScreenSaver
comm="su proactive -c \'python $path/server.py clean\' >> /etc/init.d/ServeurProActiveScreenSaver"
sh -c "echo $comm"
comm="su proactive -c \'python $path/server.py start \&\' >> /etc/init.d/ServeurProActiveScreenSaver"
sh -c "echo $comm"
update-rc.d ServeurProActiveScreenSaver defaults
chmod +x /etc/init.d/ServeurProActiveScreenSaver
echo "./ServeurProActiveScreenSaver.sh => /etc/init.d/ServeurProActiveScreenSaver [OK]"


