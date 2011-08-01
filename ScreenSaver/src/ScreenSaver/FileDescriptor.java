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
