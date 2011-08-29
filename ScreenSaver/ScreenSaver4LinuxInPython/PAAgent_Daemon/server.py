#!/usr/bin/env python

import SocketServer
from model import Model
import commands

class MyTCPHandler(SocketServer.BaseRequestHandler):
    """
    The RequestHandler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """
    
    start = 'START'
    stop = 'STOP'
    shutdown = 'SHUTDOWN'
    answer = 'receive message by : '

    def handle(self):
        # self.request is the TCP socket connected to the client
        self.data = self.request.recv(1024).strip()
        print "%s wrote:" % self.client_address[0]
        print self.data
        
        tab = self.data.split()
        
        comm, username , current_time = 'tmp', 'tmp' , 'tmp'
        if len(tab) == 2:
            comm = tab[0]
            username = tab[1]
            current_time = commands.getstatusoutput( 'date' )
            
        elif len(tab) == 1:
            comm = tab[0]
        
        mess = self.answer + username + ' at ' + current_time[1]
        
        if comm == self.start :
            self.startJVM(username)
            
        elif comm == self.stop:
            self.stopJVM(username)
            
        elif comm == self.shutdown:
            self.shutDown()

        # just send back the same data, but upper-cased
        self.request.send(mess)
        
    def startJVM(self , username):
        Model().launcher('startJVM' , username)
            
    def stopJVM(self , username):
        Model().launcher('stopJVM' , username)
        
    def shutDown(self):
        self.finish()

if __name__ == "__main__":
    HOST, PORT = "localhost", 9999

    # Create the server, binding to localhost on port 9999
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)

    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    server.serve_forever()
