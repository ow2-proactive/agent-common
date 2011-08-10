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
package RRD4J;

import RRD4J.JVMDetector;
import RRD4J.ChartData;
import RRD4J.DataBase;
import RRD4J.DataBaseRrd4j;
import com.sun.management.OperatingSystemMXBean;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;

/**
 *
 * @author pgouttef
 */
public class ClientJMX {
    
    /**
     * System and JVM informations.
     */
    private String operatingSystem;
    private String hostName;
    private long memHeap;
    private long memNonHeap;
    private String currentTask = "None";
    private long totalMemory;
    private long freeMemory;
    private long totalSwap;
    private long freeSwap;
    private int nbJVM = 0;
    private BufferedImage img = null;
    private DataBase rrd4j = new DataBaseRrd4j();
    
    private JVMDetector JVMdetector = new JVMDetector();
    
    /**
     * data structure to store memory informations.
     */
    private ArrayList<String> JVMNames = new ArrayList<String>();
    private Color[] colorList = null;

    public ClientJMX() {
        colorList = new Color[10];
        
        colorList[0] = Color.BLACK;
        colorList[1] = Color.BLUE;
        colorList[2] = Color.CYAN;
        colorList[3] = Color.DARK_GRAY;
        colorList[4] = Color.GREEN;
        colorList[5] = Color.MAGENTA;
        colorList[6] = Color.ORANGE;
        colorList[7] = Color.RED;
        colorList[8] = Color.YELLOW;
        colorList[9] = Color.LIGHT_GRAY;
        
        /*for(int i = 0; i < 16 ; ++i) {
            JVMColors.set(i, new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()) );
        }*/
    }
    
    /**
     * return the JVM name focus by JMX.
     * @return the JVMName object.
     */
    public ArrayList<String> getJVMNames() {
        return JVMNames;
    }
    
    /**
     * return host name
     * @return the host name object.
     */
    public String getHostName() {
        return hostName;
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
     * return the number of JVM
     * @return the number of JVM.
     */
    public long getNbJVM() {
        return nbJVM;
    }
    
    /**
     * return the database manager
     * @return rrd4j manager
     */
    public DataBase getDataBase() {
        return rrd4j;
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
    
    private void displayTab(String[] tab) {
        System.out.println("\ntab : ");
        for (String string : tab) {
            System.out.print(string + " ");
        }
    }
    
    private String parseName(String name) {
        
        String[] n = name.split(" ");
        if(n[0].contains("/")) {
            n = n[0].split("/");
            n = n[n.length -1].split("\\.");
            
            if(n[n.length-1].equals("jar") || n[n.length-1].equals("JAR")) {
                //System.out.println("return : " + n[n.length-2] + ".jar");
                return n[n.length-2] + ".jar";
            } else {
                //System.out.println("return : " + n[n.length-1]);
                return n[n.length-1];
            }
            
        } else {
            n = n[0].split("\\.");
            if(n[n.length-1].equals("jar") || n[n.length-1].equals("JAR")) {
                //System.out.println("return : " + n[n.length-2] + ".jar");
                return n[n.length-2] + ".jar";
            } else {
                //System.out.println("return : " + n[n.length-1]);
                return n[n.length-1];
            }
        }
    }
    
    public void updateRrd4j(String path , double[] JVMMemory ) {
        
        /**
         * Initialization path.
         */
        int SYSTEM = ((DataBaseRrd4j)rrd4j).SYSTEM;
        int JVM = ((DataBaseRrd4j)rrd4j).JVM;
        
        if(!path.endsWith(System.getProperty("file.separator"))) {
            path += System.getProperty("file.separator");
        }
        
        String databasePath = path + "database.rrd";
        
        /**
         * Set system values.
         */
        Float RAM = (float)totalMemory;
        RAM -= (float)freeMemory;
        RAM /= (float)totalMemory;
        RAM *= (float)100;
        
        Float SWAP = (float)totalSwap;
        SWAP -= (float)freeSwap;
        SWAP /= (float)totalSwap;
        SWAP *= (float)100;
        
        String[] systemDb = new String[2];
        systemDb[0] = "RAM";
        systemDb[1] = "SWAP";
        
        double[] systemValue = new double[2];
        systemValue[0] = RAM;
        systemValue[1] = SWAP;
        
        /**
         * Set JVM values.
         */
        Random rand = new Random();
        String[] JVMDb = new String[this.JVMNames.size()];
        for (int i = 0; i < this.JVMNames.size() && i < 10 ; i++) {
            JVMDb[i] = this.JVMNames.get(i);
        }
        
        rrd4j.addValue(databasePath, systemDb, systemValue , JVMDb , JVMMemory , colorList);
    }
    
    /**
     * Run JMX listener
     * @return True if the listen has ran, False if not.
     */
    public boolean runJmx(String dataFile) {
        try {
            /**
             * Initialization.
             */
            long memHeapTmp = 0, memNonHeapTmp=0;
            boolean check = false;
            double[] JVMMemory = null;
            
            /**
             * get Defaults informations on system.
             */
            OperatingSystemMXBean mxbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            this.totalMemory = mxbean.getTotalPhysicalMemorySize() / (1024*1024);
            long mem = getMemoryUsed();
            if (mem == 0) {
                return false;
            }
            this.freeMemory = totalMemory - mem;
            this.totalSwap = mxbean.getTotalSwapSpaceSize() / (1024*1024);
            this.freeSwap = mxbean.getFreeSwapSpaceSize() / (1024*1024);
            
            this.operatingSystem =  System.getProperty("os.name") + " " + 
                                    System.getProperty("os.arch") + " " + 
                                    System.getProperty("os.version");
            this.hostName = InetAddress.getLocalHost().getHostName();
            
            
            /**
             * Start JMX scanning.
             */
            JVMdetector.scan();
            JVMMemory = new double[JVMdetector.getPidTab().size()];
            try {
                
                for(int i = 0; i < JVMdetector.getPidTab().size() ; ++i) {
                    
                    JMXConnector jmxc = JVMdetector.getJMXConnector(JVMdetector.getPidTab().get(i));
                    
                    if(jmxc != null) {
                        
                        //We have seen at least one JVM.
                        check = true;
                        nbJVM++;
                        
                        // Get an MBeanServerConnection
                        MBeanServerConnection mBeanServConn = jmxc.getMBeanServerConnection();

                        RuntimeMXBean mBeanRuntime = ManagementFactory.newPlatformMXBeanProxy(mBeanServConn,
                                                ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
                        
                        MemoryMXBean memBean = ManagementFactory.newPlatformMXBeanProxy(mBeanServConn, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class ); 

                        memHeapTmp += memBean.getHeapMemoryUsage().getUsed();
                        memNonHeapTmp += memBean.getNonHeapMemoryUsage().getUsed();
                        
                        JVMMemory[i] = memBean.getHeapMemoryUsage().getUsed() + memBean.getNonHeapMemoryUsage().getUsed();
                        JVMMemory[i] = (JVMMemory[i] / this.totalMemory) * 100;
                        JVMMemory[i] /= 1024*1024;
                        /*
                         * get back some information
                         */
                        if(mBeanRuntime != null) {
                            //System.out.println("****************************");
                            //System.out.println("name : " + parseName(JVMdetector.getProcesses().get( pid ).toString() ));
                            this.JVMNames.add( parseName(JVMdetector.getProcesses().get( 
                                    JVMdetector.getPidTab().get(i) ).toString() ));
                            //for (String arg : mBeanRuntime.getInputArguments()) {
                            //    System.out.println("arg : " + arg);
                            //}
                        }
                        
                        /*
                         * Check if there are any tasks in JVM.
                         */
                        Set<ObjectName> names =
                            new TreeSet<ObjectName>(mBeanServConn.queryNames(null, null));

                        for (ObjectName name : names) {
                                if(name.toString().startsWith("org.objectweb.proactive.core.body")) {
                                        int index = mBeanServConn.getAttribute( name, "Name").toString().lastIndexOf(".") + 1;
                                        currentTask = (String)mBeanServConn.getAttribute( name, "Name").toString().substring(index);
                                }
                        }
                        jmxc.close();
                    }
                }
                
                this.memHeap = memHeapTmp / (1024*1024);
                this.memNonHeap = memNonHeapTmp / (1024*1024);
                
                
                /*
                 * update RRD4J database
                 */
                updateRrd4j(dataFile , JVMMemory);
                
            } catch (MBeanException ex) {
                Logger.getLogger(ClientJMX.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (AttributeNotFoundException ex) {
                Logger.getLogger(ClientJMX.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (InstanceNotFoundException ex) {
                Logger.getLogger(ClientJMX.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (ReflectionException ex) {
                Logger.getLogger(ClientJMX.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (IOException ex) {
                Logger.getLogger(ClientJMX.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } 
            
            return check;
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientJMX.class.getName()).log(Level.SEVERE, null, ex);
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
