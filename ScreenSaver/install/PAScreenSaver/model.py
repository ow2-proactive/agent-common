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

import login
import commands
import os
from subprocess import Popen
import signal
import time

  
# The model of the Daemon, contain all functions.
class Model:
    
    MAIN_DIR = os.environ["PROACTIVESS"]

    config_file = 'log/server.txt'
    pid = 0
    
    nbClient = 0
    clientLog = "Nb client(s) is now : "
    startLog = "Starting java proactive screensaver... \n"
    stopLog = "Stopping java proactive screensaver... \n"
    killLog = "Killing process : "
    killLogError = "Unable to kill JVM.\n"

    classpath = "lib/log4j-1.2.16.jar:lib/rrd4j-2.0.7.jar:FullScreenSaver.jar:"
    jdk_path = "tmp"

    def init(self,jdk_path):
	if self.MAIN_DIR[-1:] == "/":
	    self.MAIN_DIR = self.MAIN_DIR[:-1]

        if jdk_path[-1:] == "/":
	    jdk_path = jdk_path[:-1]

        self.jdk_path = jdk_path
	self.writeLOG("ProActiveScreenSaver directory set as : " + self.MAIN_DIR + " at " + time.ctime() + "\n")
        self.classpath = self.classpath + jdk_path + "/../lib/tools.jar"
        print "classpath : " + self.classpath

    def launcher(self,command, x=0 , y=0 , java_path=''):
        log = login.Login()
        
        if command == 'startJVM':
            #if not log.checkSession():
            #    print 'access denied'
            #else:
            print 'startJVM'
            self.startJVM(x,y)

        if command == 'stopJVM':
            print 'stopJVM'
            self.stopJVM()
                
    def startJVM(self,x,y):
        #log part
        line = 'started JVM at : ' + time.ctime() + ' with Server/Proxy \n'
        self.writeLOG(line)
        
        self.nbClient = self.nbClient + 1
        print "nb client : " + str(self.nbClient)
        self.writeLOG(self.clientLog + str(self.nbClient) +  "\n")
        
        #if it is the first client, application launch .jar
        if self.nbClient == 1:
        
	    #launcher java part
	    cmd = self.jdk_path + "/java -cp " + self.classpath + " screensaver.ScreenSaver /tmp/ScreenSaver.bmp /tmp/dataBase.rrd "
	    cmd = cmd + x + " " + y + " " + self.MAIN_DIR + "/log/log4j.properties"
	    print "command : " + cmd
            self.writeLOG("command : " + cmd + "\n")
	    pid = Popen(cmd, shell=True).pid
		
	    self.pid = (pid + 1)
	    
	    self.writeLOG(self.startLog)
    
    def stopJVM(self):
    	#log part
        line = 'stopped JVM at : ' + time.ctime() +  ' with Server/Proxy \n'
        self.writeLOG(line)
        
        self.nbClient = self.nbClient - 1
        if self.nbClient < 0:
        	self.nbClient = 0
        print "nb client : " + str(self.nbClient)
        self.writeLOG(self.clientLog + str(self.nbClient) +  "\n")
        
        #if it was the last client, application kill .jar
        if self.nbClient == 0:
	    try :
            	os.kill( self.pid , signal.SIGTERM)
            	print "kill : " + str(self.pid)
            
	        self.writeLOG(self.killLog + str(self.pid) + "\n")
	        self.writeLOG(self.stopLog)
	    except :
		print "There is no process catched for : " + str(self.pid)
                self.writeLOG(self.killLogError)
		
            
       
    def writeLOG(self, txt):
    	f = open( self.MAIN_DIR + "/" + self.config_file ,'a')
        f.write( txt )
        f.close() 
        
    def getPID(self):
        print "PID : " + str(self.pid)
    
    
