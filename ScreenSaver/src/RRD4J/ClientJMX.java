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

import com.sun.management.OperatingSystemMXBean;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    
    /**
     * Concatenate at end of JVMName for identical names.
     */
    private int debugName = 0;
    
    // the JVMs detector and connector
    private JVMDetector JVMdetector = new JVMDetector();
    
    /**
     * Default constructor. it initialize some colors for the charts.
     */
    public ClientJMX() {
        
    }

    public ClientJMX(String dataFile) {
        
        dataBaseFile = dataFile;
        
        /*
         * Initialisation of some colors.
         */
        Color[] colorList = new Color[10];
        
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
        
        Model.setColorList(colorList);
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
     * @param tab 
     */
    private void displayTab(String[] tab) {
        System.out.println("\ntab : ");
        for (String string : tab) {
            System.out.print(string + " ");
        }
    }
    
    /**
     * This method parses the complete name of a JVM and return the executable name 
     * (it removes the path and optional parameters)
     * @param name
     * @return 
     */
    private String parseName(String name) {
        
        String[] n = name.split(" ");
        if(n[0].contains( System.getProperty("file.separator"))) {
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
    
    /**
     * This method updates database with the news informations system.
     * @param path The path of the database in the system.
     * @param JVMMemory memory information of each JVM.
     */
    private void updateRrd4j() {
        
        /**
         * Set system values.
         */
        Float RAM = (float)Model.getTotalMemory();
        RAM -= (float)Model.getFreeMemory();
        RAM /= (float)Model.getTotalMemory();
        RAM *= (float)100;
        
        Float SWAP = (float)Model.getTotalSwap();
        SWAP -= (float)Model.getFreeSwap();
        SWAP /= (float)Model.getTotalSwap();
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
        String[] JVMDb = new String[Model.getJVMs().size()];
        for (int i = 0; i < Model.getJVMs().size() && i < 10 ; i++) {
            JVMDb[i] = Model.getJVMs().get(i).getName();
        }
        rrd4j.addValue(dataBaseFile, systemDb, systemValue , JVMDb , Model.getMemoryTab() , Model.getColorList());
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
    
    private boolean updateJVM(int index , JMXConnector conn , int PID , String name) {
        
        if(conn != null) {
            try {
                double memHeap, memNonHeap; 
                
                // Get an MBeanServerConnection
                MBeanServerConnection mBeanServConn = conn.getMBeanServerConnection();

                RuntimeMXBean mBeanRuntime = ManagementFactory.newPlatformMXBeanProxy(mBeanServConn,
                                        ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);

                MemoryMXBean memBean = ManagementFactory.newPlatformMXBeanProxy(mBeanServConn, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class ); 

                Model.addToHeap( memBean.getHeapMemoryUsage().getUsed() );
                Model.addToNonHeap( memBean.getNonHeapMemoryUsage().getUsed() );

                /*
                 * get back some information
                 */
                if(mBeanRuntime != null) {

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
                        Model.setJVM(index, new JVMData(name, PID, memHeap, memNonHeap, conn));
                        
                    } else {
                        System.out.println("debug : updateJVM1");
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
            } catch (Exception ex) {
                System.out.println("debug : updateJVM2");
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
                if(conn.getConnectionId() != null ) {
                    
                    updateJVM(index, conn, Model.getJVMs().get(index).getPID() , Model.getJVMs().get(index).getName());
                    checked = true;
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
    
    private void getTime(String str) {
        
        Calendar cal = Calendar.getInstance();
        String time = cal.get(Calendar.HOUR_OF_DAY)+"h "+cal.get(Calendar.MINUTE)+"m et "+cal.get(Calendar.SECOND)+"s";
        System.out.println(str + " : " + time);
    }
    
    /**
     * Run JMX listener
     * @return True if the listen has ran, False if not.
     */
    public boolean runJmx() {
        
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
            if(!Model.checkJVM( Integer.parseInt(pid) )) {
                JMXConnector jmxc = JVMdetector.getJMXConnector(pid);

                if(jmxc != null) {

                    String name = JVMdetector.getProcesses().get(pid).toString();
                    name = parseName(name);
                    
                    if(!name.equals("")) {
                        updateJVM(Model.addJVM(), jmxc, Integer.parseInt(pid) , name);
                        check = true;
                    }
                }
            }
        }
        Model.setMemHeap( Model.getMemHeap() / (1024*1024));
        Model.setMemNonHeap( Model.getMemNonHeap() / (1024*1024));
        updateRrd4j();

        return check;
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
