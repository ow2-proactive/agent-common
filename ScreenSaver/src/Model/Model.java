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
package Model;

import JMX.JVMData;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.management.remote.JMXConnector;

/**
 *
 * @author philippe Gouttefarde
 */
public class Model {
    
    /**
     * System and JVM informations.
     */
    private static String operatingSystem;
    private static String hostName;
    private static long memHeap;
    private static long memNonHeap;
    private static String currentTask = "None";
    private static long totalMemory;
    private static long freeMemory;
    private static long totalSwap;
    private static long freeSwap;
    private static double cpuBefore;
    private static double cpuUsage;
        
    /**
     * data structure to store memory informations.
     */
    private static ArrayList<JVMData> JVMs = new ArrayList<JVMData>();

    /**
     * return CPU usage value of total JVMs scanned
     * @return CPU usage
     */
    public static double getCpuUsage() {
        return cpuUsage;
    }

    /**
     * setter CPU usage of total JVMs scanned
     * @param cpuUsage CPU usage
     */
    public static void setCpuUsage(double cpuUsage) {

        System.out.println("cpu : " + cpuUsage);

        String tmp = new DecimalFormat("#0.00").format(cpuUsage);

        tmp = tmp.replace(',', '.');

        Model.cpuUsage = Double.parseDouble(tmp);
    }
    
    /**
     * compute all CPU value in JVMs list and return result
     * @return CPU usage
     */
    public static double getCpuJVMUsage() {
        double cpu = 0;
        
        for (JVMData jvm : JVMs) {
            cpu += jvm.getCPU();
        }
        return cpu;
    }
    
    
    /**
     * return the last value of CPU usage
     * @return last CPU usage
     */
    public static double getCpuBefore() {
        return cpuBefore;
    }

    
    /**
     * setter of last CPU usage
     * @param cpuBefore the last CPU value
     */
    public static void setCpuBefore(double cpuBefore) {
        Model.cpuBefore = cpuBefore;
    }

    
    /**
     * return the total memory of each JVM in a tab for the rrd4j database.
     * @return the total memory of each JVM in a tab for the rrd4j database.
     */
    static public double[] getMemoryTab() {
        double[] mem = new double[ JVMs.size() ];
        for (int i = 0; i < mem.length ; ++i) {
            mem[i] = JVMs.get(i).getTotalMemory();
        }
        
        return mem;
    }

    /**
     * return a JVM list.
     * @return a JVM list.
     */
    public static ArrayList<JVMData> getJVMs() {
        return JVMs;
    }

    /**
     * add a new JVM in the list. The method check the PID.
     * @param name the JVM name.
     * @param PID the JVM PID.
     * @param memHeap the JVM memory heap.
     * @param memNonHeap the JVM memory non heap.
     * @param conn the JMX connector of the JVM.
     * @return true if the JVM has been add, false if not.
     */
    public static boolean addJVM(String name, int PID, long startTime, double memHeap, double memNonHeap, double cpu, JMXConnector conn) {
        
        if(Model.checkJVM(PID)) {
            System.out.println("Exist already : " + name);
            return false;
        }
        
        System.out.println("JVM added : " + name);
        JVMs.add(new JVMData(name, PID, startTime , memHeap, memNonHeap, cpu, conn));
        return true;
    }
    
    /**
     * JVM setter
     * @param index the JVM index in the arraylist.
     * @param data the JVM.
     */
    public static void setJVM(int index, JVMData data) {
        if(index < JVMs.size()) {
            JVMs.set(index, data);
        }
    }
    
    /**
     * setter of JVMs complete list.
     * @param list 
     */
    public static void setJVMList( ArrayList<JVMData> list ) {
        JVMs = list;
    }
    
    /**
     * check the PID existing in the array list.
     * @param PID the PID to check.
     * @return true if it has been found, false if not.
     */
    public static boolean checkJVM(int PID) {
        
        for(int i = 0; i < JVMs.size() ; ++i) {
            if(JVMs.get(i).getPID() == PID) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Remove a specific JVM corresponding at he PID.
     * @param PID the JVM PID.
     */
    public static void removeJVMByPID(int PID) {
        
        for(int i = 0; i < JVMs.size() ; ++i) {
            if(JVMs.get(i).getPID() == PID) {
                JVMs.remove(i);
            }
        }
    }
    
    /**
     * create a new JVM a end of list, and return the new rank.
     * @return the new JVM rank.
     */
    public static int addJVM() {
        
        JVMs.add( new JVMData() );
        return (JVMs.size() - 1);
    }
    
    /**
     * debug method to display current JVMs.
     */
    public static void displayJVmNames() {
        System.out.println("nb JVM : " + JVMs.size());
        for (JVMData data : JVMs) {
            System.out.println("\t" + data.getName());
        }
    }
    
    /**
     * add value to current memory heap size.
     * @param mem the new value to add.
     */
    public static void addToHeap(long mem) {
        memHeap += mem;
    }
    
    /**
     * add value to current memory non heap size.
     * @param mem the new value to add.
     */
    public static void addToNonHeap(long mem) {
        memNonHeap += mem;
    }
    
    /**
     * reset memory Heap and non Heap to zero.
     * It's done before every scan.
     */
    public static void resetMemory() {
        memHeap = 0;
        memNonHeap = 0;
    }

    /**
     * return the current task type (mainly : Native or Java).
     * @return the CurrentTask object.
     */
    public static String getCurrentTask() {
        return currentTask;
    }

    /**
     * set the current task.
     * @param currentTask the current task name.
     */
    public static void setCurrentTask(String currentTask) {
        Model.currentTask = currentTask;
    }

    /**
     * return the free memory of system in Mo.
     * @return the FreeMemory object.
     */
    public static long getFreeMemory() {
        return freeMemory;
    }

    /**
     * free memory setter.
     * @param freeMemory the value to set.
     */
    public static void setFreeMemory(long freeMemory) {
        Model.freeMemory = freeMemory;
    }

    /**
     * return the free swap of system in Mo.
     * @return the FreeSwap object.
     */
    public static long getFreeSwap() {
        return freeSwap;
    }

    /**
     * free SWAP setter.
     * @param freeSwap the value to set.
     */
    public static void setFreeSwap(long freeSwap) {
        Model.freeSwap = freeSwap;
    }

    /**
     * return host name
     * @return the host name object.
     */
    public static String getHostName() {
        return hostName;
    }

    /**
     * host name setter.
     * @param hostName the value to set.
     */
    public static void setHostName(String hostName) {
        Model.hostName = hostName;
    }

    /**
     * return the memory heap size in Mo.
     * @return the MemHeap object.
     */
    public static long getMemHeap() {
        return memHeap;
    }

    /**
     * memory heap setter.
     * @param memHeap the value to set.
     */
    public static void setMemHeap(long memHeap) {
        Model.memHeap = memHeap;
    }

    /**
     * return the non memory heap size in Mo.
     * @return the MemNonHeap object.
     */
    public static long getMemNonHeap() {
        return memNonHeap;
    }

    /**
     * memory non heap to set.
     * @param memNonHeap the value to set.
     */
    public static void setMemNonHeap(long memNonHeap) {
        Model.memNonHeap = memNonHeap;
    }

    /**
     * return the operating system descriptor.
     * @return OperatingSystem object.
     */
    public static String getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * operating system setter.
     * @param operatingSystem the value to set.
     */
    public static void setOperatingSystem(String operatingSystem) {
        Model.operatingSystem = operatingSystem;
    }

    /**
     * return the physical memory of the system in Mo
     * @return the TotalMemory object.
     */
    public static long getTotalMemory() {
        return totalMemory;
    }

    
    /**
     * total memory setter.
     * @param totalMemory the value to set.
     */
    public static void setTotalMemory(long totalMemory) {
        Model.totalMemory = totalMemory;
    }

    /**
     * return the physical swap of the system in Mo
     * @return the TotalMemory object.
     */
    public static long getTotalSwap() {
        return totalSwap;
    }

    /**
     * total SWAP setter.
     * @param totalSwap the value to set.
     */
    public static void setTotalSwap(long totalSwap) {
        Model.totalSwap = totalSwap;
    }
    
}
