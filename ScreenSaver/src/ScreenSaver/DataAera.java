/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ScreenSaver;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 *
 * @author pgouttef
 */
public class DataAera {
    
    private Graphics2D g;
    
    private int Zone2X;
    private int Zone2SizeX;
    private int Zone2Y;
    private int Zone2SizeY;
    
    private Color colorBackground = new Color(238,238,238);
    private Color colorText = Color.black;
    private int sizeLine = 20;
    private int titleX;
    private int titleY = 20;
    
    private Font titleFont;
    private Font textFont = new Font("TimesRoman" , Font.PLAIN , 12);
    
    private int startTextX = 20;
    private int startTextY = 50;
    
    private ClientJMX clientJMX;
    
    
    public DataAera(Graphics2D g , int startX, int startY, int sizeX, int sizeY , Font titleFont , ClientJMX clientJMX) {
        this.g = g;
        Zone2X = startX;
        Zone2SizeX = sizeX;
        Zone2Y = startY;
        Zone2SizeY = sizeY;
        titleX = Zone2SizeX/2 - 30;
        this.titleFont = titleFont;
        this.clientJMX = clientJMX;
    }
    
    public void paint() {
        
        g.setPaint(colorBackground);
        g.fillRect(Zone2X, Zone2Y, Zone2SizeX,Zone2SizeY);
        
        drawTitle();
        drawText();
    }
    
    private void drawTitle() {
        String title = "data center";
        g.setPaint(colorText);
        g.setFont(titleFont);
        g.drawString(title, Zone2X + titleX, Zone2Y + titleY);
    }
    
    private void drawText() {
        g.setFont(textFont);
        
        g.drawString("Operating System : " + clientJMX.getOperatingSystem(), Zone2X + startTextX, Zone2Y + 2*startTextY);
        
        g.drawString("JVM name : " + clientJMX.getJVMName(), Zone2X + startTextX, Zone2Y + startTextY + 4*sizeLine);
        g.drawString("JVM start at : " + clientJMX.getStartTime(), Zone2X + startTextX, Zone2Y + startTextY + 5*sizeLine);
        
        g.drawString("Total memory : " + clientJMX.getTotalMemory() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 7*sizeLine);
        g.drawString("Free memory  : " + clientJMX.getFreeMemory() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 8*sizeLine);
        g.drawString("Total swap : " + clientJMX.getTotalSwap() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 9*sizeLine);
        g.drawString("Free swap  : " + clientJMX.getFreeSwap() + " Mo", Zone2X + startTextX, Zone2Y + startTextY + 10*sizeLine);
        
        g.drawString("Memory : ", Zone2X + startTextX, Zone2Y + startTextY + 12*sizeLine);
        g.drawString("Heap        : " + clientJMX.getMemHeap() + " Mo", Zone2X + startTextX +10, Zone2Y + startTextY + 13*sizeLine);
        g.drawString("Non Heap : " + clientJMX.getMemNonHeap() + " Mo", Zone2X + startTextX+10, Zone2Y + startTextY + 14*sizeLine);
        
        g.drawString("Current Task Type : " + clientJMX.getCurrentTask(), Zone2X + startTextX, Zone2Y + startTextY + 16*sizeLine);
    }
}
