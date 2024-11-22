package net.chrisrichardson.ftgo.orderservice.api.events;

import net.chrisrichardson.ftgo.common.Address;

public record OrderCreatedEvent(OrderDetails orderDetails, Address deliveryAddress, String restaurantName) 
	implements OrderDomainEvent{

}
