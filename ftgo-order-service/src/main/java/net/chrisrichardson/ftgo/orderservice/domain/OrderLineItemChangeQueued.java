package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.common.DomainEvent;

public record OrderLineItemChangeQueued(String lineItemId, int newQuantity)
	implements DomainEvent{

}
