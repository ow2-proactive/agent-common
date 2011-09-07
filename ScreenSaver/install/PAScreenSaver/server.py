#!/usr/bin/env python

import SocketServer
from model import Model
import commands
import os
import sys
import time
import stat
import ConfigParser

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
    config_file = 'configSS.txt'

    model = Model()

    #JAVA MIN
    java_version_required = "A Java version 1.6 is required at least."
    java_version = 1
    java_subversion = 6 
    java_path = ""


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
	
	if len(self.java_path) > 1 :
		self.model.writeLOG("Java founded : " + self.java_path + "\n")		
			
		'''
			IF IT'S GOOD, RUN THE LISTENNER
		'''
		print "Starting listenning..."
		self.model.init()

		os.chmod(self.pipe_path, stat.S_IRWXU | stat.S_IWGRP | stat.S_IWOTH)
		pipe = open(self.pipe_path, 'r')
		self.model.writeLOG("open pipe in read mode.\n")
		while True:

			self.data = pipe.read()

			print "Server has received : %s" % self.data

			tab = self.data.split()

			if len(tab) == 4:
			    self.comm = tab[0]
			    self.username = tab[1]
			    self.x = tab[2]
			    self.y = tab[3]
			    
			    if self.comm == self.start :
				self.startJVM(self.username,self.x,self.y,self.java_path)
			    
			    elif self.comm == self.stop:
				self.stopJVM(self.username)
			    
			    elif self.comm == self.shutdown:
				self.shutDown()
			    
			time.sleep(1)   
	else:
		print self.java_version_required
        
    def startJVM(self , username , x , y , java_path):
        self.model.launcher('startJVM' , username , x , y , java_path)
            
    def stopJVM(self , username):
        self.model.launcher('stopJVM' , username)
        
    def shutDown(self):
        self.finish()
        
    def checkJAVA_HOME(self): 
        config = ConfigParser.RawConfigParser()
        
        config.read( self.MAIN_DIR + "/" + self.config_file )
        conf = config.items('SCREENSAVER-CONFIG')

    	jdkPath = conf[0][1]
	print "jdk path : " + jdkPath 
	self.java_path = jdkPath


if __name__ == "__main__":

    if len(sys.argv) == 2:
        comm = sys.argv[1]
	if comm == "clean":
	   
	   
	    pipe_path = "/tmp/ss.pipe" 
	    pipe = open(pipe_path, 'w')
	    pipe.close()
	    os.remove(pipe_path)
	    print "named pipe file : " + pipe_path + " has removed."
    else:
	    server = Server()

	    # Activate the server; this will keep running until you
	    # interrupt the program with Ctrl-C
	    server.listen()
