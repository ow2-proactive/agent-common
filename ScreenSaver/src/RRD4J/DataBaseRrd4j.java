/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RRD4J;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

/**
 *
 * @author pgouttef
 */
public class DataBaseRrd4j implements DataBase {

    public int SYSTEM = 0;
    public int JVM = 1;
    
    private String path;
    private ArrayList<ChartData> systemDataSource = new ArrayList<ChartData>();
    private ArrayList<ChartData> JVMDataSource = new ArrayList<ChartData>();
    
    private String graphPath = "./graph.gif";
    private String comment = "ProActive screensaver using rrd4J" ;
    private long startTime = 0L;
    private long endTime;
    private int height;
    private int width;
    private long weekDuration = 604800L;
    private long halfHour = 1800L;
    private long minuteDuration = 60L;
    private long halfMinuteDuration = 30L;
    
    private long currentDuration = minuteDuration;
    
    private long getTime() {
        return System.currentTimeMillis() / 1000;
    }

    @Override
    public void deleteDB(String path) {
        
        File f = new File(path);
        if(f.exists()) {
            f.delete();
        }
    }

    private boolean dataSourceJVMExist(String datasource) {
        
        for (ChartData data: JVMDataSource) {
            if(data.getDataSourceName().equals(datasource)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean dataSourceSystemExist(String datasource) {
        
        for (ChartData data: systemDataSource) {
            if(data.getDataSourceName().equals(datasource)) {
                return true;
            }
        }
        return false;
    }
    
    private void countDb(String path) {
        try {
            RrdDb rrdDb = new RrdDb(path);
            System.out.println("nb DataBase : " + rrdDb.getDsCount());
            String[] ds = rrdDb.getDsNames();
            for (String name : ds) {
                System.out.println("name : " + name);
            }
        } catch (IOException ex) {
            Logger.getLogger(DataBaseRrd4j.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void displayValue(String path) {
        
        
        try {
            RrdDb rrdDb = new RrdDb(path);
            FetchRequest fetchRequest = rrdDb.createFetchRequest(ConsolFun.TOTAL, endTime - currentDuration, endTime);
            FetchData fetchData = fetchRequest.fetchData();
            double[][] result = fetchData.getValues();
            
            for (double[] ds : result) {
                for (double d : ds) {
                    System.out.print(d + " ");
                }
                System.out.println();
            }
            
            rrdDb.close();
            
        } catch (IOException ex) {
            Logger.getLogger(DataBaseRrd4j.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setGraphFile(String path) {
        this.graphPath = path;
    }

    @Override
    public void setSize(int width, int height) {
        this.height = height;
        this.width = width;
    }
    
    public static void main(String[] args) {
        
        String path = "test.rrd";
        String graphPath = "graph.gif";
        String dbName = "RAM";
        
        DataBase db = new DataBaseRrd4j();
        
        for(int i=0 ; i < 5 ; ++i) { 
            try {
                Thread.sleep(1000);
                //db.addValue(((DataBaseRrd4j)db).SYSTEM, path, dbName, Color.RED ,i);
            } catch (InterruptedException ex) {
                Logger.getLogger(DataBaseRrd4j.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
          
        ((DataBaseRrd4j)db).displayValue(path);
        
        db.setGraphFile(graphPath);
        db.setSize(500, 800);
        //db.createGraphic();
    }

    public boolean createDB(String path, String[] dbNameSystem, String[] dbNameJVM, Color[] color) {
        
        try {
            if(!new File(path).exists()) {
                try {
                    startTime = getTime();
                    this.path = path;
                    
                    RrdDef rrdDef = new RrdDef(path);
                    rrdDef.setStartTime(startTime);
                    rrdDef.setStep(1);
                    rrdDef.addArchive(ConsolFun.TOTAL, 0.2, 1, 5000);
                    
                    
                    
                    for(int i = 0 ; i < dbNameSystem.length ; ++i) {
                        systemDataSource.add( new ChartData(dbNameSystem[i], color[i]));
                        rrdDef.addDatasource(dbNameSystem[i], DsType.GAUGE, 5000, Double.NaN, Double.NaN);
                    }
                    for(int i = 0 ; i < dbNameJVM.length ; ++i) {
                        JVMDataSource.add( new ChartData(dbNameJVM[i], color[i+dbNameSystem.length]));
                        rrdDef.addDatasource(dbNameJVM[i], DsType.GAUGE, 5000, Double.NaN, Double.NaN);
                    }
                    
                    RrdDb rrdDb = new RrdDb(rrdDef);
                    rrdDb.close();
                } catch (IOException ex) {
                    Logger.getLogger(DataBaseRrd4j.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("ever exist.");
            }
            // To avoid bug with future stored values (1 second minimum between each value).
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DataBaseRrd4j.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public boolean addValue(String path, String[] dbSystem, double[] valueSystem, String[] dbJVMs, double[] valueJVMs, Color[] colors) {
        RrdDb rrdDb = null;
        
        if(!new File(path).exists()) {
            createDB( path, dbSystem , dbJVMs , colors);
        } 
        
        try {
            rrdDb = new RrdDb(path);
            RrdDef rrdDef = rrdDb.getRrdDef();
            
            for (String name : dbSystem) {
                if(!rrdDb.containsDs(name)){
                    rrdDef.addDatasource(name, DsType.GAUGE, 5000, Double.NaN, Double.NaN);
                }
            }
            for (String name : dbJVMs) {
                if(!rrdDb.containsDs(name)){
                    rrdDef.addDatasource(name, DsType.GAUGE, 5000, Double.NaN, Double.NaN);
                }
            }
            
            this.path = path;

            for (int i = 0; i < dbSystem.length; i++) {
                if(!dataSourceSystemExist(dbSystem[i])) {
                    systemDataSource.add(new ChartData(dbSystem[i], colors[i]));
                }
            }
            for (int i = 0; i < dbJVMs.length; i++) {
                if(!dataSourceSystemExist(dbJVMs[i])) {
                    JVMDataSource.add(new ChartData(dbJVMs[i], colors[i+dbSystem.length]));
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(DataBaseRrd4j.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        try {
            
            Sample sample = rrdDb.createSample();
            
            long time = getTime();
            String val = time + "";
            
            for (double d : valueSystem) {
                val += ":" + d;
            }
            for (double d : valueJVMs) {
                val += ":" + d;
            }
            
            sample.setAndUpdate(val);
            
            endTime = time;
            rrdDb.close();
            
        } catch (IOException ex) {
            Logger.getLogger(DataBaseRrd4j.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public BufferedImage createGraphic(int type , String title) {
        try{
            if(startTime==0L) {
                startTime = endTime - currentDuration;
            }
            
            RrdGraphDef graphDef = new RrdGraphDef();
            graphDef.setTimeSpan(startTime, endTime);
            graphDef.setHeight(height);
            graphDef.setWidth(width);
            
            
            if(type == SYSTEM) {
                for(int i = 0 ; i < systemDataSource.size() ; ++i) {
                    graphDef.datasource(
                            systemDataSource.get(i).getDataSourceName(), this.path, 
                            systemDataSource.get(i).getDataSourceName() , ConsolFun.TOTAL); 
                    graphDef.line(
                            systemDataSource.get(i).getDataSourceName(), systemDataSource.get(i).getColor() , 
                            systemDataSource.get(i).getDataSourceName(), 1);
                }
            } else {
               for(int i = 0 ; i < JVMDataSource.size() ; ++i) {
                    graphDef.datasource(
                            JVMDataSource.get(i).getDataSourceName(), this.path, 
                            JVMDataSource.get(i).getDataSourceName() , ConsolFun.TOTAL); 
                    graphDef.line(
                            JVMDataSource.get(i).getDataSourceName(), JVMDataSource.get(i).getColor() , 
                            i + "", 1);
                } 
            }

            graphDef.setFilename(graphPath);
            graphDef.setTitle(title);
            graphDef.setMaxValue(100);
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
            Logger.getLogger(DataBaseRrd4j.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
