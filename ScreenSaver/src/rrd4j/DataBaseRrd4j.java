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
package rrd4j;

import jmx.JVMData;
import util.utils;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.DsDef;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.RrdToolkit;
import org.rrd4j.core.Sample;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

/**
 *
 * @author philippe Gouttefarde
 */
public class DataBaseRrd4j implements DataBase {

    public int SYSTEM = 0;
    public int JVM = 1;
    
    private String path;
    private ArrayList<ChartData> systemDataSource = new ArrayList<ChartData>();
    private ArrayList<ChartData> JVMDataSource = new ArrayList<ChartData>();
    
    private Random random = new Random();
    private String graphPath = "/tmp/tmp.gif";
    private String comment = "ProActive screensaver using rrd4J" ;
    private long startTime = 0L;
    private long endTime;
    private int height;
    private int width;
    private long weekDuration = 604800L;
    private long halfHour = 1800L;
    private long minute15Duration = 900L;
    private long minuteDuration = 60L;
    private long halfMinuteDuration = 30L;
    
    private long currentDuration = minute15Duration;

    private final static RootLogger logger = (RootLogger) Logger.getRootLogger();
    
    /**
     * Initialize database file and ArrayList before start scanning.
     * @param path Path of the data base file.
     * @return An ArrayList of JVM name contained in file. 
     */
    public ArrayList<String> init(String path) {
        
        this.path = path;
        
        if(new File(path).exists()) {
            
            ArrayList<String> list = new ArrayList<String>();
            
            try {
                RrdDb rrdDb = new RrdDb(path);
                String[] ds = rrdDb.getDsNames();
                int i;
                for (i=0 ; i < 2 ; ++i) {
                        systemDataSource.add( new ChartData(ds[i], getRandomColor() ));
                }
                
                for (i=2 ; i < ds.length ; ++i) {
                        list.add( ds[i] );
                }
                
                return list;
            } catch (IOException ex) {
                logger.error(ex);
            }
        }
        
        return null;
    }
    
    /**
     * Update the JVMList after initialize the application.
     * @param list the initialized arraylist.
     */
    public void majInit(ArrayList<JVMData> list) {
        
        if(list != null) {
           for (JVMData data : list) {
                JVMDataSource.add( new ChartData( utils.getHashCode(
                        data.getName() + " " + data.getPID() + " " + data.getStartTime())
                        , getRandomColor() ));
            } 
        }
    }
    
    /**
     * Return the current timestamp in seconds.
     * @return the number of seconds since 1-1-1970
     */
    private long getTime() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * delete the database.
     * @param path The path of the databse.
     */
    @Override
    public void deleteDB(String path) {
        
        File f = new File(path);
        if(f.exists()) {
            f.delete();
        }
    }
    
    /**
     * debug method which count data source in the database.
     * @param path of the database.
     */
    public void checkBD(String path) {
        
        if(new File(path).exists()) {
        
            try {
                RrdDb rrdDb = new RrdDb(path);
                logger.info("nb DataBase : " + rrdDb.getDsCount());
                String[] ds = rrdDb.getDsNames();
                for (String name : ds) {
                     logger.info("name :" + name);
                }
            } catch (IOException ex) {
                logger.error(ex);
            }
        }
    }

    /**
     * debug method which display all data of a database.
     * @param path of the database.
     */
    public void displayValue(String path) {
        
        
        try {
            RrdDb rrdDb = new RrdDb(path);
            FetchRequest fetchRequest = rrdDb.createFetchRequest(ConsolFun.TOTAL, startTime , endTime);
            FetchData fetchData = fetchRequest.fetchData();
            double[][] result = fetchData.getValues();
            
            for (double[] ds : result) {
                for (double d : ds) {
                    logger.info(d);
                }
            }
            
            rrdDb.close();
            
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * setter of dimensions graph.
     * @param width
     * @param height 
     */
    @Override
    public void setSize(int width, int height) {
        this.height = height;
        this.width = width;
    }
    
    /**
     * Return a random color for the new curves.
     * @return a random color.
     */
    private Color getRandomColor() {
        
        int r = random.nextInt()%255;
        int g = random.nextInt()%255;
        int b = random.nextInt()%255;
        
        if(r < 0) r = -r;
        if(g < 0) g = -g;
        if(b < 0) b = -b;
        
        return new Color(r,g,b);
    }

    /**
     * Create a complete database with data sources name.
     * 
     * @param path the file path of db.
     * @param dbNameSystem array of data source name for system informations.
     * @param dbNameJVM array of data source name for JVM informations.
     * @return true if all is good, false if not.
     */
    public boolean createDB(String path, String[] dbNameSystem, String[] dbNameJVM) {
        
        this.path = path;
        
        try {
            startTime = getTime();

            RrdDef rrdDef = new RrdDef(path);
            rrdDef.setStartTime(startTime);
            rrdDef.setStep(1);
            rrdDef.addArchive(ConsolFun.TOTAL, 0.2, 1, 5000);

            for(int i = 0 ; i < dbNameSystem.length ; ++i) {
                systemDataSource.add( new ChartData(dbNameSystem[i], getRandomColor()));
                rrdDef.addDatasource(dbNameSystem[i], DsType.GAUGE, 5000, Double.NaN, Double.NaN);
            }
            for(int i = 0 ; i < dbNameJVM.length ; ++i) {
                JVMDataSource.add( new ChartData(dbNameJVM[i], getRandomColor()));
                rrdDef.addDatasource(dbNameJVM[i], DsType.GAUGE, 5000, Double.NaN, Double.NaN);
            }

            RrdDb rrdDb = new RrdDb(rrdDef);
            rrdDb.close();
            
            // To avoid bug with future stored values (1 second minimum between each value).
            Thread.sleep(1000);
            
        } catch (InterruptedException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
            
        return true;
    }

    /**
     * Check if the database file exist, if not, it will create it.
     * @param path the database file path.
     * @param dbSystem the system datasource name.
     * @param dbJVMs the memory datasource name.
     */
    private void checkFile(String path, String[] dbSystem, String[] dbJVMs) {
        
        if(!new File(path).exists()) {
                createDB( path, dbSystem , dbJVMs);
            } 
    }
    
    /**
     * Check if a datasource name exists in the database.
     * @param ds the datasource name.
     * @param rrdDb the courant rrdDatabase.
     * @return true if it exists, false else.
     */
    private boolean checkDsInDataBase(String ds , RrdDb rrdDb) {
        try {
            return rrdDb.containsDs(ds);
        } catch (IOException ex) {
            logger.error(ex);
        }
        return false;
    }
    
    /**
     * Check if a datasource name exists in an array
     * @param ds the datasource name.
     * @param tab the data array.
     * @return the rank if ds has been found, -1 if not.
     */
    private boolean checkDsInTab(String ds , String[] tab) {
        for (int i = 0; i < tab.length; i++) {
            if(tab[i].equals(ds)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * return the rank of a datasource name in the JVMDataSource ArrayList
     * @param name the datasource name.
     * @return the rank if name has been found, -1 if not.
     */
    private int indexOfJVMInList(String name) {
        for (int i = 0; i < JVMDataSource.size(); i++) {
            if(JVMDataSource.get(i).getDataSourceName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * This method will check each new JVM scanned in the database:
     *      1) delete the JVMs lost
     *      2) add the new JVM
     * @param path the rrd4j file path.
     * @param dbJVMs the JVMs scanned.
     */
    private void checkDataSources(String path, String[] dbJVMs) {
        try {
            RrdDb rrdDb = new RrdDb(path);
            
            //Check the JVM to delete in database.
            for (int i = 2; i < rrdDb.getDsCount(); i++) {
                if(!checkDsInTab(rrdDb.getDatasource(i).getName(), dbJVMs)) {
                    int tmp = indexOfJVMInList(rrdDb.getDatasource(i).getName());
                    
                    if(tmp != -1) {
                        logger.info("remove " + rrdDb.getDatasource(i).getName() + " at rank : " + tmp);
                        RrdToolkit.removeDatasource(path, rrdDb.getDatasource(i).getName(), false);
                        JVMDataSource.remove( tmp );
                    }
                }
            }
            
            //check the JVM to add in database.
            for(int i = 0; i < dbJVMs.length ; ++i) {
                
                if( !checkDsInDataBase(dbJVMs[i] , rrdDb)) {
                    
                    logger.info("add : " + dbJVMs[i]);

                    DsDef def = new DsDef(dbJVMs[i], DsType.GAUGE, 5000, Double.NaN, Double.NaN);
                    RrdToolkit.addDatasource(path, def, false);
                    JVMDataSource.add( new ChartData(dbJVMs[i], getRandomColor()));
                }
            }
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
    
    /**
     * update the database with news values.
     * 
     * @param path the file path of db.
     * @param dbNameSystem array of data source name for system informations.
     * @param valueSystem array of system values.
     * @param dbNameJVM array of data source name for JVM informations.
     * @param valueJVMs array of JVM values.
     * @return true if all is good, false if not.
     */
    public boolean addValue(String path, String[] dbSystem, double[] valueSystem, String[] dbJVMs, double[] valueJVMs) {
        
        
        /*
         * Check datas before insert new value.
         */
        this.path = path;
        checkFile( path, dbSystem , dbJVMs);
        checkDataSources(path, dbJVMs);

        try {
            RrdDb rrdDb = null;
            rrdDb = new RrdDb(path);
            Sample sample = rrdDb.createSample();

            //Create the new value
            long time = getTime();
            //To avoid bug in updating values.
            if(time - rrdDb.getLastUpdateTime() <= 1) {
                try {
                    Thread.sleep(1000);
                    time = getTime();
                } catch (InterruptedException ex) {
                    logger.error(ex);
                }
            }
            String val = time + "";

            for (double d : valueSystem) {
                val += ":" + d;
            }
            for (double d : valueJVMs) {
                val += ":" + d;
            }

            //Add value to database.
            sample.setAndUpdate(val);

            endTime = time;
            rrdDb.close();

        } catch (IOException ex) {
            logger.error(ex);
            return false;
        }
        
        return true;
    }

    /**
     * Generate graphic of system informations or JVMs informations.
     * 
     * @param type 0 for system, 1 for JVMs.
     * @param title The title of the chart.
     * @return a BufferedImage containing the graph. 
     */
    public BufferedImage createGraphic(int type , String title) {
        
        try{
            startTime = endTime - currentDuration;
            
            RrdGraphDef graphDef = new RrdGraphDef();
            graphDef.setTimeSpan(startTime, endTime);
            graphDef.setHeight(height);
            graphDef.setWidth(width);
            
            if(type == SYSTEM) {
                for(int i = 0 ; i < systemDataSource.size() ; ++i) {
                    graphDef.datasource(
                            systemDataSource.get(i).getDataSourceName(), this.path, 
                            systemDataSource.get(i).getDataSourceName(), ConsolFun.TOTAL); 
                    graphDef.line(
                            systemDataSource.get(i).getDataSourceName(), systemDataSource.get(i).getColor() , 
                            systemDataSource.get(i).getDataSourceName(), 1);
                }
                graphDef.setMaxValue(100);
                
            } else {
               for(int i = 0 ; i < JVMDataSource.size() ; ++i) {
                    graphDef.datasource(
                            JVMDataSource.get(i).getDataSourceName(), this.path, 
                            JVMDataSource.get(i).getDataSourceName(), ConsolFun.TOTAL); 
                    graphDef.line(
                            JVMDataSource.get(i).getDataSourceName(), JVMDataSource.get(i).getColor() , 
                            i + "", 1);
                } 
                graphDef.setAltAutoscaleMax(true);
            }

            graphDef.setFilename(graphPath);
            graphDef.setTitle(title);
            
            graphDef.setMinValue(0);
            graphDef.setVerticalLabel("percent %");
            graphDef.setAltYGrid(true);
            graphDef.setAntiAliasing(true);
            graphDef.setForceRulesLegend(true);
            graphDef.comment(comment);
            graphDef.setNoMinorGrid(true);
            graphDef.setShowSignature(false);
            
            RrdGraph graph = new RrdGraph(graphDef);
            
            int difOnX = graph.getRrdGraphInfo().getWidth() - width;
            int difOnY = graph.getRrdGraphInfo().getHeight() - height;
            
            BufferedImage bi = new BufferedImage(width+difOnX,height+difOnY,BufferedImage.TYPE_INT_RGB);
            
            graph.render(bi.getGraphics());
            
            return bi;
        } catch (IOException ex) {
            logger.error(ex);
        }
        return null;
    }
}
