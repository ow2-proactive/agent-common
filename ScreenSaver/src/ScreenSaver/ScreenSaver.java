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

