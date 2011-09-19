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

import SocketServer
from model import Model
import commands
import os
import sys
import time
import stat
import ConfigParser

'''
    This server set as a daemon which wait signal from proxy to execute screensaver.
    Server manages right access from different users.

    It launchs FullScreenSaver.jar if proxy send START signal.
    Stop it when users using screensaver fall to 0.
'''

class Server():
    """
    The RequestHandler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """
    
    MAIN_DIR = os.environ["PROACTIVESS"]
    start = 'START'
    stop = 'STOP'
    shutdown = 'SHUTDOWN'
    answer = 'receive message by : '

    pipe_path = "/tmp/ss.pipe"
    config_file = 'config.txt'

    model = Model()

    jdk_path = ""


    def listen(self):

    	if os.path.exists(self.pipe_path):
		os.remove(self.pipe_path)
		self.model.writeLOG("remove " + self.pipe_path + "\n")

	try:
    		os.mkfifo(self.pipe_path)
    		self.model.writeLOG("create " + self.pipe_path  + "\n")
		
	except OSError:
		print "error scanned"
    		pass

        print "Server start to listen the proxys"
        
        '''
        CHECK JAVA VERSION
        '''

        self.model.writeLOG("Checking JVM path at " + time.ctime() + "\n")
        self.checkJAVA_HOME()
	
        self.model.writeLOG("Java founded : " + self.jdk_path + "\n")

        #Go to proactive directory:
        os.chdir(self.MAIN_DIR)

        '''
                IF IT'S GOOD, RUN THE LISTENNER
        '''
        print "Starting listenning..."
        self.model.init(self.jdk_path)

        os.chmod(self.pipe_path, stat.S_IRWXU | stat.S_IWGRP | stat.S_IWOTH)
        pipe = open(self.pipe_path, 'r')
        self.model.writeLOG("open pipe in read mode.\n")
        while True:

                self.data = pipe.read()

                print "Server has received : %s" % self.data

                tab = self.data.split()

                if len(tab) == 3:
                    self.comm = tab[0]
                    self.length = tab[1]
                    self.width = tab[2]

                    if self.comm == self.start :
                        self.startJVM(self.length,self.width)

                    elif self.comm == self.stop:
                        self.stopJVM()

                    elif self.comm == self.shutdown:
                        self.shutDown()

                time.sleep(1)
        
    def startJVM(self , x , y):
        self.model.launcher('startJVM' , x , y )
            
    def stopJVM(self):
        self.model.launcher('stopJVM')
        
    def shutDown(self):
        self.finish()
        
    def checkJAVA_HOME(self): 
        config = ConfigParser.RawConfigParser()
        
        config.read( self.MAIN_DIR + "/" + self.config_file )
        conf = config.items('SCREENSAVER-CONFIG')

    	jdkPath = conf[0][1]
	print "jdk path : " + jdkPath 
	self.jdk_path = jdkPath


if __name__ == "__main__":

    main_dir = os.environ["PROACTIVESS"]
    file_name = main_dir + "/resource/pid.tmp"

    if len(sys.argv) == 2:
        comm = sys.argv[1]
        print "command : " + comm
	if comm == "clean":
	   
	    if os.path.exists( file_name ):
                os.remove(file_name)
                print "removed : " + file_name + "."
        elif comm == "start":
            if os.path.exists( file_name ):
                print "Impossible to execute several instances of ProActive server."
                exit(1)
            
            else:
            	
            	f = open(file_name, 'w')
            	f.write("Server PID = " + str(os.getpid()) )
            	f.close()
            
                server = Server()

                # Activate the server; this will keep running until you
                # interrupt the program with Ctrl-C
                server.listen()
        else:
            print "Help:"
            print ""
            print "start : launch an instance of server."
            print "clean : remove pid.tmp in order to launch another instance of server."
            print "help : print this help menu."
            
    else:
        print "Help:"
        print ""
        print "start : launch an instance of server."
        print "clean : remove pid.tmp in order to launch another instance of server."
        print "help : print this help menu."
