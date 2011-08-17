#!/usr/bin/env python
  
#
# ################################################################
#
# ProActive Parallel Suite(TM): The Java(TM) library for
#    Parallel, Distributed, Multi-Core Computing for
#    Enterprise Grids & Clouds
#
# Copyright (C) 1997-2011 INRIA/University of
#                 Nice-Sophia Antipolis/ActiveEon
# Contact: proactive@ow2.org or contact@activeeon.com
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Affero General Public License
# as published by the Free Software Foundation; version 3 of
# the License.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this library; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
# USA
#
# If needed, contact us to obtain a release under GPL Version 2 or 3
# or a different license than the AGPL.
#
#  Initial developer(s):               The ActiveEon Team
#                        http://www.activeeon.com/
#  Contributor(s):
#
# ################################################################ 
# $$ACTIVEEON_CONTRIBUTOR$$
#

import login
import commands
import os
import Xlib
import Xlib.display
from subprocess import Popen
import signal

  
# The model of the Daemon, contain all functions.
class Model:
    
    config_file = '/usr/bin/PAAgent/log.txt'
    pid_file = '/usr/bin/PAAgent/pid.tmp'
    
    def launcher(self,command):
        user = commands.getoutput( 'whoami' )
        log = login.Login()
        if log.checkAccess(user):
            if command == 'startJVM':
                print 'startJVM'
                self.startJVM(user)
                
            if command == 'stopJVM':
                print 'stopJVM'
                self.stopJVM(user)
                
        else:
            print 'access denied' 
    
        
    def startJVM(self,user):
        #log part
        f = open( self.config_file ,'a')
        current_time = commands.getstatusoutput( 'date' )
        line = user + ' started JVM at : ' + str(current_time[1]) + '\n'
        f.write( line )
        f.close() 
        
        #launcher java part
        display = Xlib.display.Display()
        root = display.screen().root
        desktop = root.get_geometry()
        x = desktop.width
        y = desktop.height
        cmd = "java -jar /usr/bin/PAAgent/FullScreenSaver.jar /tmp/ScreenSaver.bmp /tmp/dataBase.rrd "
        cmd = cmd + str(x) + " " + str(y) 
        pid = Popen(cmd, shell=True).pid
        f = open(self.pid_file, 'w')
        f.write(str(pid+1))
        f.close()
    
    def stopJVM(self,user):
        f = open( self.config_file ,'a')
        current_time = commands.getstatusoutput( 'date' )
        line = user + ' stopped JVM at : ' + str(current_time[1]) + '\n'
        f.write( line )
        f.close()   
        if os.path.exists( self.pid_file ):
            f = open(self.pid_file, 'r')
            pid = f.read()
            os.kill( int(pid), signal.SIGTERM)
            #os.remove( self.pid_file )
        
    def getPID(self):
        if os.path.exists( self.pid_file ):
            f = open(self.pid_file, 'r')
            pid = f.read()
            print "PID : " + pid
    
    
