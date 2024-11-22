package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDomainEvent;

public record OrderRevisionProposed (
		OrderRevision orderRevision,
		Money currentOrderTotal,
		Money newOrderTotal) implements OrderDomainEvent {

}
