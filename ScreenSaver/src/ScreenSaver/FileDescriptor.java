/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ScreenSaver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 *
 * @author pgouttef
 */
public class FileDescriptor {
    
    private static int MAX = 500;
    
    public static void writeFile(ArrayList<ChartData> data , String dataFile) {
        
        File file = new File(dataFile);
        try{
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);

            if(data.size() >= MAX) {
                for(int i=data.size()-1 ; i > data.size()-1-MAX ; i-- ) {
                    out.write(data.get(i).toString() + "\n");
                }
            } else {
                for (ChartData tmp : data) {
                    out.write( tmp.toString()  + "\n" );
                }
            }
            
            out.close();
        } catch (Exception e) {
            
        }
    }
    
    public static ArrayList<ChartData> readFile(String dataFile) {
        
        ArrayList<ChartData> data = new ArrayList<ChartData>();
        
        File file = new File(dataFile);
        if (file.exists()) {
           try{
                FileInputStream fstream = new FileInputStream(file);

                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader( new InputStreamReader(in));
                String strLine;

                while((strLine = br.readLine()) != null) {
                    String[] str = strLine.split(" ");
                    data.add(new ChartData(
                            Long.parseLong(str[0]), Double.parseDouble(str[1]), Double.parseDouble(str[2])));
                }

                in.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } 
        }
        
        
        return data;
    }
    
    public static void main(String[] args) {
        
        Calendar cal = Calendar.getInstance();
        ArrayList<ChartData> datas = new ArrayList<ChartData>();
        for(int i=0; i < 550 ; i++) {
            ChartData ch = new ChartData();
            ch.setTime(cal.getTime().getTime());
            ch.setRAM(i);
            ch.setSwap(550-i);
            datas.add(ch);
        }
        //FileDescriptor.writeFile(datas);
        //datas = FileDescriptor.readFile();
    }
}
