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

import Model.Model;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 *
 * @author philippe Gouttefarde
 */
public class DataAera {
    
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
    
    /**
     * Constructor with parameters.
     * @param startX : X start coordonate of data aera on graphics2D.
     * @param startY : Y start coordonate of data aera on graphics2D.
     * @param sizeX : X size of data aera on graphics2D.
     * @param sizeY : Y size of data aera on graphics2D.
     */
    public DataAera(int startX, int startY, int sizeX, int sizeY) {
        Zone2X = startX;
        Zone2SizeX = sizeX;
        Zone2Y = startY;
        Zone2SizeY = sizeY;
        titleX = Zone2SizeX/2 - 30;
    }
    /**
     *  The first method, the graphic begin here.
     */
    public void paint(Graphics2D g) {
        
        g.setPaint(colorBackground);
        g.fillRect(Zone2X, Zone2Y, Zone2SizeX,Zone2SizeY);
        
        drawTitle(g);
        drawText(g);
    }
    
    /**
     * write title on the data chart
     * @param g the Graphics2D support to paint
     */
    private void drawTitle(Graphics2D g) {
        g.setPaint(colorText);
        g.setFont(titleFont);
        g.drawString(title, Zone2X + titleX, Zone2Y + titleY);
    }
    
    /**
     * Display some informations on system and JVMs.
     * @param g the Graphics2D support to paint
     */
    private void drawText(Graphics2D g) {
        g.setFont(textFont);
        
        g.drawString("Operating System : " + Model.getOperatingSystem(), Zone2X + startTextX, Zone2Y + 2*startTextY);
        
        g.drawString("Current Task Type : " + Model.getCurrentTask(), Zone2X + startTextX, Zone2Y + startTextY + 4*sizeLine);
        
        g.drawString("Total memory : " + Model.getTotalMemory() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 6*sizeLine);
        g.drawString("Free memory  : " + Model.getFreeMemory() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 7*sizeLine);
        g.drawString("Total swap : " + Model.getTotalSwap() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 8*sizeLine);
        g.drawString("Free swap  : " + Model.getFreeSwap() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 9*sizeLine);
        
        g.drawString("Memory : ", Zone2X + startTextX, Zone2Y + startTextY + 11*sizeLine);
        g.drawString("Heap        : " + Model.getMemHeap() + " Mo", Zone2X + startTextX +10, Zone2Y + startTextY + 12*sizeLine);
        g.drawString("Non Heap : " + Model.getMemNonHeap() + " Mo", Zone2X + startTextX +10, Zone2Y + startTextY + 13*sizeLine);
        
        g.drawString("Nb JVM scanned : " + Model.getJVMs().size(), Zone2X + startTextX, Zone2Y + startTextY + 15*sizeLine);
        for(int i=0 ; i < Model.getJVMs().size() ; ++i) {
            g.drawString(i + ") " + Model.getJVMs().get(i).getName(), Zone2X + startTextX +10, Zone2Y + startTextY + (16+i)*sizeLine);
        }
    }
}
