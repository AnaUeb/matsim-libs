package org.matsim.contrib.emissions;

import org.junit.jupiter.api.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.network.NetworkUtils;

import static org.junit.jupiter.api.Assertions.*;

public class VspHbefaRoadTypeMappingTest {

	@Test
	void testSimpleMapping() {

        var mapper = new VspHbefaRoadTypeMapping();
        var link = getTestLink("", 70 / 3.6);

        var result = mapper.determineHbefaType(link);

        assertEquals("URB/MW-Nat./80", result);
    }

    private static Link getTestLink(String osmRoadType, double allowedSpeed) {

        var network = NetworkUtils.createNetwork();
        var from = network.getFactory().createNode(Id.createNodeId("from"), new Coord(0, 0));
        var to = network.getFactory().createNode(Id.createNodeId("to"), new Coord(0, 1000));
        var link = network.getFactory().createLink(Id.createLinkId("link"), from, to);
        link.setFreespeed(allowedSpeed);
        link.setCapacity(1000);
        link.setNumberOfLanes(1);
        link.getAttributes().putAttribute(NetworkUtils.ALLOWED_SPEED, allowedSpeed);
        link.getAttributes().putAttribute(NetworkUtils.TYPE, osmRoadType);

        return link;
    }

}
