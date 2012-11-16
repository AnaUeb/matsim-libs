/* *********************************************************************** *
 * project: org.matsim.*
 * DumpDataAtEnd.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
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
package org.matsim.core.controler.corelisteners;

import java.io.File;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.api.experimental.facilities.ActivityFacilities;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.facilities.ActivityFacilitiesImpl;
import org.matsim.core.facilities.FacilitiesWriter;
import org.matsim.core.network.NetworkChangeEventsWriter;
import org.matsim.core.network.NetworkFactoryImpl;
import org.matsim.core.network.NetworkImpl;
import org.matsim.core.network.NetworkWriter;
import org.matsim.core.scenario.ScenarioImpl;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.households.HouseholdsWriterV10;
import org.matsim.lanes.data.v20.LaneDefinitions20;
import org.matsim.lanes.data.v20.LaneDefinitionsWriter20;

public class DumpDataAtEnd implements ShutdownListener {

	private final Scenario scenarioData;

	private final OutputDirectoryHierarchy controlerIO;

	public DumpDataAtEnd(Scenario scenarioData, OutputDirectoryHierarchy controlerIO) {
		this.scenarioData = scenarioData;
		this.controlerIO = controlerIO;
	}

	@Override
	public void notifyShutdown(ShutdownEvent event) {
		// dump plans
		new PopulationWriter(scenarioData.getPopulation(), scenarioData.getNetwork()).write(controlerIO.getOutputFilename(Controler.FILENAME_POPULATION));
		// dump network
		new NetworkWriter(scenarioData.getNetwork()).write(controlerIO.getOutputFilename(Controler.FILENAME_NETWORK));
		// dump config
		new ConfigWriter(scenarioData.getConfig()).write(controlerIO.getOutputFilename(Controler.FILENAME_CONFIG));
		// dump facilities
		ActivityFacilities facilities = ((ScenarioImpl) scenarioData).getActivityFacilities();
		if (facilities != null) {
			new FacilitiesWriter((ActivityFacilitiesImpl) facilities).write(controlerIO.getOutputFilename("output_facilities.xml.gz"));
		}
		if (((NetworkFactoryImpl) scenarioData.getNetwork().getFactory()).isTimeVariant()) {
			new NetworkChangeEventsWriter().write(controlerIO.getOutputFilename("output_change_events.xml.gz"),
					((NetworkImpl) scenarioData.getNetwork()).getNetworkChangeEvents());
		}
		if (scenarioData.getConfig().scenario().isUseHouseholds()) {
			new HouseholdsWriterV10(((ScenarioImpl) scenarioData).getHouseholds()).writeFile(controlerIO.getOutputFilename(Controler.FILENAME_HOUSEHOLDS));
		}
		if (scenarioData.getConfig().scenario().isUseLanes()) {
			new LaneDefinitionsWriter20(scenarioData.getScenarioElement(LaneDefinitions20.class)).write(controlerIO.getOutputFilename(Controler.FILENAME_LANES));
		}
		if (!event.isUnexpected()&& scenarioData.getConfig().vspExperimental().isWritingOutputEvents()) {
			File toFile = new File(	controlerIO.getOutputFilename("output_events.xml.gz"));
			File fromFile = new File(controlerIO.getIterationFilename(scenarioData.getConfig().controler().getLastIteration(), "events.xml.gz"));
			IOUtils.copyFile(fromFile, toFile);
		}
	}

}
