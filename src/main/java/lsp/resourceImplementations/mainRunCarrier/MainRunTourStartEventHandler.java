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

package lsp.resourceImplementations.mainRunCarrier;

import lsp.LSPCarrierResource;
import lsp.LSPSimulationTracker;
import lsp.LogisticChainElement;
import lsp.shipment.LSPShipment;
import lsp.shipment.ShipmentLeg;
import lsp.shipment.ShipmentPlanElement;
import lsp.shipment.ShipmentUtils;
import org.matsim.api.core.v01.Id;
import org.matsim.contrib.freight.carrier.CarrierService;
import org.matsim.contrib.freight.carrier.Tour;
import org.matsim.contrib.freight.carrier.Tour.ServiceActivity;
import org.matsim.contrib.freight.carrier.Tour.TourElement;
import org.matsim.contrib.freight.events.CarrierTourStartEvent;
import org.matsim.contrib.freight.events.eventhandler.FreightTourStartEventHandler;
import org.matsim.core.controler.events.AfterMobsimEvent;
import org.matsim.core.controler.listener.AfterMobsimListener;

public class MainRunTourStartEventHandler implements AfterMobsimListener, FreightTourStartEventHandler, LSPSimulationTracker<LSPShipment> {
// Todo: I have made it (temporarily) public because of junit tests :( -- need to find another way to do the junit testing. kmt jun'23


	private final Tour tour;
	private final CarrierService carrierService;
	private final LogisticChainElement logisticChainElement;
	private final LSPCarrierResource resource;
	private LSPShipment lspShipment;


	public MainRunTourStartEventHandler(LSPShipment lspShipment, CarrierService carrierService, LogisticChainElement logisticChainElement, LSPCarrierResource resource, Tour tour) {
		this.lspShipment = lspShipment;
		this.carrierService = carrierService;
		this.logisticChainElement = logisticChainElement;
		this.resource = resource;
		this.tour = tour;
	}


	@Override
	public void reset(int iteration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleEvent(CarrierTourStartEvent event) {
		if (event.getTourId().equals(tour.getId())) {
			for (TourElement tourElement : tour.getTourElements()) {
				if (tourElement instanceof ServiceActivity serviceActivity) {
					if (serviceActivity.getService().getId() == carrierService.getId() && event.getCarrierId() == resource.getCarrier().getId()) {
						logLoad(event, tour);
						logTransport(event, tour);
					}
				}
			}
		}
	}

	private void logLoad(CarrierTourStartEvent event, Tour tour) {
		ShipmentUtils.LoggedShipmentLoadBuilder builder = ShipmentUtils.LoggedShipmentLoadBuilder.newInstance();
		builder.setCarrierId(event.getCarrierId());
		builder.setLinkId(event.getLinkId());
		builder.setStartTime(event.getTime() - getCumulatedLoadingTime(tour));
		builder.setEndTime(event.getTime());
		builder.setLogisticsChainElement(logisticChainElement);
		builder.setResourceId(resource.getId());
		ShipmentPlanElement loggedShipmentLoad = builder.build();
		String idString = loggedShipmentLoad.getResourceId() + "" + loggedShipmentLoad.getLogisticChainElement().getId() + "" + loggedShipmentLoad.getElementType();
		Id<ShipmentPlanElement> loadId = Id.create(idString, ShipmentPlanElement.class);
		lspShipment.getShipmentLog().addPlanElement(loadId, loggedShipmentLoad);
	}

	private double getCumulatedLoadingTime(Tour tour) {
		double cumulatedLoadingTime = 0;
		for (TourElement tourElement : tour.getTourElements()) {
			if (tourElement instanceof ServiceActivity serviceActivity) {
				cumulatedLoadingTime = cumulatedLoadingTime + serviceActivity.getDuration();
			}
		}
		return cumulatedLoadingTime;
	}

	private void logTransport(CarrierTourStartEvent event, Tour tour) {
		ShipmentUtils.LoggedShipmentTransportBuilder builder = ShipmentUtils.LoggedShipmentTransportBuilder.newInstance();
		builder.setCarrierId(event.getCarrierId());
		builder.setFromLinkId(event.getLinkId());
		builder.setToLinkId(tour.getEndLinkId());
		builder.setStartTime(event.getTime());
		builder.setLogisticChainElement(logisticChainElement);
		builder.setResourceId(resource.getId());
		ShipmentLeg transport = builder.build();
		String idString = transport.getResourceId() + "" + transport.getLogisticChainElement().getId() + "" + transport.getElementType();
		Id<ShipmentPlanElement> transportId = Id.create(idString, ShipmentPlanElement.class);
		lspShipment.getShipmentLog().addPlanElement(transportId, transport);
	}


	public LSPShipment getLspShipment() {
		return lspShipment;
	}


	public CarrierService getCarrierService() {
		return carrierService;
	}


	public LogisticChainElement getLogisticChainElement() {
		return logisticChainElement;
	}


	public LSPCarrierResource getResource() {
		return resource;
	}


	@Override public void setEmbeddingContainer( LSPShipment pointer ){
		this.lspShipment = pointer;
	}

	@Override public void notifyAfterMobsim( AfterMobsimEvent event ){
	}
}
