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

import RRD4J.ClientJMX;
import RRD4J.DataBaseRrd4j;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author philippe Gouttefarde
 */
public class ChartAera {
    
    /* Chart Aera */
    private int Zone1X;
    private int Zone1SizeX;
    private int Zone1Y;
    private int Zone1SizeY;
    
    private int sizeLegendX = 91;
    private int sizeLegendY = 73;
    
    //Context colors
    private Color colorBackground = Color.white;
    private Color colorText = Color.black;
    
    //ProActive colors
    private Color colorBlue = new Color(0x1d, 0x30 , 0x6b);
    private Color colorOrange = new Color(0xe7, 0x74 , 0x24);
    
    //Client JMX object
    private ClientJMX clientJMX;
    
    //SYSTEM and JVM ariables
    private int SYSTEM = new DataBaseRrd4j().SYSTEM;
    private int JVM = new DataBaseRrd4j().JVM;

    /**
     * Constructor with parameters.
     * 
     * @param startX : X position where screensaver will start.
     * @param startY : Y position where screensaver will start.
     * @param sizeX : X size of screensaver.
     * @param sizeY : Y size of screensaver.
     * @param clientJMX : the JMX client object for listen JVM.
     */
    public ChartAera(int startX, int startY, int sizeX, int sizeY , ClientJMX clientJMX) {
        this.Zone1X = startX;
        this.Zone1SizeX = sizeX;
        this.Zone1Y = startY;
        this.Zone1SizeY = sizeY;
        this.clientJMX = clientJMX;
    }
    
    /**
     *  The first method, the graphic begin here.
     * @param g the Graphics2D support to paint
     */
    public void paint(Graphics2D g){
        
        g.setPaint(colorBackground);
        g.fillRect(Zone1X, Zone1Y - Zone1SizeY, Zone1SizeX, Zone1SizeY);
        g.setPaint(colorText);
        g.drawRect(Zone1X, Zone1Y - Zone1SizeY, Zone1SizeX, Zone1SizeY);
        
        drawChart(g);
    }
    
    /**
     * Main method which will draw the charts.
     * @param g the Graphics2D support to paint
     */
    private void drawChart(Graphics2D g) {
        
        int midY = Zone1Y - Zone1SizeY/2; 
        clientJMX.getDataBase().setSize(Zone1SizeX - sizeLegendX, Zone1SizeY/2 -sizeLegendY);
        
        g.drawImage(clientJMX.getDataBase().createGraphic(SYSTEM, "System informations"), null, Zone1X, Zone1Y - Zone1SizeY);
        g.drawImage(clientJMX.getDataBase().createGraphic(JVM , "JVMs informations"), null, Zone1X, midY);
    }
}
