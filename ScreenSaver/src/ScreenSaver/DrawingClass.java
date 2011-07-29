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
    
    private int GRAPHIC_LENGTH = 800;
    private int GRAPHIC_HEIGHT = 600; 
    private int BORDER_L_R = 10;
    private int PAD_TOP = 200;
    private int PAD_BOTTOM = 100;
    
    private int MID_Aera1_Aera2 = GRAPHIC_LENGTH/3;
    
    private int Clock_X = 160;
    private int Clock_Y = 120;
    
    private int Error_X = 300;
    private int Error_Y = 300;
    
    private ClientJMX clientJMX = new ClientJMX();
    private boolean isJMXRunning;
    
    /*
     * Constructor
     */
    public DrawingClass(String dataFile) throws HeadlessException {
        clientJMX.runJmx(dataFile);
    }
    
    /*
     * Constructor
     */
    public DrawingClass(int sizeX , int sizeY , String dataFile) throws HeadlessException {
        
        GRAPHIC_LENGTH = sizeX;
        GRAPHIC_HEIGHT = sizeY; 
        MID_Aera1_Aera2 = GRAPHIC_LENGTH/3;
        
        isJMXRunning = clientJMX.runJmx(dataFile);
    }
    
    public void paint(Graphics2D g) {
        
        drawClock(g);
        
        /* BackGround */
        int Zone1X = MID_Aera1_Aera2;
        int Zone1SizeX = GRAPHIC_LENGTH - BORDER_L_R - Zone1X;
        int Zone1Y = GRAPHIC_HEIGHT - PAD_BOTTOM;
        int Zone1SizeY = GRAPHIC_HEIGHT - PAD_TOP - PAD_BOTTOM;
        
        int Zone2X = BORDER_L_R;
        int Zone2SizeX = -(Zone1SizeX - GRAPHIC_LENGTH) - 2 * BORDER_L_R;
        int Zone2Y = PAD_TOP;
        int Zone2SizeY = GRAPHIC_HEIGHT - PAD_TOP - PAD_BOTTOM;
        
        if(isJMXRunning) {
           
            /* Chart Aera */
            ChartAera chartAera = new ChartAera(
                    g, Zone1X, Zone1Y, Zone1SizeX, Zone1SizeY , clientJMX);
            chartAera.paint();

            /* Data Aera */
            DataAera dataAera = new DataAera(
                    g, Zone2X, Zone2Y, Zone2SizeX, Zone2SizeY , chartAera.getTitleFont() , clientJMX );
            dataAera.paint();
        } else {
            g.drawString("No signal on JMX port...", 
                Error_X , Error_Y);
        }
    }
    
    private void drawClock(Graphics2D g) {
        g.setPaint( Color.white );
        Calendar cal = Calendar.getInstance();
        g.drawString(cal.get(Calendar.HOUR_OF_DAY)+"h "+cal.get(Calendar.MINUTE)+"m et "+cal.get(Calendar.SECOND)+"s", 
                Clock_X , Clock_Y);
    }
}
