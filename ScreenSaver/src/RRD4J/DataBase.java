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

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author philippe Gouttefarde
 */
public interface DataBase {
    
    /**
     * Create a complete database with data sources name.
     * 
     * @param path the file path of db.
     * @param dbNameSystem array of data source name for system informations.
     * @param dbNameJVM array of data source name for JVM informations.
     * @param color array of color line.
     * @return true if all is good, false if not.
     */
    public boolean createDB(String path,String[] dbNameSystem, String[] dbNameJVM , Color[] color);
    
    /**
     * Delete the specific database.
     * 
     * @param path Path of the database to delete.
     */
    public void deleteDB(String path);
    
    /**
     * update the database with news values.
     * 
     * @param path the file path of db.
     * @param dbNameSystem array of data source name for system informations.
     * @param valueSystem array of system values.
     * @param dbNameJVM array of data source name for JVM informations.
     * @param valueJVMs array of JVM values.
     * @param color array of color line.
     * @return true if all is good, false if not.
     */
    public boolean addValue(String path,
            String[] dbSystem, double[] valueSystem,
            String[] dbJVMs, double[] valueJVMs, 
            Color[] colors);
    
    /**
     * Generate graphic of system informations or JVMs informations.
     * 
     * @param type 0 for system, 1 for JVMs.
     * @param title The title of the chart.
     * @return a BufferedImage containing the graph. 
     */
    public BufferedImage createGraphic(int type , String title);
    
    /**
     * Set path for the graph file.
     * 
     * @param path Path for the future graph. 
     */
    public void setGraphFile(String path);
    
    /**
     * Give the size of the future graph.
     * 
     * @param height Height of the graph.
     * @param width Width of the graph.
     */
    public void setSize(int height, int width);
    
}
