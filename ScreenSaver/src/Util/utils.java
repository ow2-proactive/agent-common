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
package Util;

import JMX.JVMData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rrd4j.core.RrdToolkit;

/**
 *
 * @author philippe Gouttefarde
 */
public class utils {
    
    /**
     * Check if a JVMData exists in an ArrayList of String.
     * @param data the JVMData object
     * @param rrd4jList the ArrayList
     * @return the rank if it exists, -1 if not.
     */
    private static int checkJVM(JVMData data , ArrayList<String> rrd4jList) {
        
        for (int i = 0 ; i < rrd4jList.size() ; ++i) {
            if(     rrd4jList.get(i).equals( getHashCode(
                data.getName() + " " + data.getPID() + " " + data.getStartTime() ))) {
                
                return i;
            }
        }
       
        return -1;
    }
    
    /**
     * Check if a JVM name exists in an ArrayList of JVMData.
     * @param data the JVM name
     * @param JVMs the ArrayList
     * @return true if it exists, false if not.
     */
    private static boolean checkDataSource(String data, ArrayList<JVMData> JVMs) {
        
        for (JVMData tmp : JVMs) {
            if(     data.equals( getHashCode(
                tmp.getName() + " " + tmp.getPID() + " " + tmp.getStartTime() ))) {
                
                return true;
            }
        }
       
        return false;
    }
    
    /**
     * Debug method to print an ArrayList of string
     * @param list the ArrayList
     * @param txt a prefix of the display
     */
    private static void printList(ArrayList<String> list, String txt) {
        
        if(list != null) {
           
            System.out.println( txt + " size : " + list.size());
            for (String name : list) {
                System.out.println("\t" + name);
            }
        }
    }
    
    /**
     * Debug method to print an ArrayList of JVMData
     * @param list the ArrayList
     * @param txt a prefix of the display
     */
    private static void printListJVMs(ArrayList<JVMData> list, String txt) {
        
        if(list != null) {
           
            System.out.println( txt + " size : " + list.size());
            for (JVMData tmp : list) {
                System.out.println("\t" + tmp.getName());
            }
        }
    }
    
    /**
     * Synchronize the ArrayList of running JVMs with the database rrd4j.
     * @param JVMs a first list of running JVMs
     * @param rrd4jList the list of JVM contained in rrd4j data base
     * @param path the database file
     * @return a final ArrayList of running JVMs synchronized.
     */
    public static ArrayList<JVMData> synchronization( ArrayList<JVMData> JVMs , ArrayList<String> rrd4jList , String path) {
        
        int i = 0;
        
        
        if(rrd4jList == null) {
            return new ArrayList<JVMData>();
        }
        //Check rrd4j in JVM detected
        while(i < rrd4jList.size()) {
            
            if(!checkDataSource(rrd4jList.get(i) , JVMs)) {
                try {
                    
                    RrdToolkit.removeDatasource(path, rrd4jList.get(i), false);
                    rrd4jList.remove(i);
                    
                } catch (IOException ex) {
                    Logger.getLogger(utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                i++;
            }
        }
        
        ArrayList<JVMData> result = new ArrayList<JVMData>( rrd4jList.size() );
        for(i = 0 ; i < rrd4jList.size() ; i++) {
            result.add( new JVMData());
        }
        
        //Check JVM detected in rrd4j
        while (JVMs.size() > 0) {
            
            int rank = checkJVM(JVMs.get(0), rrd4jList);
            //if JVM doesn't exist in rrd4j
            if(rank != -1 && rank >= 0) {
                
                result.set( rank , JVMs.get(0) );
            } 
            
            JVMs.remove(0);
        }
        
        return result;
    }
    
    /**
     * Return the hashcode of a string.
     * Usefull for build a single name for each datasource in database.
     * @param str a tempory datasource name
     * @return the hashcode of datasource name
     */
    public static String getHashCode(String str) {
        return str.hashCode() + "";
    } 
}
