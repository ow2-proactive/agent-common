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
from subprocess import Popen
import signal
import time

  
# The model of the Daemon, contain all functions.
class Model:
    
    MAIN_DIR = os.environ["PROACTIVESS"]

    config_file = 'log.txt'
    pid = 0
    
    nbClient = 0
    clientLog = "Nb client(s) is now : "
    startLog = "Starting java proactive screensaver... \n"
    stopLog = "Stopping java proactive screensaver... \n"
    killLog = "Killing process : "
    
    def init(self):
	if self.MAIN_DIR[-1:] == "/":
	    self.MAIN_DIR = self.MAIN_DIR[:-1]
	self.writeLOG("ProActiveScreenSaver directory set as : " + self.MAIN_DIR + " at " + time.ctime())

    def launcher(self,command,user, x=0 , y=0 , java_path=''):
        log = login.Login()
        
        if log.checkAccess(user , self.MAIN_DIR):
            if command == 'startJVM':
                #if not log.checkSession(user):
                #    print 'access denied' 
                #else:
                print 'startJVM'
                self.startJVM(user,x,y,java_path)
                
            if command == 'stopJVM':
                print 'stopJVM'
                self.stopJVM(user)
                
        else:
            print 'access denied' 
    
        
    def startJVM(self,user,x,y,jdk_path):
        #log part
        line = user + ' started JVM at : ' + time.ctime() + ' with Server/Proxy \n'
        self.writeLOG(line)
        
        self.nbClient = self.nbClient + 1
        print "nb client : " + str(self.nbClient)
        self.writeLOG(self.clientLog + str(self.nbClient) +  "\n")
        
        #if it is the first client, application launch .jar
        if self.nbClient == 1:
        
	    #launcher java part
	    cmd = jdk_path + "/java -jar " + self.MAIN_DIR + "/FullScreenSaver.jar /tmp/ScreenSaver.bmp /tmp/dataBase.rrd "
	    cmd = cmd + x + " " + y
	    print "command : " + cmd
	    pid = Popen(cmd, shell=True).pid
		
	    self.pid = (pid + 1)
	    
	    self.writeLOG(self.startLog)
    
    def stopJVM(self,user):
    	#log part
        line = user + ' stopped JVM at : ' + time.ctime() +  ' with Server/Proxy \n'
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
		
            
       
    def writeLOG(self, txt):
    	f = open( self.MAIN_DIR + "/" + self.config_file ,'a')
        f.write( txt )
        f.close() 
        
    def getPID(self):
        print "PID : " + str(self.pid)
    
    