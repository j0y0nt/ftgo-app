package net.chrisrichardson.ftgo.orderservice.sagas.cancelorder;

import net.chrisrichardson.ftgo.common.Money;

public record CancelOrderSagaData(
		Long orderId,
		String reverseRequestId,
		Long restaurantId,
		long consumerId,
		Money orderTotal) {

	public CancelOrderSagaData(Long orderId, long consumerId, Money orderTotal) {
		this(orderId, null, null, consumerId, orderTotal);
	}
}
