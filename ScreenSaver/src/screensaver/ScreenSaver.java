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
package screensaver;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.RootLogger;

/**
 *
 * @author philippe Gouttefarde
 */

public class ScreenSaver {

    private final static RootLogger logger = (RootLogger) Logger.getRootLogger();
    //ProActive picture resource
    private static URL imageURL = ScreenSaver.class.getResource("ActiveEon.png");
    private static ImageIcon icon = new ImageIcon(imageURL);
    /**
     * Initialize the Graphics2D object with background,resources and copyright.
     * 
     * @param g the graphics2D object.
     * @param x width of the picture.
     * @param y height of the picture.
     * @return The Graphics2D initialized.
     */
    public static Graphics2D init(Graphics2D g , int x , int y) {

        g.setPaint( Color.BLACK );
        g.clearRect(0, 0, x, y);
        
        g.drawImage(icon.getImage(), 30, 30, null);
        
        String copyright1 =  "Copyright (C) 1997-2011 INRIA/University of";
        String copyright2 =  "Nice-Sophia Antipolis/ActiveEon";
        g.setPaint( Color.WHITE );
        g.drawString( copyright1 , 30 , y-30 );
        g.drawString( copyright2 , 108 , y-15 );
        g.drawImage(icon.getImage(), 30, 30, null);
        
        g.setFont( new Font("Arial" , Font.PLAIN , 12));
        g.addRenderingHints( new RenderingHints( 
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        
        return g;
    }
    
    /**
     * The main method of the application. 
     * It load resources, initialize the picture, launch JVM and rrd4j process.
     * It generates some pictures and collapse their.
     * Create a picture file in bmp format.
     * 
     * @param pictureFile The path where the picture will be create.
     * @param dataFile The database path.
     * @param x Width of the screen.
     * @param y Height of the screen.
     */
    public static void createBMP( String pictureFile , String dataFile, int x, int y ) {

        logger.info("Starting screensaver..");
        
        if(x > 0 && y > 0) {
            BufferedImage bitmap = new BufferedImage(x, y, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D template = bitmap.createGraphics();
            Graphics2D g = bitmap.createGraphics();
            
            DrawingClass draw = new DrawingClass(bitmap.getWidth(),bitmap.getHeight() , dataFile);

            
            //new File(dataFile).delete();
            new File(pictureFile).delete();

            template = init(template,x,y);
            /*
             * Drawing loop
             */
            while(true) {
                // The draw method
                g = (Graphics2D) template.create(0, 0, x, y);
                draw.paint(  g  );
                try {
                    ImageIO.write(bitmap, "BMP", new File( pictureFile ));

                    Calendar cal = Calendar.getInstance();
                    String time = cal.get(Calendar.HOUR_OF_DAY)+"h "+cal.get(Calendar.MINUTE)+"m et "+cal.get(Calendar.SECOND)+"s";
                } catch (IOException e) {
                    logger.error("bug during creating picture.");
                }

            }
        } else {
            logger.error("Coordonate X and Y have to be positive.");
            logger.error("Found X: " + x + " and Y: " + y);
            logger.error("Exit...");
        }
    } 
    
    
    /**
     * The entry point of screensaver.
     * @param argv :
     *           // BMP file path
     *           argv[0] 
     *           // rrd4j file path
     *           argv[1] 
     *           // X size of screen
     *           argv[2]
     *           // Y size of screen
     *           argv[3]
     *           // lol4j file path
     *           argv[4]
     */
    public static void main( String[] argv ) {

        if(argv.length == 5) {

            PropertyConfigurator.configure(argv[4]);
            logger.info("main()");

            logger.info("4 parameters checked [OK]");
            ScreenSaver.createBMP( 
                    // BMP file path
                    argv[0] , 
                    // rrd4j file path
                    argv[1] , 
                    // X size of screen
                    Integer.parseInt(argv[2]) ,
                    // Y size of screen
                    Integer.parseInt(argv[3]) );
        }
    }
}

