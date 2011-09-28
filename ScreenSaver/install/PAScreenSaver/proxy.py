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

# author : philippe Gouttefarde

from gobject import MainLoop
from dbus import SessionBus
from dbus.mainloop.glib import DBusGMainLoop
from subprocess import Popen
import socket
import sys
import commands
import os
import time

'''
    This proxy listen to events on org.gnome.ScreenSaver dbus.
    If statut switch to True, proxy send START signal to local server with username and screen size.
    If status switch to False, proxy send STOP signal, and server substract an user. If total users using 
    screensaver fall to zero, the .jar stop.
'''

class SSTrigger:
	
    # FORMAT MESSAGE : [START/STOP] [X] [Y]

    MAIN_DIR = "/usr/bin/ProActiveScreenSaver"

    start = 'START'
    stop = 'STOP'

    #log path of proxy
    config_file = 'log/proxy.txt'

    #named pipe path
    pipe_path = "/tmp/ss.pipe"

    #Initialize dbus
    def __init__(self):
        DBusGMainLoop(set_as_default=True)
        self.mem='ActiveChanged'
        self.dest='org.gnome.ScreenSaver'
        self.bus=SessionBus()
        self.loop=MainLoop()
        self.bus.add_signal_receiver(self.catch,self.mem,self.dest)

    # log method
    def writeLOG(self, txt):
    	f = open( self.MAIN_DIR + "/" + self.config_file ,'a')
        f.write( txt )
        f.close()

    # receive signal and write message on dbus
    def catch(self,ssOn):
        
        screen = commands.getoutput("xrandr | grep \* | tr -s ' ' | cut -d' ' -f2")
        screen_res = screen.split('x')
    
        #Screensize	
        length = screen_res[0]
        width = screen_res[1]
        
        if ssOn == 1: #Screensaver turned on

            data = self.start + " " + length + " " + width
            
            pipe = open(self.pipe_path, 'w')
	    
            pipe.write(data)		
            pipe.close()

            self.writeLOG(time.ctime() + "proxy has send : " + data + "\n")
            print "proxy has send : " + data

        else: #Screensaver turned off
 	    
            data = self.stop + " " + length + " " + width
            
            pipe = open(self.pipe_path, 'w')
	    
            pipe.write(data)		
            pipe.close()

            self.writeLOG(time.ctime() + "proxy has send : " + data + "\n")
            print "proxy has send : " + data

#Wait server init the process
while not os.path.exists("/tmp/ss.pipe"):
    print "wait...\n"
    time.sleep( 1 )

#debug param ( START or STOP )
if len(sys.argv) == 2:
    comm = sys.argv[1]
    print comm
    # Write on the pipe
    screen = commands.getoutput("xrandr | grep \* | tr -s ' ' | cut -d' ' -f2")
    screen_res = screen.split('x')
    
    #Screensize	
    x = screen_res[0]
    y = screen_res[1]
    data = comm + " " + x + " " + y
    pipe = open("/tmp/ss.pipe", 'w')
	    
    pipe.write(data)		
    pipe.close()

# Basic application start       
else:
    print "Starting listening dbus gnome-screensaver..."
    SSTrigger().loop.run()
