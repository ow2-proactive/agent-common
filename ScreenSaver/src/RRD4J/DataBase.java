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
     * Create a new database.
     * 
     * @param path Path of the db file.
     * @param dbName Name of the future database.
     */
    public boolean createDB(String path,String[] dbNameSystem, String[] dbNameJVM , Color[] color);
    
    /**
     * Delete the specific database.
     * 
     * @param path Path of the database to delete.
     */
    public void deleteDB(String path);
    
    /**
     * Add a new value to the specific database.
     * 
     * @param path Path of the database.
     * @param dbName Name of the database. 
     * @param value Value to add.
     */
    public boolean addValue(String path,
            String[] dbSystem, double[] valueSystem,
            String[] dbJVMs, double[] valueJVMs, 
            Color[] colors);
    
    /**
     * Generate graphic with all database that the class know.
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
