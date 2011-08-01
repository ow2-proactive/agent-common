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
package ScreenSaver;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author pgouttef
 */
public class ChartAera {
    
    private Graphics2D g;
    
    /* Chart Aera */
    private int Zone1X;
    private int Zone1SizeX;
    private int Zone1Y;
    private int Zone1SizeY;
    
    //Context colors
    private Color colorBackground = Color.white;
    private Color colorText = Color.black;
    
    //ProActive colors
    private Color colorBlue = new Color(0x1d, 0x30 , 0x6b);
    private Color colorOrange = new Color(0xe7, 0x74 , 0x24);
    
    //Font of chart title
    private Font titleFont;
    
    //Client JMX object
    private ClientJMX clientJMX;

    /**
     * Constructor with parameters.
     * 
     * @param g : Graphics2D object on which we will draw graphics.
     * @param startX : X position where screensaver will start.
     * @param startY : Y position where screensaver will start.
     * @param sizeX : X size of screensaver.
     * @param sizeY : Y size of screensaver.
     * @param clientJMX : the JMX client object for listen JVM.
     */
    public ChartAera(Graphics2D g , int startX, int startY, int sizeX, int sizeY , ClientJMX clientJMX) {
        this.g = g;
        this.Zone1X = startX;
        this.Zone1SizeX = sizeX;
        this.Zone1Y = startY;
        this.Zone1SizeY = sizeY;
        this.clientJMX = clientJMX;
    }
    
    /**
     *  The first method, the graphic begin here.
     */
    public void paint(){
        
        g.setPaint(colorBackground);
        g.fillRect(Zone1X, Zone1Y - Zone1SizeY, Zone1SizeX, Zone1SizeY);
        g.setPaint(colorText);
        g.drawRect(Zone1X, Zone1Y - Zone1SizeY, Zone1SizeX, Zone1SizeY);
        
        drawChart();
    }
    
    /**
     * This method create the database store in ClientJMX object under the format : ArrayList< ChartData >
     * 
     * @return XYDataset object.
     */
    private XYDataset createDataset() {

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.setDomainIsPointsInTime(true);
        
        
        ArrayList<ChartData> datas = clientJMX.getDatas();
        Calendar cal = Calendar.getInstance();
        
        /*
         * RAM SERIE
         */
        final TimeSeries s1 = new TimeSeries("RAM", Millisecond.class);
        for (ChartData chartData : datas) {
            Date date = new Date(chartData.getTime());
            cal.setTime(date);
            s1.add( new Millisecond(date) , 100*chartData.getRAM()/clientJMX.getTotalMemory());
        }
        
        /*
         * Swap SERIE
         */
        final TimeSeries s2 = new TimeSeries("Swap", Millisecond.class);
        for (ChartData chartData : datas) {
            Date date = new Date(chartData.getTime());
            cal.setTime(date);
            s2.add( new Millisecond(date) , 100*chartData.getSwap()/clientJMX.getTotalMemory());
        }
        
        dataset.addSeries(s1);
        dataset.addSeries(s2);

        return dataset;

    }
    
    /**
     * Create a specific value axis for pourcent format.
     * @param title String object
     * @return ValueAxis object
     */
    private ValueAxis createRangeAxis(String title) {
        ValueAxis axe = new NumberAxis(title);
        
        axe.setLowerBound(0);
        axe.setUpperBound(100);
        
        return axe;
    }
    
    /**
     * Create a specific and dynamic domain axis for time format.
     * @param title String object
     * @return ValueAxis object
     */
    private DateAxis createDomainAxis(String title) {
        DateAxis axe = new DateAxis(title);
        
        axe.setVerticalTickLabels(true);
        
        int size = clientJMX.getDatas().size();
        if(size < 5){
            axe.setTickUnit( new DateTickUnit(DateTickUnit.SECOND, 1));
        } else if(size < 20) {
            axe.setTickUnit( new DateTickUnit(DateTickUnit.SECOND, 5));
        } else if(size < 50) {
            axe.setTickUnit( new DateTickUnit(DateTickUnit.SECOND, 20));
        } else if(size < 150) {
            axe.setTickUnit( new DateTickUnit(DateTickUnit.MINUTE, 1));
        } else if(size < 300) {
            axe.setTickUnit( new DateTickUnit(DateTickUnit.MINUTE, 2));
        } else {
            axe.setTickUnit( new DateTickUnit(DateTickUnit.MINUTE, 5));
        }
        
        axe.setDateFormatOverride(new SimpleDateFormat("hh:mm:ss"));
        axe.setLowerMargin(0.1);
        axe.setUpperMargin(0.1);
        
        return axe;
    }
    
    /**
     * Main method which will draw the chart.
     */
    private void drawChart() {
        
        String title    = "data visualization";
        String domain   = "time";
        String range    = "memory";
        
        final XYDataset dataset = createDataset();
        final XYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setBasePaint( colorOrange );
        
        final XYPlot plot = new XYPlot(dataset, createDomainAxis(domain), 
                createRangeAxis(range), renderer);
        
        plot.setBackgroundPaint( Color.lightGray);
        plot.setDomainGridlinePaint( colorBlue );
        plot.setRangeGridlinePaint( colorBlue );
        plot.setDomainCrosshairVisible(true);
        
        
        final JFreeChart chart = new JFreeChart(title, plot);
        
        BufferedImage buff = chart.createBufferedImage(Zone1SizeX, Zone1SizeY);
        g.drawImage(buff, Zone1X, Zone1Y - Zone1SizeY, null);
        titleFont = chart.getTitle().getFont();
    }
    
    /**
     * Return the font instance of the chart title generate by JFreeChart.
     * @return the title font in Font type.
     */
    public Font getTitleFont() {
        return titleFont;
    }
}
