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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.util.Calendar;

/**
 *
 * @author philippe Gouttefarde
 */
public class DrawingClass{

    /*
     * The chart. Display the events list on the screen. All coordonate are generated dynamically.
     */
    private Graphics2D g;
    
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
    
    private ClientJMX clientJMX = new ClientJMX();
    private boolean isJMXRunning = false;
    
    /* BackGround */
    private int Zone1X;
    private int Zone1SizeX;
    private int Zone1Y;
    private int Zone1SizeY;
        
    private int Zone2X;
    private int Zone2SizeX;
    private int Zone2Y;
    private int Zone2SizeY;
    
    private String dataFile;
    private ChartAera chartAera;
    private DataAera dataAera;
    
    /*
     * Constructor
     */
    public DrawingClass(Graphics2D g, int sizeX , int sizeY , String dataFile) throws HeadlessException {
        
        this.g = g;
        
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
        Zone2SizeY = GRAPHIC_HEIGHT - PAD_TOP - PAD_BOTTOM;
        
        /* Chart Aera */
        chartAera = new ChartAera(
                g, Zone1X, Zone1Y, Zone1SizeX, Zone1SizeY , clientJMX);

        /* Data Aera */
        dataAera = new DataAera(
                g, Zone2X, Zone2Y, Zone2SizeX, Zone2SizeY , clientJMX );
        
        this.dataFile = dataFile;
    }
    
    /**
     *  The first method, the graphic begin here.
     */
    public void paint() {
        
        drawClock();
        
        isJMXRunning = clientJMX.runJmx(dataFile);
        if(isJMXRunning) {
           
            /* Chart Aera */
            chartAera.paint();

            /* Data Aera */
            dataAera.paint();
        
        } else {
            g.drawString("No signal on JMX port...", 
                Error_X , Error_Y);
        }
    }
    
    /**
     * Display current time on the screensaver.
     */
    private void drawClock() {
        g.setPaint( Color.white );
        Calendar cal = Calendar.getInstance();
        g.drawString(cal.get(Calendar.HOUR_OF_DAY)+"h "+cal.get(Calendar.MINUTE)+"m et "+cal.get(Calendar.SECOND)+"s", 
                Clock_X , Clock_Y);
    }
}
