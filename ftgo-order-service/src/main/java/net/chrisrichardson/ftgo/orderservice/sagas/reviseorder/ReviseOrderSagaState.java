package net.chrisrichardson.ftgo.orderservice.sagas.reviseorder;

public enum ReviseOrderSagaState {
	 REQUESTING_RESTAURANT_ORDER_UPDATE,
	 AUTHORIZATION_INCREASED,
	 COMPLETED,
	 REQUESTING_AUTHORIZATION,
	 REVERSING_ORDER_UPDATE,
	 REVERSING_AUTHORIZATION,
	 WAITING_FOR_CHANGE_TO_BE_MADE
}
