#!/bin/sh

# author : philippe Gouttefarde

PROACTIVESS=/usr/bin/ProActiveScreenSaver

id=`id -u`
if [ $USER != 'root' -a $id==0 ]; then
	echo "REQUIRES ROOT"
	exit 0
fi

# Main directory for Pro Active ScreenSaver 
rm -rf $PROACTIVESS
echo "remove $PROACTIVESS folder [OK]"

#Get linux distribution
tmp=`cat /etc/*-release`
distrib=`echo $tmp | grep Ubuntu`

if [ $(echo ${#distrib}) -gt 1 ]
then
	echo "Ubuntu distribution detected."

	# The ProActive ScreenSaver 
	rm /usr/lib/xscreensaver/ProActive
	echo "remove  /usr/lib/xscreensaver/ProActive file [OK]"

	# The ProActive ScreenSaver  .desktop
	rm /usr/share/applications/screensavers/ProActive.desktop
	echo "remove /usr/share/applications/screensavers/ProActive.desktop file [OK]"

	# autostart server at system boot for proactive user
	update-rc.d -f ServerProActiveScreenSaver remove
	rm /etc/init.d/ServerProActiveScreenSaver
	echo "remove /etc/init.d/ServerProActiveScreenSaver file [OK]"

else
	distrib=`echo $tmp | grep Fedora`

	if [ $(echo ${#distrib}) -gt 1 ]
	then
		echo "Fedora distribution detected."

		# The ProActive ScreenSaver 
		rm /usr/libexec/xscreensaver/ProActive
		echo "remove  /usr/libexec/xscreensaver/ProActive file [OK]"

		# The ProActive ScreenSaver  .xml
		rm /usr/share/xscreensaver/config/ProActive.xml
		echo "remove/usr/share/xscreensaver/config/ProActive.xml file [OK]"

		# autostart server at system boot for proactive user
		grep -v "server.py" /etc/rc.local > /etc/rc.local2
		rm /etc/rc.loval
		mv /etc/rc.local2 /etc/rc.local
		echo "remove export line in /etc/rc.local file [OK]"

	fi
fi
