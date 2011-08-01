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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class ScreenSaver {
    
    public static void createBMP( String fileName , String template , String dataFile ) {
        
        try {
            BufferedImage bitmap = ImageIO.read( new File( template )); 
           
            System.out.println("Starting screensaver..");
            DrawingClass draw = new DrawingClass(bitmap.getWidth(),bitmap.getHeight() , dataFile);
            
            
            
            //BufferedImage bitmap = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = bitmap.createGraphics();
            
            
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
        } catch (IOException ex) {
			Logger.getLogger(ScreenSaver.class.getName()).log(Level.SEVERE, null, ex);
		}
    }
    
    public static void main( String[] argv ) {
        
        String imgFile;
        String imgFileTemplate;
        String dataFile;
        if(argv.length == 3) {
            
            imgFile = argv[0];
            dataFile = argv[1];
            imgFileTemplate = argv[2];
            
        } else if(argv.length == 2) {
            
            imgFile = argv[0];
            dataFile = argv[1];
            imgFileTemplate = "/home/pgouttef/Stage/Images/picture/ScreenSaverTemplate.bmp";
            
        } else {
            
            imgFile = "/tmp/test.png";
            dataFile = "/tmp/ScreenSaverData.txt";
            imgFileTemplate = "/home/pgouttef/Stage/Images/picture/ScreenSaverTemplate.bmp";
            
        }
        ScreenSaver.createBMP( imgFile , imgFileTemplate , dataFile );
    }
}

