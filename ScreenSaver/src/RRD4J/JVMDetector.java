package RRD4J;

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

import sun.tools.jps.Jps;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/**
 * @author KWang
 * 
 */
public class JVMDetector {

   private  String sep =  System.getProperty("file.separator");
	
   private  String path;
   private final String CONNECTOR_ADDRESS =
		   "com.sun.management.jmxremote.localConnectorAddress";
   
   private enum STATUS{enabled, notEnalbed, willBeEnabled};
   
   private ArrayList<String> pidTab = new ArrayList<String>();
   private Map processes = null;
   private Map jmx = null;
   
   public ArrayList<String> getPidTab() {
	   return pidTab;
   }

    public void setPidTab(ArrayList<String> pidTab) {
            this.pidTab = pidTab;
    }

    public Map getJmx() {
            return jmx;
    }

    public void setJmx(Map jmx) {
            this.jmx = jmx;
    }

    public Map getProcesses() {
            return processes;
    }

    public void setProcesses(Map processes) {
            this.processes = processes;
    }

    private void initPath() {
            path = System.getProperty("java.home");
            path += sep + ".." + sep + "bin" + sep;
    }
   
   public void scan() {
	   	
	   	initPath();
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

        displayInfos(processes, jmx);
   }
   
   private void displayInfos(Map processes, Map jmx){
   	
   		for (Object object : jmx.entrySet().toArray()) {
   		
	   		String[] tmp = object.toString().split("=");
	   		String key = tmp[0];
	   		String status = tmp[1];
	   		
	   		pidTab.add(key);
	   		
	   		/*System.out.println("********************************************");
	   		System.out.println("Name : " + processes.get( key ));
	   		System.out.println("Info : " + status + "(" + key + ")");*/
		}
   }

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

   private Map getProcessesMap() throws IOException {
       Map processes = new TreeMap();
       Runtime rt = Runtime.getRuntime();

       BufferedReader br = new BufferedReader(new InputStreamReader(rt.exec(
    		   path + "jps -l -m").getInputStream()));
       String line = new String();
       while ((line = br.readLine()) != null) {
           String[] proc = line.split(" ");
           if(proc.length > 1){
           	if (proc[1].equals(Jps.class.getName()) 
                       || proc[1].equals(JVMDetector.class.getName()))
                       continue; // ignore jps command process and this process
           }
           
           processes.put(proc[0], line.substring(proc[0].length()).trim());
       }
       return processes;
   }
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
   
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	
        JVMDetector jvm = new JVMDetector();
        jvm.scan();
    }
}