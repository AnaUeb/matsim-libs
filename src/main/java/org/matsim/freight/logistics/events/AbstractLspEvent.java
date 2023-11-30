package org.matsim.freight.logistics.events;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.HasLinkId;
import org.matsim.api.core.v01.network.Link;
import org.matsim.freight.logistics.HasLspShipmentId;
import org.matsim.freight.logistics.shipment.LSPShipment;

import java.util.Map;

import static org.matsim.freight.logistics.HasLspShipmentId.ATTRIBUTE_LSP_SHIPMENT_ID;

/**
 * A general logistic event contains the information (= {@link Id}) of the - the location (= {@link
 * Link}) - the lspShipment (= {@link LSPShipment}) belonging to it.
 *
 * <p>Please note, that general _freight_ events can be found in the freight contrib.
 *
 * @author Kai Martins-Turner (kturner)
 */
public abstract class AbstractLspEvent extends Event implements HasLinkId, HasLspShipmentId {

  private final Id<Link> linkId;
  private final Id<LSPShipment> lspShipmentId;

  public AbstractLspEvent(double time, Id<Link> linkId, Id<LSPShipment> lspShipmentId) {
    super(time);
    this.linkId = linkId;
    this.lspShipmentId = lspShipmentId;
  }

  /**
   * @return id of the {@link LSPShipment}
   */
  @Override
  public final Id<LSPShipment> getLspShipmentId() {
    return lspShipmentId;
  }

  @Override
  public final Id<Link> getLinkId() {
    return linkId;
  }

  /**
   * Adds the {@link Id<LSPShipment>} to the list of attributes. {@link Id<Link>} is handled by
   * superclass {@link Event}
   *
   * @return The map of attributes
   */
  @Override
  public Map<String, String> getAttributes() {
    Map<String, String> attr = super.getAttributes();
    attr.put(ATTRIBUTE_LSP_SHIPMENT_ID, lspShipmentId.toString());
    return attr;
  }
}
