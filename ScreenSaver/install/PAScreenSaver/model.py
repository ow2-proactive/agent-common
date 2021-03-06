  
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

import commands
import os
from subprocess import Popen
import signal
import time

  
# The model of the Daemon, contain all functions.
class Model:
    
    #path of proactiveSS application 
    MAIN_DIR = "/usr/bin/ProActiveScreenSaver"

    #log path of server
    config_file = 'log/server.txt'

    #Current PID of screensaver.jar if it executed
    pid = 0
    
    #Current number of client
    nbClient = 0

    #log sentences..
    clientLog = "Nb client(s) is now : "
    startLog = "Starting java proactive screensaver... "
    stopLog = "Stopping java proactive screensaver... "
    killLog = "Killing process : "
    killLogError = "Unable to kill JVM."
    javaLogError = "Impossible to run Java command, bad syntax."

    #classpath of java execution
    classpath = "lib/log4j-1.2.16.jar:lib/rrd4j-2.0.7.jar:ScreenSaver.jar:"
    jdk_path = "tmp"

    # initialize jdk path and main directory path
    def init(self,jdk_path):

        # MAIN DIR PATH
	if self.MAIN_DIR[-1:] == "/":
	    self.MAIN_DIR = self.MAIN_DIR[:-1]

        # JDK PATH
        if jdk_path[-1:] == "/":
	    jdk_path = jdk_path[:-1]

        self.jdk_path = jdk_path
        self.classpath = self.classpath + jdk_path + "/../lib/tools.jar"

    # main method.
    # 
    #   receive command from server.py
    #       startJVM:
    #                   - Add a client to the counter
    #                   - If it is the first, launch java application
    #
    #       stopJVM:
    #                   - Sub a client to the counter
    #                   - If it was the last, shutdown java application
    #
    def launcher(self,command, x=0 , y=0 , java_path=''):
        
        if command == 'startJVM':
            self.startJVM(x,y)

        if command == 'stopJVM':
            self.stopJVM()
        
    # The start Java method
    def startJVM(self,x,y):
        
        #log part
        line = 'START signal received'
        self.writeLOG(line)
        #***********************************

        #Clients counter manager
        self.nbClient = self.nbClient + 1
        print "nb client : " + str(self.nbClient)
        self.writeLOG(self.clientLog + str(self.nbClient))
        #***********************************
        
        #if it is the first client, application launch .jar
        if self.nbClient == 1:
        
	    #launcher java part
	    cmd = self.jdk_path + "/java -cp " + self.classpath + " screensaver.ScreenSaver /tmp/ScreenSaver.bmp "
	    cmd = cmd + self.MAIN_DIR + "/database.rrd " + x + " " + y + " " + self.MAIN_DIR + "/log/log4j.properties"
	    print "command : " + cmd
        self.writeLOG("command : " + cmd)
	    
        try:
            pid = Popen(cmd, shell=True).pid
            #get back PID of java process
            self.pid = (pid + 1)
	        
            self.writeLOG(self.startLog)

        except OSError:
            print self.javaLogError
            self.writeLOG(self.javaLogError)
        #***********************************
    
    # The stop Java method
    def stopJVM(self):
    	
        #log part
        line = 'STOP signal received'
        self.writeLOG(line)
        #***********************************
        
        #Clients counter manager
        self.nbClient = self.nbClient - 1
        if self.nbClient < 0:
        	self.nbClient = 0
        print "nb client : " + str(self.nbClient)
        self.writeLOG(self.clientLog + str(self.nbClient))
        #***********************************
        
        #if it was the last client, application kill .jar
        if self.nbClient == 0:
	    try :
        	os.kill( self.pid , signal.SIGTERM)
        	print "kill : " + str(self.pid)
            
        	self.writeLOG(self.killLog + str(self.pid))
        	self.writeLOG(self.stopLog)
	    except :
		    print "There is no process catched for : " + str(self.pid)
            self.writeLOG(self.killLogError)
	#***********************************
            
       
    # log method
    def writeLOG(self, txt):
    	f = open( self.MAIN_DIR + "/" + self.config_file ,'a')
        f.write( time.ctime() + " : " + txt + "\n" )
        f.close() 
    
    
