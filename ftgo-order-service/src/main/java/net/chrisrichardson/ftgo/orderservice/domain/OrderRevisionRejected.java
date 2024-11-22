package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.common.DomainEvent;

public record OrderRevisionRejected(OrderRevision orderRevision) 
	implements DomainEvent{

}
