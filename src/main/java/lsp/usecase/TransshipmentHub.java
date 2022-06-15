/*
 *  *********************************************************************** *
 *  * project: org.matsim.*
 *  * *********************************************************************** *
 *  *                                                                         *
 *  * copyright       : (C) 2022 by the members listed in the COPYING,        *
 *  *                   LICENSE and WARRANTY file.                            *
 *  * email           : info at matsim dot org                                *
 *  *                                                                         *
 *  * *********************************************************************** *
 *  *                                                                         *
 *  *   This program is free software; you can redistribute it and/or modify  *
 *  *   it under the terms of the GNU General Public License as published by  *
 *  *   the Free Software Foundation; either version 2 of the License, or     *
 *  *   (at your option) any later version.                                   *
 *  *   See also COPYING, LICENSE and WARRANTY file                           *
 *  *                                                                         *
 *  * ***********************************************************************
 */

package lsp.usecase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lsp.LSPDataObject;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.events.handler.EventHandler;

import lsp.LogisticsSolutionElement;
import lsp.LSPResource;
import lsp.controler.LSPSimulationTracker;
import org.matsim.utils.objectattributes.attributable.Attributes;

/**
 * {@link LSPResource} bei der die geplanten Tätigkeiten NICHT am Verkehr teilnehmen.
 *
 * Thus, these activities are entered directly in the Schedule of the LSPShipments that pass through the TranshipmentHub.
 *
 * An entry is added to the schedule of the shipments that is an instance of
 * {@link lsp.shipment.ScheduledShipmentHandle}. There, the name of the Resource
 * and the client element are entered, so that the way that the {@link lsp.shipment.LSPShipment}
 * takes is specified. In addition, the planned start and end time of the handling
 * (i.e. crossdocking) of the shipment is entered. In the example, crossdocking
 * starts as soon as the considered LSPShipment arrives at the {@link TransshipmentHub}
 * and ends after a fixed and a size dependent amount of time.
 */
/*package-private*/ class TransshipmentHub extends LSPDataObject<LSPResource> implements LSPResource {

	private final Id<LSPResource> id;
	private final Id<Link> locationLinkId;
	private final TransshipmentHubScheduler transshipmentHubScheduler;
	private final List<LogisticsSolutionElement> clientElements;

	TransshipmentHub(UsecaseUtils.TransshipmentHubBuilder builder){
		this.id = builder.getId();
		this.locationLinkId = builder.getLocationLinkId();
		this.transshipmentHubScheduler = builder.getTransshipmentHubScheduler();
		transshipmentHubScheduler.setTranshipmentHub(this);
		TranshipmentHubTourEndEventHandler eventHandler = new TranshipmentHubTourEndEventHandler(this);
		transshipmentHubScheduler.setEventHandler(eventHandler);
		this.clientElements = builder.getClientElements();
		this.addSimulationTracker( eventHandler );
	}
	
	@Override
	public Id<Link> getStartLinkId() {
		return locationLinkId;
	}

	@Override
	public Id<Link> getEndLinkId() {
		return locationLinkId;
	}

	@Override
	public Collection<LogisticsSolutionElement> getClientElements() {
		return clientElements;
	}

	@Override
	public Id<LSPResource> getId() {
		return id;
	}

	@Override
	public void schedule(int bufferTime) {
		transshipmentHubScheduler.scheduleShipments(this, bufferTime);
	}

	public double getCapacityNeedFixed(){
		return transshipmentHubScheduler.getCapacityNeedFixed();
	}

	public double getCapacityNeedLinear(){
		return transshipmentHubScheduler.getCapacityNeedLinear();
	}

}
