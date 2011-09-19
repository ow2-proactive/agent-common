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

import model.Model;
import rrd4j.DataBase;
import rrd4j.DataBaseRrd4j;
import util.utils;
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
import java.util.List;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;

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
    
    //Argument to check:
    String argToCheck = "-Dproactive.agent.rank=";

    private final static RootLogger logger = (RootLogger) Logger.getRootLogger();
    
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
            logger.error(ex);
        }
        
        return 0;
    }
    
    /**
     * debug method to display a String tab
     * @param tab the tab to display
     */
    private void displayTab(String[] tab) {
        logger.info("tab : ");
        for (String string : tab) {
             logger.info(string + " ");
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
                value = n[n.length-2] + ".jar";
            } else {
                value = n[n.length-1];
            }
            
        } else {
            n = n[0].split("\\.");
            if(n[n.length-1].equals("jar") || n[n.length-1].equals("JAR")) {
                value =  n[n.length-2] + ".jar";
            } else {
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
        
        /**
         * Set JVM names.
         */
        
        String[] JVMDb = new String[Model.getJVMs().size()];
        for (int i = 0; i < Model.getJVMs().size() && i < 10 ; i++) {
            JVMDb[i] =    utils.getHashCode(Model.getJVMs().get(i).getName() + " " 
                        + Model.getJVMs().get(i).getPID() + " " 
                        + Model.getJVMs().get(i).getStartTime());
            logger.info("JVM : " + JVMDb[i]);
        }
        
        /*
         * Add a new entry in database.
         */
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
            logger.error(ex);
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
        
        boolean checkJVM = false;
        if(conn != null) {
            try {
                double memHeap = 0, memNonHeap = 0; 
                
                // Get an MBeanServerConnection
                MBeanServerConnection mBeanServConn = conn.getMBeanServerConnection();

                RuntimeMXBean mBeanRuntime = ManagementFactory.newPlatformMXBeanProxy(mBeanServConn,
                                        ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);

                MemoryMXBean memBean = ManagementFactory.newPlatformMXBeanProxy(mBeanServConn, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class ); 

                OperatingSystemMXBean osMxBean = (OperatingSystemMXBean) ManagementFactory.newPlatformMXBeanProxy(mBeanServConn,
                                        ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
                /*
                 * get back some information
                 */
                if(mBeanRuntime != null && osMxBean != null) {

                    if(!name.equals("")){

                        /*
                         * Check agent's JVMs.
                         */
                        List<String> args = mBeanRuntime.getInputArguments();
                        for (String arg : args) {
                            if(arg.startsWith( argToCheck )) {
                            
                                Model.addToHeap( memBean.getHeapMemoryUsage().getUsed() );
                                Model.addToNonHeap( memBean.getNonHeapMemoryUsage().getUsed() );
                            
                                /*
                                 * Update JVMs ArrayList
                                 */
                                memHeap = memBean.getHeapMemoryUsage().getUsed();
                                memHeap = (memHeap/Model.getTotalMemory()) * 100;
                                memHeap /= 1024*1024;

                                memNonHeap = memBean.getNonHeapMemoryUsage().getUsed();
                                memNonHeap = (memNonHeap/Model.getTotalMemory()) * 100;
                                memNonHeap /= 1024*1024;

                                Model.setJVM(index, new JVMData(name, PID, mBeanRuntime.getStartTime(), memHeap, memNonHeap, osMxBean.getProcessCpuTime(), conn));
                                checkJVM = true;
                            }
                        }
                    } 
                }

                if(!checkJVM) {
                    Model.removeLastJVM();
                }
                
                return true;
            } catch (Exception ex) {
                logger.error(ex);
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
                        logger.info("Broken connection detected for : " + Model.getJVMs().get(index).getName());
                        Model.removeJVMByPID(Model.getJVMs().get(index).getPID());
                    } 
                } else {
                    logger.info("Broken connection detected for : " + Model.getJVMs().get(index).getName());
                    Model.removeJVMByPID(Model.getJVMs().get(index).getPID());
                } 
                
            } catch (IOException ex) {
                logger.info("Broken connection detected for : " + Model.getJVMs().get(index).getName());
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
        logger.info(str + " : " + time);
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
                            logger.error(ex);
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
