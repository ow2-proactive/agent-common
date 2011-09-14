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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/**
 *
 * @author philippe Gouttefarde
 */
public class JVMDetector {

   
   private  String sep =  System.getProperty("file.separator");
	
   private  String path;
   private final String CONNECTOR_ADDRESS =
		   "com.sun.management.jmxremote.localConnectorAddress";
   
   private enum STATUS{enabled, notEnalbed, willBeEnabled};
   
   
   // The pid list of the JVMs.
   private ArrayList<String> pidTab = new ArrayList<String>();
   // 2 maps to the detection process.
   private Map processes = null;
   private Map jmx = null;
   
   /**
    * Default constructor.
    * Initialize jdk home path.
    */
   public JVMDetector() {
       initPath();
   }
   
   /**
    * Clear the PID array.
    */
   public void resetPidTab() {
       pidTab.clear();
   }
   
   /**
    * getter of the PID list.
    * @return a ArrayList of PID.
    */
   public ArrayList<String> getPidTab() {
	   return pidTab;
   }

   /**
    * return the process map
    * @return the process map
    */
    public Map getProcesses() {
            return processes;
    }

    /**
     * Init the JAVAHOME path
     */
    private void initPath() {
            path = System.getProperty("java.home");
            path += sep + ".." + sep + "bin" + sep;
    }
   
    /**
     * The method launch the scanning process to detects all JVM running on the system.
     */
   public void scan() {
	   	
        // first round: get all process info.
        // process id is the key, name of the process is the value
        // name includes full package name for the application's main class
        // or the full path name to the application's JAR file
        // along with the arguments passed to the main method
        try {
                processes = getProcessesMap();
        } catch (IOException e) {
                e.printStackTrace();
        }

        // second round: detect if jmx is enabled
        // process id is the key, whether the process is jmx enabled is the
        // value
	jmx = detectJMXAgent(processes.keySet());

        setPIDList(jmx);
   }
   
   /**
    * set the PID list.
    * 
    * @param jmx The jmx connectors map.
    */
   private void setPIDList(Map jmx){
   	
   		for (Object object : jmx.entrySet().toArray()) {
   		
                        int index = object.toString().indexOf("=");
	   		pidTab.add(object.toString().substring(0, index));
		}
   }

   /**
    * debug method to display process status.
    * @param value The value to test.
    * @return the value in String format.
    */
   private String getString(STATUS value) {
       switch (value) {
           case enabled:
               return "Enabled";
           case willBeEnabled:
               return "Will Be Enabled";
           case notEnalbed:
           default:
               return "Not Enabled";
       }
   }

   /**
    * Construct the jmx map.
    * @param processids set.
    * @return the jmx map.
    */
   private Map detectJMXAgent(Set processids) {
        BufferedReader br = null;
        Map jmx = new TreeMap();
        try {
            
            Runtime rt = Runtime.getRuntime();
            br = new BufferedReader(new InputStreamReader(rt.exec(path + "jps -l -v")
            .getInputStream()));
            String line = new String();
            while ((line = br.readLine()) != null) {
                String[] proc = line.split(" ");
                if (processids.contains(proc[0])){
                    STATUS isJmx = STATUS.notEnalbed;
                    for(String process : proc){
                        if(process.startsWith("-Dcom.sun.management.jmxremote") ||
                            process.startsWith("-Dcom.sun.management.config.file")){
                            isJmx = STATUS.enabled;
                            break;
                        }
                    }
                    if(!STATUS.enabled.equals(isJmx)){
                        try {
                            VirtualMachine.attach(proc[0]);
                            isJmx = STATUS.willBeEnabled;
                        } catch (AttachNotSupportedException e) {
                            //do nothing
                        }
                    }
                    jmx.put(proc[0], isJmx);
                }
            }
        } catch (IOException ex) {
        } finally {
            
            return jmx;

        }
   }

   /**
    * Construct the process map.
    * @return the process map.
    * @throws IOException 
    */
   private Map getProcessesMap() throws IOException {
       Map processes = new TreeMap();
       Runtime rt = Runtime.getRuntime();

       BufferedReader br = new BufferedReader(new InputStreamReader(rt.exec(
    		   path + "jps -l -m").getInputStream()));
       String line = new String();
       while ((line = br.readLine()) != null) {
           String[] proc = line.split(" ");
           if(proc.length > 1){
           	if (proc[1].equals("sun.tools.jps.Jps")
                       || proc[1].equals(JVMDetector.class.getName()))
                       continue; // ignore jps command process and this process
           }
           
           processes.put(proc[0], line.substring(proc[0].length()).trim());
       }
       return processes;
   }
   
   /**
    * return a jmx connector specific to the java process id.
    * @param id The id of java application.
    * @return the jmx connector
    */
   public JMXConnector getJMXConnector(String id) {
	   
          // attach to the target application
          VirtualMachine vm;
          try {
                vm = VirtualMachine.attach(id);
                // get the connector address
                String connectorAddress =
                      vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);

                  // no connector address, so we start the JMX agent
                  if (connectorAddress == null) {
                     String agent = vm.getSystemProperties().getProperty("java.home") +
                         File.separator + "lib" + File.separator + "management-agent.jar";
                     vm.loadAgent(agent);

                     // agent is started, get the connector address
                     connectorAddress =
                         vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
                  }

             // establish connection to connector server
             JMXServiceURL url = new JMXServiceURL(connectorAddress);
             JMXConnector jmxc = JMXConnectorFactory.connect(url);

             return jmxc;

        } catch (IOException e) {
                //e.printStackTrace();
                return null;
        } catch (AttachNotSupportedException e) {
                //e.printStackTrace();
                return null;
        } catch (AgentLoadException e) {
                //e.printStackTrace();
                return null;
        } catch (AgentInitializationException e) {
                //e.printStackTrace();
                return null;
        }
   }
}