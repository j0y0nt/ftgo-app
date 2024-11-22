package net.chrisrichardson.ftgo.orderservice.domain;

import java.util.List;
import java.util.Optional;

import net.chrisrichardson.ftgo.common.RevisedOrderLineItem;

public record OrderRevision(
		Optional<DeliveryInformation> deliveryInformation,
		List<RevisedOrderLineItem> revisedOrderLineItems) {

}
