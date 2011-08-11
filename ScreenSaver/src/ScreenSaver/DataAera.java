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
import java.awt.Font;
import java.awt.Graphics2D;

/**
 *
 * @author philippe Gouttefarde
 */
public class DataAera {
    
    private Graphics2D g;
    
    //Graphical informations
    private int Zone2X;
    private int Zone2SizeX;
    private int Zone2Y;
    private int Zone2SizeY;
    
    private int sizeLine = 20;
    private int titleX;
    private int titleY = 20;
    
    private int startTextX = 20;
    private int startTextY = 50;
    
    //Color informations
    private Color colorBackground = new Color(238,238,238);
    private Color colorText = Color.black;
    
    //Font informations
    private Font titleFont = new Font("Arial", Font.BOLD, 17);
    private Font textFont = new Font("TimesRoman" , Font.PLAIN , 12);
    private String title = "data center";
    
    //Client JMF instance to take back JVM informations and put them on graphics2D g.
    private ClientJMX clientJMX;
    
    
    /**
     * Constructor with parameters.
     * @param g : graphics2D objecton which we will draw.
     * @param startX : X start coordonate of data aera on graphics2D.
     * @param startY : Y start coordonate of data aera on graphics2D.
     * @param sizeX : X size of data aera on graphics2D.
     * @param sizeY
     * @param titleFont
     * @param clientJMX 
     */
    public DataAera(Graphics2D g , int startX, int startY, int sizeX, int sizeY , ClientJMX clientJMX) {
        this.g = g;
        Zone2X = startX;
        Zone2SizeX = sizeX;
        Zone2Y = startY;
        Zone2SizeY = sizeY;
        titleX = Zone2SizeX/2 - 30;
        this.clientJMX = clientJMX;
    }
    /**
     *  The first method, the graphic begin here.
     */
    public void paint() {
        
        g.setPaint(colorBackground);
        g.fillRect(Zone2X, Zone2Y, Zone2SizeX,Zone2SizeY);
        
        drawTitle();
        drawText();
    }
    
    /**
     * write title on the data chart
     */
    private void drawTitle() {
        g.setPaint(colorText);
        g.setFont(titleFont);
        g.drawString(title, Zone2X + titleX, Zone2Y + titleY);
    }
    
    /**
     * Display some informations on system and JVMs.
     */
    private void drawText() {
        g.setFont(textFont);
        
        g.drawString("Operating System : " + clientJMX.getOperatingSystem(), Zone2X + startTextX, Zone2Y + 2*startTextY);
        
        g.drawString("Current Task Type : " + clientJMX.getCurrentTask(), Zone2X + startTextX, Zone2Y + startTextY + 4*sizeLine);
        
        g.drawString("Total memory : " + clientJMX.getTotalMemory() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 6*sizeLine);
        g.drawString("Free memory  : " + clientJMX.getFreeMemory() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 7*sizeLine);
        g.drawString("Total swap : " + clientJMX.getTotalSwap() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 8*sizeLine);
        g.drawString("Free swap  : " + clientJMX.getFreeSwap() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 9*sizeLine);
        
        g.drawString("Memory : ", Zone2X + startTextX, Zone2Y + startTextY + 11*sizeLine);
        g.drawString("Heap        : " + clientJMX.getMemHeap() + " Mo", Zone2X + startTextX +10, Zone2Y + startTextY + 12*sizeLine);
        g.drawString("Non Heap : " + clientJMX.getMemNonHeap() + " Mo", Zone2X + startTextX +10, Zone2Y + startTextY + 13*sizeLine);
        
        g.drawString("Nb JVM scanned : " + clientJMX.getNbJVM(), Zone2X + startTextX, Zone2Y + startTextY + 15*sizeLine);
        for(int i=0 ; i < clientJMX.getJVMNames().size() ; ++i) {
            g.drawString(i + ") " + clientJMX.getJVMNames().get(i), Zone2X + startTextX +10, Zone2Y + startTextY + (16+i)*sizeLine);
        }
        
        
        
        
    }
}
