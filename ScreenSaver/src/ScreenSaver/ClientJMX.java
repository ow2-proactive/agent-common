/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ScreenSaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL; 

/**
 *
 * @author pgouttef
 */
public class ClientJMX {
    
    /**
     * System and JVM informations.
     */
    private String operatingSystem;
    private String startTime;
    private String JVMName;
    private long memHeap;
    private long memNonHeap;
    private String currentTask = "None";
    private long totalMemory;
    private long freeMemory;
    private long totalSwap;
    private long freeSwap;
    
    /**
     * data structure to store memory informations.
     */
    ArrayList<ChartData> datas;

    public String getJVMName() {
        return JVMName;
    }

    public String getCurrentTask() {
        return currentTask;
    }

    public long getMemHeap() {
        return memHeap;
    }

    public long getMemNonHeap() {
        return memNonHeap;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getStartTime() {
        return startTime;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getFreeSwap() {
        return freeSwap;
    }

    public long getTotalSwap() {
        return totalSwap;
    }

    public ArrayList<ChartData> getDatas() {
        return datas;
    }
    
    /**
     * Test if parameter is a Long or Not
     * @param input
     * @return 
     */
    private boolean isLong( String input ) {
        
        try
        {
            Long.parseLong(input);
            return true;
        }
        catch( Exception e )
        {
            return false;
        }
    }
    
    /**
     * Return system information
     * @return The memory used currently by the system.
     */
    private long getMemoryUsed() {
        
        try {
                        
            Process p = Runtime.getRuntime().exec("free -m");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            input.readLine();input.readLine();
            String output = input.readLine();

            String[] tab = output.replaceAll("\\s+", " ").split(" ");
            input.close();
            
            for (String string : tab) {
                if(isLong(string)) {
                    return Long.parseLong(string);
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ClientJMX.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 0;
    }
    
    /**
     * Run jmx listenner
     * @return True if the listen has runned, False if not.
     */
    public boolean runJmx(String dataFile) {
        
        datas = FileDescriptor.readFile(dataFile);

        try {
            JMXServiceURL url =
                new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");
            
            JMXConnection conn = new JMXConnection(url);
            JMXConnector jmxc = conn.getConnection();
            
            if(jmxc == null) {
                System.out.println("1");
                return false;
            }

            // Get an MBeanServerConnection
            //
            MBeanServerConnection mBeanServConn = jmxc.getMBeanServerConnection();
            
            List<MemoryPoolMXBean> memPoolMXBean = ManagementFactory.getMemoryPoolMXBeans();
            
            RuntimeMXBean mBeanRuntime = ManagementFactory.newPlatformMXBeanProxy(mBeanServConn,
                                    ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
            
            OperatingSystemMXBean mBeanOpSystem = ManagementFactory.newPlatformMXBeanProxy(mBeanServConn,
                                    ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
            
            operatingSystem = mBeanOpSystem.getName() + " " + mBeanOpSystem.getVersion() + " " + mBeanOpSystem.getArch(); 
            startTime = new Date(mBeanRuntime.getStartTime()) + "";
            JVMName = mBeanRuntime.getVmName();
            
            com.sun.management.OperatingSystemMXBean mxbean = (com.sun.management.OperatingSystemMXBean)sun.management.ManagementFactory.getOperatingSystemMXBean();

            long memHeapTmp = 0, memNonHeapTmp=0;
            for (MemoryPoolMXBean memoryPoolMXBean : memPoolMXBean) {
                    if(memoryPoolMXBean.getType().toString().equals("Heap memory")) {
                            memHeapTmp+=memoryPoolMXBean.getUsage().getUsed();
                    } else {
                            memNonHeapTmp+=memoryPoolMXBean.getUsage().getUsed();
                    }
            }
            this.totalMemory = mxbean.getTotalPhysicalMemorySize() / (1024*1024);
            this.freeMemory = totalMemory - getMemoryUsed();
            this.totalSwap = mxbean.getTotalSwapSpaceSize() / (1024*1024);
            this.freeSwap = mxbean.getFreeSwapSpaceSize() / (1024*1024);
            this.memHeap = memHeapTmp / (1024*1024);
            this.memNonHeap = memNonHeapTmp / (1024*1024);

            ChartData ch = new ChartData(totalMemory-freeMemory, totalSwap-freeSwap);
            datas.add(ch);
            
            FileDescriptor.writeFile(datas , dataFile);

            Set<ObjectName> names =
                new TreeSet<ObjectName>(mBeanServConn.queryNames(null, null));

            for (ObjectName name : names) {
                    //echo("toString : " + name.toString());
                    if(name.toString().startsWith("org.objectweb.proactive.core.body")) {
                            int index = mBeanServConn.getAttribute( name, "Name").toString().lastIndexOf(".") + 1;
                            currentTask = (String)mBeanServConn.getAttribute( name, "Name").toString().substring(index);
                    }
            }

            if(conn.closeConnection() == false) {
                System.out.println("2");
                return false;
            }
            
            return true;
            
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static void main(String[] args) {
        String output = "";
        try {
                        
            Process p = Runtime.getRuntime().exec("free -m");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            input.readLine();input.readLine();
            output = input.readLine();

            String[] tab = output.replaceAll("\\s+", " ").split(" ");
            
            input.close();
            
            System.out.println(output);
            System.out.println(tab[2]);
            
        } catch (IOException ex) {
            Logger.getLogger(ClientJMX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
