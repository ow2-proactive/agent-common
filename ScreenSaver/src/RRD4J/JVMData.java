/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RRD4J;

import javax.management.remote.JMXConnector;

/**
 *
 * @author pgouttef
 */
public class JVMData {
    
    private String name;
    private int PID;
    private double memHeap;
    private double memNonHeap;
    private JMXConnector connector;
    
    public JVMData() {
        
    }
    
    public JVMData(String name, int PID, double memHeap, double memNonHeap , JMXConnector connector) {
        this.name = name;
        this.PID = PID;
        this.memHeap = memHeap;
        this.memNonHeap = memNonHeap;
        this.connector = connector;
    }
    
    public double getTotalMemory() {
        return memHeap + memNonHeap;
    }

    public int getPID() {
        return PID;
    }

    public String getName() {
        return name;
    }
    
    public JMXConnector getConnector() {
        return connector;
    }
}
