#!/bin/bash

# author : philippe Gouttefarde

echo "Installation of ProActive ScreenSaver...."

id=`id -u`
if [ $USER != 'root' -a $id==0 ]; then
	echo "REQUIRES ROOT"
	exit 0
fi

#Check is proactive user exists
if [ -z $(getent passwd proactive) ]; then
 	echo "proactive user does not exist, please create it."
	exit 1

else
 	echo "Check proactive user [OK]"
fi

path=/usr/bin/ProActiveScreenSaver

echo "PROACTIVESS is set as : $path"

#Get linux distribution
tmp=`cat /etc/*-release`
distrib=`echo $tmp | grep Ubuntu`

if [ $(echo ${#distrib}) -gt 1 ]
then
	echo "Ubuntu distribution detected."

	# Main directory for Pro Active ScreenSaver 
	mkdir $path
	cp -r ./PAScreenSaver/* ${path}/
	chmod -R 700 $path
	chmod 755 $path
	chmod 755 $path/proxy.py
	chown -R proactive $path
	find $path | grep .svn | xargs rm -rf
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

	# autostart server at system boot for proactive user
	cp ./ServeurProActiveScreenSaver.sh /etc/init.d/ServeurProActiveScreenSaver
	comm="su proactive -c \'python $path/server.py clean\' >> /etc/init.d/ServeurProActiveScreenSaver"
	sh -c "echo $comm"
	comm="su proactive -c \'python $path/server.py start \&\' >> /etc/init.d/ServeurProActiveScreenSaver"
	sh -c "echo $comm"
	update-rc.d ServeurProActiveScreenSaver defaults
	chmod +x /etc/init.d/ServeurProActiveScreenSaver
	echo "./ServeurProActiveScreenSaver.sh => /etc/init.d/ServerProActiveScreenSaver [OK]"

else
	distrib=`echo $tmp | grep Fedora`

	if [ $(echo ${#distrib}) -gt 1 ]
	then
		echo "Fedora distribution detected."

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
		find $path | grep .svn | xargs rm -rf
		echo "./PAScreenSaver/ => $path [OK]"

		# The ProActive ScreenSaver 
		cp ./ProActiveScreenSaver /usr/libexec/xscreensaver/ProActive
		chmod 755 /usr/libexec/xscreensaver/ProActive
		chown root /usr/libexec/xscreensaver/ProActive
		echo "./ProActiveScreenSaver => /usr/libexec/xscreensaver/ProActive [OK]"

		# The ProActive ScreenSaver  .desktop
		cp ./ProActiveScreenSaver.xml /usr/share/xscreensaver/config/ProActive.xml
		chmod 644 /usr/share/xscreensaver/config/ProActive.xml
		chown root /usr/share/xscreensaver/config/ProActive.xml
		echo "./ProActiveScreenSaver.desktop => /usr/share/applications/screensavers/ProActive.desktop [OK]"

		# autostart server at system boot for proactive user
		comm="su proactive -c \'python $path/server.py clean\' >> /etc/rc.local"
		sh -c "echo $comm"
		comm="su proactive -c \'python $path/server.py start \&\' >> /etc/rc.local"
		sh -c "echo $comm"
		chmod +x /etc/rc.local
		echo "commands for server startup => /etc/rc.local [OK]"
	fi
fi

echo " "
echo "*-------------------------------------*"
echo "Installation is now complete."
echo "You can configure < ${path}/config.txt > file and set your JDK/bin path (at least 1.6 release)."
echo "Thanks for choosing ProActive screensaver."
