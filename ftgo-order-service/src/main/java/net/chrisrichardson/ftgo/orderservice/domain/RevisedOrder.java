package net.chrisrichardson.ftgo.orderservice.domain;

public record RevisedOrder(Order order, LineItemQuantityChange change) {

}
