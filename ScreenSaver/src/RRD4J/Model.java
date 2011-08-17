/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RRD4J;

import java.awt.Color;
import java.util.ArrayList;
import javax.management.remote.JMXConnector;

/**
 *
 * @author pgouttef
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
    
    /**
     * data structure to store memory informations.
     */
    private static ArrayList<JVMData> JVMs = new ArrayList<JVMData>();
    private static Color[] colorList = null;

    static double[] getMemoryTab() {
        double[] mem = new double[ JVMs.size() ];
        for (int i = 0; i < mem.length ; ++i) {
            mem[i] = JVMs.get(i).getTotalMemory();
        }
        
        return mem;
    }
    
    /**
     * Concatenate at end of JVMName for identical names.
     */
    private int debugName = 0;

    public static ArrayList<JVMData> getJVMs() {
        return JVMs;
    }

    public static void setJVMs(ArrayList<JVMData> JVMs) {
        Model.JVMs = JVMs;
    }

    public static boolean addJVM(String name, int PID, double memHeap, double memNonHeap, JMXConnector conn) {
        
        if(Model.checkJVM(PID)) {
            System.out.println("Exist already : " + name);
            return false;
        }
        
        System.out.println("JVM added : " + name);
        JVMs.add(new JVMData(name, PID, memHeap, memNonHeap, conn));
        return true;
    }
    
    public static void setJVM(int index, JVMData data) {
        if(index < JVMs.size()) {
            JVMs.set(index, data);
        }
    }
    
    public static boolean checkJVM(int PID) {
        
        for(int i = 0; i < JVMs.size() ; ++i) {
            if(JVMs.get(i).getPID() == PID) {
                return true;
            }
        }
        return false;
    }
    
    public static void removeJVMByPID(int PID) {
        
        for(int i = 0; i < JVMs.size() ; ++i) {
            if(JVMs.get(i).getPID() == PID) {
                JVMs.remove(i);
            }
        }
    }
    
    public static int addJVM() {
        
        JVMs.add( new JVMData() );
        return (JVMs.size() - 1);
    }
    
    public static void displayJVmNames() {
        System.out.println("nb JVM : " + JVMs.size());
        for (JVMData data : JVMs) {
            System.out.println("\t" + data.getName());
        }
    }
    
    public static void addToHeap(long mem) {
        memHeap += mem;
    }
    
    public static void addToNonHeap(long mem) {
        memNonHeap += mem;
    }
    
    public static void resetMemory() {
        memHeap = 0;
        memNonHeap = 0;
    }
    
    public static Color[] getColorList() {
        return colorList;
    }

    public static void setColorList(Color[] colorList) {
        Model.colorList = colorList;
    }

    /**
     * return the current task type (mainly : Native or Java).
     * @return the CurrentTask object.
     */
    public static String getCurrentTask() {
        return currentTask;
    }

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

    public static void setTotalSwap(long totalSwap) {
        Model.totalSwap = totalSwap;
    }
    
}
