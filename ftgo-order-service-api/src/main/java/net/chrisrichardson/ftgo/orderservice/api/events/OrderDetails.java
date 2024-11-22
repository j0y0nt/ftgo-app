package net.chrisrichardson.ftgo.orderservice.api.events;

import java.util.List;

import net.chrisrichardson.ftgo.common.Money;

public record OrderDetails(
		long consumerId,
		long restaurantId,
		List<OrderLineItem> lineItems,
		Money orderTotal) {

}
