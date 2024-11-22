package net.chrisrichardson.ftgo.orderservice.api.web;

import java.time.LocalDateTime;
import java.util.List;

import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.RevisedOrderLineItem;

public interface FtgoTypes {

	record LineItem(String menuItemId, int quantity) {
	}

	record CreateOrderRequest(long restaurantId, long consumerId,
			LocalDateTime deliveryTime, List<LineItem> lineItems,
			Address deliveryAddress) {
	}

	record CreateOrderResponse(long orderId) {
	}

	record ReviseOrderRequest(List<RevisedOrderLineItem> revisedOrderLineItems) {
	}

}
