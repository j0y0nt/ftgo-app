package net.chrisrichardson.ftgo.orderservice.sagas.cancelorder;

public enum CancelOrderSagaState {
	WAITING_TO_AUTHORIZE, COMPLETED, REVERSING
}
