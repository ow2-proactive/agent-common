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

import JMX.ClientJMX;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Calendar;

/**
 *
 * @author philippe Gouttefarde
 */
public class DrawingClass{

    /*
     * The chart. Display the events list on the screen. All coordonate are generated dynamically.
     */
    
    private int GRAPHIC_LENGTH = 800;
    private int GRAPHIC_HEIGHT = 600; 
    private int BORDER_L_R = 80;
    private int PAD_TOP = 200;
    private int PAD_BOTTOM = 100;
    
    private int MID_Aera1_Aera2 = GRAPHIC_LENGTH/3;
    
    private int Clock_X = 180;
    private int Clock_Y = 140;
    
    private int Error_X = 300;
    private int Error_Y = 300;
    
    private ClientJMX clientJMX;
    
    /* CHART AERA */
    private int Zone1X;
    private int Zone1SizeX;
    private int Zone1Y;
    private int Zone1SizeY;
        
    /* DATA AERA */
    private int Zone2X;
    private int Zone2SizeX;
    private int Zone2Y;
    private int Zone2SizeY;
    
    private ChartAera chartAera;
    private DataAera dataAera;
    
    //ProActive colors
    private Color colorBlue = new Color(0x1d, 0x30 , 0x6b);
    private Color colorOrange = new Color(0xe7, 0x74 , 0x24);
    
    /**
     * 
     * @param sizeX X size of screen
     * @param sizeY Y size of screen
     * @param dataFile rrd4j file path
     */
    public DrawingClass(int sizeX , int sizeY , String dataFile) {
        
        clientJMX = new ClientJMX(dataFile);
        
        GRAPHIC_LENGTH = sizeX;
        GRAPHIC_HEIGHT = sizeY; 
        MID_Aera1_Aera2 = GRAPHIC_LENGTH/3;
        
        /* BackGround */
        Zone1X = MID_Aera1_Aera2;
        Zone1SizeX = GRAPHIC_LENGTH - BORDER_L_R - Zone1X;
        Zone1Y = GRAPHIC_HEIGHT - PAD_BOTTOM;
        Zone1SizeY = GRAPHIC_HEIGHT - PAD_TOP - PAD_BOTTOM;

        Zone2X = BORDER_L_R;
        Zone2SizeX = -(Zone1SizeX - GRAPHIC_LENGTH) - 2 * BORDER_L_R;
        Zone2Y = PAD_TOP;
        Zone2SizeY = Zone1SizeY;
        
        /* Chart Aera */
        chartAera = new ChartAera(
                Zone1X, Zone1Y, Zone1SizeX, Zone1SizeY , clientJMX);

        /* Data Aera */
        dataAera = new DataAera(
                Zone2X, Zone2Y, Zone2SizeX, Zone2SizeY);
        
    }
    
    /**
     * Draw border with ProActive colors
     * @param g the Graphics2D support to paint
     */
    private void drawBorder(Graphics2D g) {
        
        
        Stroke s = g.getStroke();
        
        /* Blue border */
        g.setStroke( new BasicStroke(5) );
        g.setPaint( colorBlue );
        g.drawLine(Zone2X, Zone2Y, Zone2X , Zone2Y + Zone2SizeY);
        g.drawLine(Zone2X, Zone1Y , Zone2X + Zone1SizeX + Zone2SizeX, Zone1Y);
        g.drawLine(Zone2X + Zone1SizeX + Zone2SizeX, Zone1Y, Zone2X + Zone2SizeX + Zone1SizeX, Zone2Y);
        g.drawLine(Zone2X + Zone2SizeX + Zone1SizeX, Zone2Y, Zone2X, Zone2Y);
        g.drawLine(MID_Aera1_Aera2 , Zone2Y, MID_Aera1_Aera2, Zone1Y);
        
        /* Orange border */
        g.setStroke( new BasicStroke(1) );
        g.setPaint( colorOrange );
        g.drawLine(Zone2X, Zone2Y, Zone2X , Zone2Y + Zone2SizeY);
        g.drawLine(Zone2X, Zone1Y , Zone2X + Zone1SizeX + Zone2SizeX, Zone1Y);
        g.drawLine(Zone2X + Zone1SizeX + Zone2SizeX, Zone1Y, Zone2X + Zone2SizeX + Zone1SizeX, Zone2Y);
        g.drawLine(Zone2X + Zone2SizeX + Zone1SizeX, Zone2Y, Zone2X, Zone2Y);
        g.drawLine(MID_Aera1_Aera2 , Zone2Y, MID_Aera1_Aera2, Zone1Y);
        
        g.setStroke(s);
    }
    
    /**
     * The first method, the graphic begin here.
     * @param g the Graphics2D support to paint
     */
    public void paint(Graphics2D g) {
        
        drawClock(g);
        
        if(clientJMX.runJmx()) {
           
            /* Chart Aera */
            chartAera.paint(g);

            /* Data Aera */
            dataAera.paint(g);
            
            /* Border */
            drawBorder(g);
        
        } else {
            g.drawString("No signal on JMX port...", 
                Error_X , Error_Y);
        }
    }
    
    /**
     * Display current time on the screensaver.
     * @param g the Graphics2D support to paint
     */
    private void drawClock(Graphics2D g) {
        g.setPaint(Color.BLACK);
        g.clearRect(Clock_X, Clock_Y-12, 100 , 15);
        g.setPaint( Color.WHITE );
        Calendar cal = Calendar.getInstance();
        String time = cal.get(Calendar.HOUR_OF_DAY)+"h "+cal.get(Calendar.MINUTE)+"m et "+cal.get(Calendar.SECOND)+"s";
        g.drawString(time, Clock_X , Clock_Y);
    }
}
