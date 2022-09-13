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

import com.google.inject.Inject;
import lsp.LSPSimulationTracker;
import lsp.shipment.*;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.freight.carrier.Carrier;
import org.matsim.contrib.freight.carrier.CarrierService;
import org.matsim.contrib.freight.carrier.ScheduledTour;
import org.matsim.contrib.freight.carrier.Tour;
import org.matsim.contrib.freight.carrier.Tour.ServiceActivity;
import org.matsim.contrib.freight.carrier.Tour.TourElement;

import org.matsim.contrib.freight.events.FreightTourStartEvent;
import org.matsim.contrib.freight.events.eventhandler.FreightTourStartEventHandler;
import lsp.LogisticsSolutionElement;
import lsp.LSPCarrierResource;
import org.matsim.contrib.freight.utils.FreightUtils;
import org.matsim.core.controler.events.AfterMobsimEvent;
import org.matsim.core.controler.listener.AfterMobsimListener;
import org.matsim.core.events.handler.EventHandler;

import java.util.ArrayList;
import java.util.Collection;

/*package-private*/ class MainRunTourStartEventHandler implements AfterMobsimListener, FreightTourStartEventHandler, LSPSimulationTracker<LSPShipment> {

	private final Tour tour;
	private final CarrierService carrierService;
	private final LogisticsSolutionElement solutionElement;
	private final LSPCarrierResource resource;
	private final Collection<EventHandler> eventHandlers = new ArrayList<>();
	private LSPShipment lspShipment;


	public MainRunTourStartEventHandler(LSPShipment lspShipment, CarrierService carrierService, LogisticsSolutionElement solutionElement, LSPCarrierResource resource, Tour tour) {
		this.lspShipment = lspShipment;
		this.carrierService = carrierService;
		this.solutionElement = solutionElement;
		this.resource = resource;
		this.tour = tour;
	}


	@Override
	public void reset(int iteration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(FreightTourStartEvent event) {
//		Tour tour = null;
//		//TODO: Does not work, because scenario is null -> Need help from KN :(
//		// In the CarrierModul there is already a CarrierProvider returning "return FreightUtils.getCarriers(scenario);" --> How can I access it???
//		// OR
//		// LSPModule -> provideCarriers ??
//		Carrier carrier = FreightUtils.getCarriers(scenario).getCarriers().get(event.getCarrierId());
//		Collection<ScheduledTour> scheduledTours = carrier.getSelectedPlan().getScheduledTours();
//		for (ScheduledTour scheduledTour : scheduledTours) {
//			if (scheduledTour.getVehicle().getId() == event.getVehicleId()) {
//				tour = scheduledTour.getTour();
//				break;
//			}
//		}
		for (TourElement tourElement : tour.getTourElements()) {
			if (tourElement instanceof ServiceActivity serviceActivity) {
				if (serviceActivity.getService().getId() == carrierService.getId() && event.getCarrierId() == resource.getCarrier().getId()) {
					logLoad(event);
					logTransport(event);
				}
			}
		}

	}

	private void logLoad(FreightTourStartEvent event) {
		ShipmentUtils.LoggedShipmentLoadBuilder builder = ShipmentUtils.LoggedShipmentLoadBuilder.newInstance();
		builder.setCarrierId(event.getCarrierId());
		builder.setLinkId(event.getLinkId());
//		Tour tour = null;
//		//TODO: Does not work, because scenario is null -> Need help from KN :(
//		// In the CarrierModul there is already a CarrierProvider returning "return FreightUtils.getCarriers(scenario);" --> How can I access it???
//		// OR
//		// LSPModule -> provideCarriers ??
//		Carrier carrier = FreightUtils.getCarriers(scenario).getCarriers().get(event.getCarrierId());
//		Collection<ScheduledTour> scheduledTours = carrier.getSelectedPlan().getScheduledTours();
//		for (ScheduledTour scheduledTour : scheduledTours) {
//			if (scheduledTour.getVehicle().getId() == event.getVehicleId()) {
//				tour = scheduledTour.getTour();
//				break;
//			}
//		}
		double startTime = event.getTime() - getCumulatedLoadingTime(tour);
		builder.setStartTime(startTime);
		builder.setEndTime(event.getTime());
		builder.setLogisticsSolutionElement(solutionElement);
		builder.setResourceId(resource.getId());
		ShipmentPlanElement loggedShipmentLoad = builder.build();
		String idString = loggedShipmentLoad.getResourceId() + "" + loggedShipmentLoad.getSolutionElement().getId() + "" + loggedShipmentLoad.getElementType();
		Id<ShipmentPlanElement> loadId = Id.create(idString, ShipmentPlanElement.class);
		lspShipment.getLog().addPlanElement(loadId, loggedShipmentLoad);
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

	private void logTransport(FreightTourStartEvent event) {
		ShipmentUtils.LoggedShipmentTransportBuilder builder = ShipmentUtils.LoggedShipmentTransportBuilder.newInstance();
		builder.setCarrierId(event.getCarrierId());
		builder.setFromLinkId(event.getLinkId());
//		Tour tour = null;
//		//TODO: Does not work, because scenario is null -> Need help from KN :(
//		// In the CarrierModul there is already a CarrierProvider returning "return FreightUtils.getCarriers(scenario);" --> How can I access it???
//		// OR
//		// LSPModule -> provideCarriers ??
//		Carrier carrier = FreightUtils.getCarriers(scenario).getCarriers().get(event.getCarrierId());
//		Collection<ScheduledTour> scheduledTours = carrier.getSelectedPlan().getScheduledTours();
//		for (ScheduledTour scheduledTour : scheduledTours) {
//			if (scheduledTour.getVehicle().getId() == event.getVehicleId()) {
//				tour = scheduledTour.getTour();
//				break;
//			}
//		}
		builder.setToLinkId(tour.getEndLinkId());
		builder.setStartTime(event.getTime());
		builder.setLogisticsSolutionElement(solutionElement);
		builder.setResourceId(resource.getId());
		ShipmentLeg transport = builder.build();
		String idString = transport.getResourceId() + "" + transport.getSolutionElement().getId() + "" + transport.getElementType();
		Id<ShipmentPlanElement> transportId = Id.create(idString, ShipmentPlanElement.class);
		lspShipment.getLog().addPlanElement(transportId, transport);
	}


	public LSPShipment getLspShipment() {
		return lspShipment;
	}


	public CarrierService getCarrierService() {
		return carrierService;
	}


	public LogisticsSolutionElement getSolutionElement() {
		return solutionElement;
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
