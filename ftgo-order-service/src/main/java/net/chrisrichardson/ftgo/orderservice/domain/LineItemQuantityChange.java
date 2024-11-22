package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.common.Money;

public record LineItemQuantityChange(
		Money currentOrderTotal,
		Money newOrderTotal,
		Money delta) {
}
