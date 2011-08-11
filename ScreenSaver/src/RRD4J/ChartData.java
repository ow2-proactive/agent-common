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
package RRD4J;

import java.awt.Color;

/**
 *
 * @author pgouttef
 */
public class ChartData {
    
    //datas for chart
    private String dataSourceName;
    private Color color;

    /**
     * Constructor with 2 parameters.
     * @param dataSourcename Name of the dataSource.
     * @param color Future line color on the chart.
     */
    public ChartData(String dataSourcename, Color color) {
        this.dataSourceName = dataSourcename;
        this.color = color;
    }
    
    /**
     * getter of color
     * @return color
     */
    public Color getColor() {
        return color;
    }

    /**
     * setter of color
     * @param color 
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * getter of data source name
     * @return the name
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * setter of the data source name
     * @param dataSourceName 
     */
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    
}
