/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
