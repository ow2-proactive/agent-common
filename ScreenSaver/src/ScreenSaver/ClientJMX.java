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
package ScreenSaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
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

    /**
     * return the JVM name focus by JMX.
     * @return the JVMName object.
     */
    public String getJVMName() {
        return JVMName;
    }

    /**
     * return the current task type (mainly : Native or Java).
     * @return the CurrentTask object.
     */
    public String getCurrentTask() {
        return currentTask;
    }

    /**
     * return the memory heap size in Mo.
     * @return the MemHeap object.
     */
    public long getMemHeap() {
        return memHeap;
    }

    /**
     * return the non memory heap size in Mo.
     * @return the MemNonHeap object.
     */
    public long getMemNonHeap() {
        return memNonHeap;
    }

    /**
     * return the operating system descriptor.
     * @return OperatingSystem object.
     */
    public String getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * return the start time of the JVM focus by JMX.
     * @return a string based on Date format.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * return the free memory of system in Mo.
     * @return the FreeMemory object.
     */
    public long getFreeMemory() {
        return freeMemory;
    }

    /**
     * return the physical memory of the system in Mo
     * @return the TotalMemory object.
     */
    public long getTotalMemory() {
        return totalMemory;
    }

    /**
     * return the free swap of system in Mo.
     * @return the FreeSwap object.
     */
    public long getFreeSwap() {
        return freeSwap;
    }

    /**
     * return the physical swap of the system in Mo
     * @return the TotalMemory object.
     */
    public long getTotalSwap() {
        return totalSwap;
    }

    /**
     * return an array which contain the series, loaded on a specific file in /tmp
     * The first element is the timestamp of the event, and other values some informations, exemple : RAM value.
     * @return an array of ChartData objects.
     */ 
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
     * Return a system information
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
     * Run JMX listener
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
            long mem = getMemoryUsed();
            if (mem == 0) return false;
            this.freeMemory = totalMemory - mem;
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
