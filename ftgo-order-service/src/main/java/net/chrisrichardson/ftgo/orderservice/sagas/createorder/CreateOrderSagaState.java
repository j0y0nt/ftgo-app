package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.consumerservice.api.ValidateOrderByConsumer;
import net.chrisrichardson.ftgo.kitchenservice.api.CancelCreateTicket;
import net.chrisrichardson.ftgo.kitchenservice.api.ConfirmCreateTicket;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicket;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply;
import net.chrisrichardson.ftgo.kitchenservice.api.TicketDetails;
import net.chrisrichardson.ftgo.kitchenservice.api.TicketLineItem;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ApproveOrderCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.RejectOrderCommand;

public class CreateOrderSagaState {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private Long orderId;

	private OrderDetails orderDetails;
	private long ticketId;

	private CreateOrderSagaState() {
	}

	public CreateOrderSagaState(Long orderId, OrderDetails orderDetails) {
		this.orderId = orderId;
		this.orderDetails = orderDetails;
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public OrderDetails getOrderDetails() {
		return orderDetails;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
	}

	public long getTicketId() {
		return ticketId;
	}

	CreateTicket makeCreateTicketCommand() {
		return new CreateTicket(getOrderDetails().restaurantId(), getOrderId(), makeTicketDetails(getOrderDetails()));
	}

	public Long getOrderId() {
		return orderId;
	}

	private TicketDetails makeTicketDetails(OrderDetails orderDetails) {
		return new TicketDetails(makeTicketLineItems(orderDetails.lineItems()));
	}

	private List<TicketLineItem> makeTicketLineItems(List<OrderLineItem> lineItems) {
		return lineItems.stream().map(this::makeTicketLineItem).collect(toList());
	}

	private TicketLineItem makeTicketLineItem(OrderLineItem orderLineItem) {
		return new TicketLineItem(orderLineItem.getMenuItemId(), orderLineItem.getName(), orderLineItem.getQuantity());
	}

	void handleCreateTicketReply(CreateTicketReply reply) {
		logger.debug("getTicketId {}", reply.ticketId());
		setTicketId(reply.ticketId());
	}

	CancelCreateTicket makeCancelCreateTicketCommand() {
		return new CancelCreateTicket(getOrderId());
	}

	RejectOrderCommand makeRejectOrderCommand() {
		return new RejectOrderCommand(getOrderId());
	}
	
	ValidateOrderByConsumer makeValidateOrderByConsumerCommand() {
	    ValidateOrderByConsumer x = new ValidateOrderByConsumer();
	    x.setConsumerId(getOrderDetails().consumerId());
	    x.setOrderId(getOrderId());
	    x.setOrderTotal(getOrderDetails().orderTotal().asString());
	    return x;
	  }
	
	AuthorizeCommand makeAuthorizeCommand() {
		AuthorizeCommand ac = new AuthorizeCommand();
		ac.setConsumerId(getOrderDetails().consumerId());
		ac.setOrderId(getOrderId());
		ac.setOrderTotal(getOrderDetails().orderTotal().asString());
	    return ac;
	}

	ApproveOrderCommand makeApproveOrderCommand() {
		return new ApproveOrderCommand(getOrderId());
	}

	ConfirmCreateTicket makeConfirmCreateTicketCommand() {
		return new ConfirmCreateTicket(getTicketId());
	}
}
