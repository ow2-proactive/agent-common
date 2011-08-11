/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RRD4J;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author pgouttef
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
