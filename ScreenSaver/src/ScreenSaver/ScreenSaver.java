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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ScreenSaver {
    
    public static void createBMP( String fileName , String dataFile , int x , int y ) {
        
        System.out.println("Starting screensaver..");
        
        URL imageURL = ScreenSaver.class.getResource("ActiveEon.png");
        ImageIcon icon = null;
        if( imageURL != null) {
            icon = new ImageIcon(imageURL);
        }
        
        BufferedImage bitmap = new BufferedImage(x, y, BufferedImage.TYPE_3BYTE_BGR);

        DrawingClass draw = new DrawingClass(bitmap.getWidth(),bitmap.getHeight() , dataFile);



        //BufferedImage bitmap = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = bitmap.createGraphics();

        g.setPaint( Color.BLACK );
        g.drawRect(0, 0, x, y);
        
        String copyright1 =  "Copyright (C) 1997-2011 INRIA/University of";
        String copyright2 =  "Nice-Sophia Antipolis/ActiveEon";
        g.setPaint( Color.WHITE );
        g.drawString( copyright1 , 30 , y-30 );
        g.drawString( copyright2 , 108 , y-15 );
        g.drawImage(icon.getImage(), 30, 30, null);


        g.setFont( new Font("Arial" , Font.PLAIN , 12));
        g.addRenderingHints( new RenderingHints( 
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        //g.setColor( draw.getBackgroundColor() );
        //g.fillRect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        draw.paint(g);

        g.dispose();

        try {
                    ImageIO.write(bitmap, "bmp", new File( fileName ));
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
            
        } 
    
    public static void main( String[] argv ) {
        
        String imgFile;
        String dataFile;
        int x,y;
        if(argv.length == 4) {
            
            imgFile = argv[0];
            dataFile = argv[1];
            x = Integer.parseInt(argv[2]);
            y = Integer.parseInt(argv[3]);
            
            ScreenSaver.createBMP( imgFile , dataFile , x , y );
            
        }
    }
}

