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

from gobject import MainLoop
from dbus import SessionBus
from dbus.mainloop.glib import DBusGMainLoop
from subprocess import Popen
import socket
import sys
import commands

class SSTrigger:
	
    # FORMAT MESSAGE : [START/STOP] [USERNAME]
	
    start = 'START'
    stop = 'STOP'
    HOST = "localhost"
    PORT = 9999

    def __init__(self):
        DBusGMainLoop(set_as_default=True)
        self.mem='ActiveChanged'
        self.dest='org.gnome.ScreenSaver'
        self.bus=SessionBus()
        self.loop=MainLoop()
        self.bus.add_signal_receiver(self.catch,self.mem,self.dest)
    def catch(self,ssOn):
        if ssOn == 1: #Screensaver turned on
           
            #current_time = commands.getstatusoutput( 'date' )
            data = self.start + " " + commands.getoutput( 'whoami' )
            
            # Create a socket (SOCK_STREAM means a TCP socket)
	    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	    
	    # Connect to server and send data
	    sock.connect((self.HOST, self.PORT))
	    sock.send(data + "\n")
	    
	    # Receive data from the server and shut down
	    received = sock.recv(1024)
	    sock.close()
	    
	    print "Sent:     %s" % data
	    print "Received: %s" % received

        else: #Screensaver turned off
 	    
 	    data = self.stop + " " + commands.getoutput( 'whoami' )
            
        # Create a socket (SOCK_STREAM means a TCP socket)
	    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	    
	    # Connect to server and send data
	    sock.connect((self.HOST, self.PORT))
	    sock.send(data + "\n")
	    
	    # Receive data from the server and shut down
	    received = sock.recv(1024)
	    sock.close()
	    
	    print "Sent:     %s" % data
	    print "Received: %s" % received

if len(sys.argv) == 2:
    comm = sys.argv[1]
    if comm == 'SHUTDOWN':
        print comm
        # Create a socket (SOCK_STREAM means a TCP socket)
        data = comm + " " + commands.getoutput( 'whoami' )
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        
        # Connect to server and send data
        sock.connect(("localhost", 9999))
        sock.send(data + "\n")
        
else:
    SSTrigger().loop.run()
