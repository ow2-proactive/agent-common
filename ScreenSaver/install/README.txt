        PROACTIVE SCREENSAVER

This application is an ubuntu screensaver for gnome-screensaver.
It is written in python and java and provides a monitoring screensaver for ProActive linux agent's JVMs.


INSTALL:

User required: proactive (with root access)

switch to a root shell and run: ./install.sh

The install script will ask you a path where install the application (usually in /usr/bin or /home/proactive).
All users need access to $PATH/proxy.py in execution.

Select ProActive screensaver in your screensavers preferences pop-up.

Then, restart computer and enjoy it.

CONFIG:

If installation finished without problem, go to $PATH, and edit $PATH/configSS.txt, 
you must write path to binaries of jdk 1.6 at least allow for proactive user.

exemple : jdk=$JDK_PATH/bin

Then open config.txt and set access policy for your users.