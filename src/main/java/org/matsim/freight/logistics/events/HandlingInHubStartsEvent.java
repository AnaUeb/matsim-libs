/*
 *   *********************************************************************** *
 *   project: org.matsim.*
 *   *********************************************************************** *
 *                                                                           *
 *   copyright       : (C)  by the members listed in the COPYING,        *
 *                     LICENSE and WARRANTY file.                            *
 *   email           : info at matsim dot org                                *
 *                                                                           *
 *   *********************************************************************** *
 *                                                                           *
 *     This program is free software; you can redistribute it and/or modify  *
 *     it under the terms of the GNU General Public License as published by  *
 *     the Free Software Foundation; either version 2 of the License, or     *
 *     (at your option) any later version.                                   *
 *     See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                           *
 *   ***********************************************************************
 *
 */

package org.matsim.freight.logistics.events;


import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.freight.logistics.LSPResource;
import org.matsim.freight.logistics.shipment.LSPShipment;
import java.util.Map;
import static org.matsim.freight.logistics.events.LspEventAttributes.ATTRIBUTE_EXP_HANDLING_DURATION;
import static org.matsim.freight.logistics.events.LspEventAttributes.ATTRIBUTE_HUB_ID;

/**
 * An event, that informs that the handling of a {@link LSPShipment}  in a hub has started.
 *
 * @author Kai Martins-Turner (kturner)
 */
public final class HandlingInHubStartsEvent extends AbstractLspEvent {

	public static final String EVENT_TYPE = "Handling_started";
	private final Id<LSPResource> hubId;
	private final double expHandlingDuration;

	//TODO: Add more information about the handling, e.g. expected duration of handling.  KMT after mtg with kn, jul' 23

	public HandlingInHubStartsEvent(double time, Id<Link> linkId, Id<LSPShipment> lspShipmentId, Id<LSPResource> hubId, double expHandlingDuration) {
		super(time, linkId, lspShipmentId);
		this.hubId = hubId;
		this.expHandlingDuration = expHandlingDuration;
	}

	@Override public String getEventType() {
		return EVENT_TYPE;
	}

	public Id<LSPResource> getHubId() {
		return hubId;
	}

	public double getExpHandlingDuration(){
		return expHandlingDuration;
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attr = super.getAttributes();
		attr.put(ATTRIBUTE_HUB_ID, hubId.toString());
		attr.put(ATTRIBUTE_EXP_HANDLING_DURATION, String.valueOf(expHandlingDuration));
		return attr;
	}

}
