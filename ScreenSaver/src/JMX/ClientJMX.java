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
package JMX;

import Model.Model;
import RRD4J.DataBase;
import RRD4J.DataBaseRrd4j;
import Util.utils;
import com.sun.management.OperatingSystemMXBean;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

/**
 *
 * @author philippe Gouttefarde
 */
public class ClientJMX {
    
    private String dataBaseFile;
    
    // database manager
    private DataBase rrd4j = new DataBaseRrd4j();
    
    // the JVMs detector and connector
    private JVMDetector JVMdetector = new JVMDetector();
    
    // Local CPU usage compute
    private long nanoBefore,nanoAfter;
    private OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    
    /**
     * parameter constructor
     * @param dataFile the rrd4j file path
     */
    public ClientJMX(String dataFile) {
        
        dataBaseFile = dataFile;
        
        Model.setCpuBefore( osBean.getProcessCpuTime() );
        nanoBefore = System.nanoTime();
        
        ArrayList<String> JVMds = ((DataBaseRrd4j)rrd4j).init(dataFile);
        scan();
        
        Model.setJVMList( utils.synchronization(Model.getJVMs(), JVMds, dataFile) );  
        
        ((DataBaseRrd4j)rrd4j).majInit(Model.getJVMs());
    }
    
    /**
     * return the database manager
     * @return rrd4j manager
     */
    public DataBase getDataBase() {
        return rrd4j;
    }
    
    /**
     * Test if parameter is a Long or Not.
     * @param str a String.
     * @return true is the string is a number, false if not.
     */
    private boolean isLong( String str ) {
        
        try
        {
            Long.parseLong(str);
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
     * debug method to display a String tab
     * @param tab the tab to display
     */
    private void displayTab(String[] tab) {
        System.out.println("\ntab : ");
        for (String string : tab) {
            System.out.print(string + " ");
        }
    }
    
    /**
     * This method parses the complete name of a JVM and return the executable name .
     * (it removes the path and optional parameters).
     * @param name the origin name to parse.
     * @return the final name.
     */
    private String parseName(String name) {

        String value;
        String[] n = name.split(" ");
        if(n[0].contains( System.getProperty("file.separator") )) {
            n = n[0].split( System.getProperty("file.separator") );
            n = n[n.length -1].split("\\.");
            
            if(n[n.length-1].equals("jar") || n[n.length-1].equals("JAR")) {
                //System.out.println("return : " + n[n.length-2] + ".jar");
                value = n[n.length-2] + ".jar";
            } else {
                //System.out.println("return : " + n[n.length-1]);
                value = n[n.length-1];
            }
            
        } else {
            n = n[0].split("\\.");
            if(n[n.length-1].equals("jar") || n[n.length-1].equals("JAR")) {
                //System.out.println("return : " + n[n.length-2] + ".jar");
                value =  n[n.length-2] + ".jar";
            } else {
                //System.out.println("return : " + n[n.length-1]);
                value =  n[n.length-1];
            }
        }
        
        if(value.length() > 20) {
            value = value.substring(0, 20);
        }
        return value;
    }
    
    /**
     * This method updates database with the news informations system.
     */
    private void updateRrd4j() {
        
        /**
         * Set system values.
         */
        nanoAfter = System.nanoTime();
        double CPU;
        double cpuAfter;
        if(nanoAfter > nanoBefore) {
            cpuAfter = Model.getCpuJVMUsage();
            CPU = ((cpuAfter-Model.getCpuBefore())*100)/(nanoAfter - nanoBefore);
        } else {
            cpuAfter = 0;
            CPU = 0F;
        }
        
        if(CPU > 100 || CPU < 0){
            CPU = 0;
        }
        Model.setCpuUsage(CPU);
        Model.setCpuBefore(cpuAfter);
        nanoBefore = nanoAfter;
        
        double RAM = Model.getMemHeap() + Model.getMemNonHeap();
        RAM /= Model.getTotalMemory();
        RAM *= 100;
        
        if(RAM > 100 || RAM < 0) {
            RAM = 0;
        }
        
        String[] systemDb = new String[2];
        systemDb[0] = "JVMs Memory usage";
        systemDb[1] = "JVMs CPU usage";
        
        double[] systemValue = new double[2];
        systemValue[0] = RAM;
        systemValue[1] = CPU;
        
        System.out.println("CPU : " + CPU + ", RAM : " + RAM);
        
        /**
         * Set JVM names.
         */
        System.out.println("JVMs size : " + Model.getJVMs().size());
        
        String[] JVMDb = new String[Model.getJVMs().size()];
        for (int i = 0; i < Model.getJVMs().size() && i < 10 ; i++) {
            JVMDb[i] =    utils.getHashCode(Model.getJVMs().get(i).getName() + " " 
                        + Model.getJVMs().get(i).getPID() + " " 
                        + Model.getJVMs().get(i).getStartTime());
        }
        rrd4j.addValue(dataBaseFile, systemDb, systemValue , JVMDb , Model.getMemoryTab());
    }

    /**
     * get system informations.
     * @return true if all is good, false if not.
     */
    private boolean getSystemInformations() {
        try {
                /**
                 * get Defaults informations on system.
                 */
                OperatingSystemMXBean mxbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                Model.setTotalMemory(mxbean.getTotalPhysicalMemorySize() / (1024*1024));
                long mem = getMemoryUsed();
                if (mem == 0) {
                    return false;
                }
                
                Model.setFreeMemory(Model.getTotalMemory() - mem);
                Model.setTotalSwap(mxbean.getTotalSwapSpaceSize() / (1024*1024));
                Model.setFreeSwap(mxbean.getFreeSwapSpaceSize() / (1024*1024));
                
                Model.setOperatingSystem(System.getProperty("os.name") + " " + 
                                        System.getProperty("os.arch") + " " + 
                                        System.getProperty("os.version"));
                Model.setHostName(InetAddress.getLocalHost().getHostName());
                
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientJMX.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("debug : getSystemInformations()");
        }
        
        return true;
    }
    
    /**
     * Update the rrd4j database with some memory informations.
     * @param index JVM index in the Model.JVM Arraylist
     * @param conn the jmx connector
     * @param PID the process PID
     * @param name the process name
     * @return true if the update has done, false else.
     */
    private boolean updateJVM(int index , JMXConnector conn , int PID , String name) {
        
        if(conn != null) {
            try {
                double memHeap = 0, memNonHeap = 0; 
                
                // Get an MBeanServerConnection
                MBeanServerConnection mBeanServConn = conn.getMBeanServerConnection();

                RuntimeMXBean mBeanRuntime = ManagementFactory.newPlatformMXBeanProxy(mBeanServConn,
                                        ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);

                MemoryMXBean memBean = ManagementFactory.newPlatformMXBeanProxy(mBeanServConn, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class ); 

                Model.addToHeap( memBean.getHeapMemoryUsage().getUsed() );
                Model.addToNonHeap( memBean.getNonHeapMemoryUsage().getUsed() );

                OperatingSystemMXBean osMxBean = (OperatingSystemMXBean) ManagementFactory.newPlatformMXBeanProxy(mBeanServConn,
                                        ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
                /*
                 * get back some information
                 */
                if(mBeanRuntime != null && osMxBean != null) {

                    if(!name.equals("")){

                        memHeap = memBean.getHeapMemoryUsage().getUsed();
                        memHeap = (memHeap/Model.getTotalMemory()) * 100;
                        memHeap /= 1024*1024;
                        
                        memNonHeap = memBean.getNonHeapMemoryUsage().getUsed();
                        memNonHeap = (memNonHeap/Model.getTotalMemory()) * 100;
                        memNonHeap /= 1024*1024;
                        
                        /*
                         * Update JVMs ArrayList
                         */
                        Model.setJVM(index, new JVMData(name, PID, mBeanRuntime.getStartTime(), memHeap, memNonHeap, osMxBean.getProcessCpuTime(), conn));
                    } 
                }

                /*
                 * Check if there are any tasks in JVM.
                 */
                Set<ObjectName> names =
                    new TreeSet<ObjectName>(mBeanServConn.queryNames(null, null));

                /*
                 * check for a task name.
                 */
                for (ObjectName oName : names) {
                        if(name.toString().startsWith("org.objectweb.proactive.core.body")) {
                                int i = mBeanServConn.getAttribute( oName, "Name").toString().lastIndexOf(".") + 1;
                                Model.setCurrentTask((String)mBeanServConn.getAttribute( oName, "Name").toString().substring(index));
                        }
                }
                
                return true;
            } catch (Exception ex) {
                System.out.println( ex.getMessage() );
            } 
        }
        return true;
    }
    
    /**
     * Test all connexions than we know.
     * Update the running connexions and remove the broken.
     */
    private boolean checkJVMAtStart() {
        
        boolean checked = false;
        
        for (int index=0 ; index < Model.getJVMs().size() ; ++index) {
            try {
                JMXConnector conn = Model.getJVMs().get(index).getConnector();
                
                if(conn != null) {
                   //Test if connection is broken or disconnect
                    if(conn.getConnectionId() != null ) {

                        updateJVM(index, conn, Model.getJVMs().get(index).getPID() , Model.getJVMs().get(index).getName());
                        checked = true;
                    } else {
                        System.out.println("Broken connection detected for : " + Model.getJVMs().get(index).getName());
                        Model.removeJVMByPID(Model.getJVMs().get(index).getPID());
                    } 
                } else {
                    System.out.println("Broken connection detected for : " + Model.getJVMs().get(index).getName());
                    Model.removeJVMByPID(Model.getJVMs().get(index).getPID());
                } 
                
            } catch (IOException ex) {
                System.out.println("Broken connection detected for : " + Model.getJVMs().get(index).getName());
                Model.removeJVMByPID(Model.getJVMs().get(index).getPID());
            }
        }
        
        return checked;
    }
    
    /**
     * Debug method to get current time.
     * @param str The sentence will be display before the time.
     */
    private void getTime(String str) {
        
        Calendar cal = Calendar.getInstance();
        String time = cal.get(Calendar.HOUR_OF_DAY)+"h "+cal.get(Calendar.MINUTE)+"m et "+cal.get(Calendar.SECOND)+"s";
        System.out.println(str + " : " + time);
    }
    
    /**
     * launch JMX scan, to detect JVMs
     * @return 
     */
    private boolean scan() {
        /*
         * Initialization.
         */
        boolean check = false;
        Model.resetMemory();
        JVMdetector.resetPidTab();

        /**
         * System informations
         */
        if(!getSystemInformations()) {
            return false;
        }
        
        /**
         * Check JVm already connected.
         */
        check = checkJVMAtStart();
        
        /**
         * Start JMX scanning.
         */
        JVMdetector.scan();
        for(int i = 0; i < JVMdetector.getPidTab().size() ; ++i) {
            String pid = JVMdetector.getPidTab().get(i);
            
            //Check if the JVM is already knew
            if(!Model.checkJVM( Integer.parseInt(pid) )) {
                JMXConnector jmxc = JVMdetector.getJMXConnector(pid);

                //If the jmx connector is activ, continue...
                if(jmxc != null) {

                    String name = JVMdetector.getProcesses().get(pid).toString();
                    name = parseName(name);
                    
                    //If we get a no empty name, register it
                    if(!name.equals("")) {
                        updateJVM(Model.addJVM(), jmxc, Integer.parseInt(pid) , name);
                        check = true;
                    } else {
                        try {
                            jmxc.close();
                        } catch (IOException ex) {
                            Logger.getLogger(ClientJMX.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        
        // compute total system memory used with JVM
        Model.setMemHeap( Model.getMemHeap() / (1024*1024));
        Model.setMemNonHeap( Model.getMemNonHeap() / (1024*1024));
        
        return check;
    }
    
    /**
     * Run JMX listener
     * @return True if the listen has ran, False if not.
     */
    public boolean runJmx() {
        
        //launch scan
        boolean check = scan();
        
        //update database
        updateRrd4j();

        return check;
    }
}