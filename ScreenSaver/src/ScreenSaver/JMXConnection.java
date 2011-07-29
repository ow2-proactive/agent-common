/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ScreenSaver;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 *
 * @author pgouttef
 */
public class JMXConnection {
    
    private JMXServiceURL url;
    JMXConnector conn = null;
    
    public JMXConnection(JMXServiceURL url) {
        this.url = url;
    }
    
    public JMXConnector getConnection() {
        try {
            conn = JMXConnectorFactory.connect(url, null);
            return conn;
        } catch (IOException ex) {
            Logger.getLogger(JMXConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public boolean closeConnection() {
        if(conn!=null) {
            try {
                conn.close();
                return true;
            } catch (IOException ex) {
                Logger.getLogger(JMXConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        return false;
    }
    
}
