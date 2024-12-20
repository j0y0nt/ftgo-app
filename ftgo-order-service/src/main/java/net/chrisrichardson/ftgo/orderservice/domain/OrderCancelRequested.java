package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.common.DomainEvent;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderState;

public record  OrderCancelRequested(OrderState state) implements DomainEvent {

}
