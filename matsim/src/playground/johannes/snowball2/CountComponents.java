/* *********************************************************************** *
 * project: org.matsim.*
 * CountComponents.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

/**
 * 
 */
package playground.johannes.snowball2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.matsim.utils.io.IOUtils;

import edu.uci.ics.jung.algorithms.cluster.ClusterSet;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author illenberger
 *
 */
public class CountComponents implements GraphStatistic {
	
	private WeakComponentClusterer wcc = new WeakComponentClusterer();
	
	private ClusterSet cSet;

	public double run(Graph g) {
		cSet = wcc.extract(g);
		return cSet.size();
	}

	public ClusterSet getClusterSet() {
		return cSet;
	}
	
	public void dumpComponentSummary(String filename) {
		if(cSet != null) {
			Map<Integer, Integer> clusters = new HashMap<Integer, Integer>();
			for(int i = 0; i < cSet.size(); i++) {
				int size = cSet.getCluster(i).size();
				Integer count = clusters.get(size);
				if(count == null)
					count = 0;
				count++;
				clusters.put(size, count);
			}
			
			try {
			BufferedWriter writer = IOUtils.getBufferedWriter(filename);
			for(Integer size : clusters.keySet()) {
				writer.write(String.valueOf(clusters.get(size)));
				writer.write(" x size ");
				writer.write(String.valueOf(size));
				writer.newLine();
			}
			writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
