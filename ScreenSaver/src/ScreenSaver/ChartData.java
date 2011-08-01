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

import java.util.Calendar;

/**
 *
 * @author pgouttef
 */
public class ChartData {
    
    //datas for chart
    private long time;
    private double RAM;
    private double swap;

    /**
     * 3 parameters constructor 
     * @param t time in long format
     * @param R RAM used
     * @param s Swap used 
     */
    public ChartData(long t, double R , double s){
        time = t;
        RAM = R;
        swap = s;
    }
    
    /**
     * 2 parameters constructor 
     * @param R RAM used
     * @param s Swap used 
     */
    public ChartData(double R , double s){
        
        Calendar cal = Calendar.getInstance();
        time = cal.getTime().getTime();
        RAM = R;
        swap = s;
    }

    /**
     * Default constructor
     */
    public ChartData() {
    }
    
    /**
     * getter RAM
     * @return RAM value 
     */
    public double getRAM() {
        return RAM;
    }

    /**
     * setter RAM
     * @param RAM : RAM value 
     */
    public void setRAM(double RAM) {
        this.RAM = RAM;
    }

    /**
     * getter swap
     * @return swap value 
     */
    public double getSwap() {
        return swap;
    }

    /**
     * setter swap
     * @param swap : swap value
     */
    public void setSwap(double swap) {
        this.swap = swap;
    }

    /**
     * getter time
     * @return time value
     */
    public long getTime() {
        return time;
    }

    /**
     * setter time 
     * @param time : time value
     */
    public void setTime(long time) {
        this.time = time;
    }
    
    /**
     * 
     * @return String format for variables 
     */
    @Override
    public String toString() {
        return time + " " + RAM + " " + swap;
    }
}
