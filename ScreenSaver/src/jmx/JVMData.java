/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################ 
 * $$ACTIVEEON_CONTRIBUTOR$$
 */
package jmx;

import javax.management.remote.JMXConnector;

/**
 *
 * @author philippe Gouttefarde
 */
public class JVMData {
    
    /*
     * This class reprensent a connection from the executable to a remote JVM on the local system.
     */
    
    private String name;
    private int PID;
    private long startTime;
    private double memHeap;
    private double memNonHeap;
    private double cpu;
    private JMXConnector connector;
    
    /**
     * default constructor
     */
    public JVMData() {
        
    }
    
    /**
     * parameters constructor
     * @param name the JVM name
     * @param PID the JVM PID
     * @param memHeap the JVM memory Heap 
     * @param memNonHeap the JVM memory non Heap
     * @param connector  the JMX connector
     */
    public JVMData(String name, int PID, long startTime, double memHeap, double memNonHeap, double cpu , JMXConnector connector) {
        this.name = name;
        this.PID = PID;
        this.startTime = startTime;
        this.memHeap = memHeap;
        this.memNonHeap = memNonHeap;
        this.cpu = cpu;
        this.connector = connector;
    }
    
    /**
     * startTime getter
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * cpu usage getter
     * @return the cpu usage
     */
    public double getCPU() {
        return cpu;
    }
    
    /**
     * total memory getter
     * @return the addition of Heap and non Heap
     */
    public double getTotalMemory() {
        return memHeap + memNonHeap;
    }

    /**
     * PID getter
     * @return the PID
     */
    public int getPID() {
        return PID;
    }

    /**
     * name getter
     * @return the Name
     */
    public String getName() {
        return name;
    }
    
    /**
     * JMX connector getter
     * @return the connector
     */
    public JMXConnector getConnector() {
        return connector;
    }
}
