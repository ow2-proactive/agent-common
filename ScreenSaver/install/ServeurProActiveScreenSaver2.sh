#!/bin/sh
#
# ProActiveScreenSaverServer Server of ProActive screensaver
#
# chkconfig: - 55 25
# description: The ProActive Screensaver

### BEGIN INIT INFO
# Provides: 
# Required-Start: 
# Required-Stop: 
# Should-Start: 
# Should-Stop: 
# Default-Start: 
# Default-Stop: 
# Short-Description: 
# Description:      
### END INIT INFO

# Source function library.
. /etc/rc.d/init.d/functions

exec="/etc/init.d/ServerProActiveScreenSaver"
prog="ServerProActiveScreenSaver"

[ -x $exec ] || exit 5
echo -n $"Starting $prog: "

su proactive -c 'python /usr/bin/ProActiveScreenSaver/server.py clean'
su proactive -c 'python /usr/bin/ProActiveScreenSaver/server.py start'

