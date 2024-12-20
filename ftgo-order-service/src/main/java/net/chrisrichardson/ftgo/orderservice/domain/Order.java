package net.chrisrichardson.ftgo.orderservice.domain;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static net.chrisrichardson.ftgo.orderservice.api.events.OrderState.APPROVAL_PENDING;

import java.util.List;
import java.util.Optional;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.UnsupportedStateTransitionException;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderAuthorized;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderCancelled;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderCreatedEvent;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDetails;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDomainEvent;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderRejected;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderState;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
public class Order {

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Long version;

	@Enumerated(EnumType.STRING)
	private OrderState state;

	private Long consumerId;
	private Long restaurantId;

	@Embedded
	private OrderLineItems orderLineItems;

	@Embedded
	private DeliveryInformation deliveryInformation;

	@Embedded
	private PaymentInformation paymentInformation;

	@Embedded
	private Money orderMinimum = new Money(Integer.MAX_VALUE);

	private Order() {
	}

	public Order(long consumerId, long restaurantId, DeliveryInformation deliveryInformation,
			List<OrderLineItem> orderLineItems) {
		this.consumerId = consumerId;
		this.restaurantId = restaurantId;
		this.deliveryInformation = deliveryInformation;
		this.orderLineItems = new OrderLineItems(orderLineItems);
		this.state = APPROVAL_PENDING;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DeliveryInformation getDeliveryInformation() {
		return deliveryInformation;
	}

	public Money getOrderTotal() {
		return orderLineItems.orderTotal();
	}

	public List<OrderDomainEvent> cancel() {
		switch (state) {
		case APPROVED:
			this.state = OrderState.CANCEL_PENDING;
			return emptyList();
		default:
			throw new UnsupportedStateTransitionException(state);
		}
	}

	public List<OrderDomainEvent> undoPendingCancel() {
		switch (state) {
		case CANCEL_PENDING:
			this.state = OrderState.APPROVED;
			return emptyList();
		default:
			throw new UnsupportedStateTransitionException(state);
		}
	}

	public List<OrderDomainEvent> noteCancelled() {
		switch (state) {
		case CANCEL_PENDING:
			this.state = OrderState.CANCELLED;
			return singletonList(new OrderCancelled());
		default:
			throw new UnsupportedStateTransitionException(state);
		}
	}

	public List<OrderDomainEvent> noteApproved() {
		switch (state) {
		case APPROVAL_PENDING:
			this.state = OrderState.APPROVED;
			return singletonList(new OrderAuthorized());
		default:
			throw new UnsupportedStateTransitionException(state);
		}
	}

	public List<OrderDomainEvent> noteRejected() {
		switch (state) {
		case APPROVAL_PENDING:
			this.state = OrderState.REJECTED;
			return singletonList(new OrderRejected());

		default:
			throw new UnsupportedStateTransitionException(state);
		}
	}

	public Optional<List<OrderDomainEvent>> noteReversingAuthorization() {
		return Optional.of(null);
	}

	public ResultWithDomainEvents<LineItemQuantityChange, OrderDomainEvent> revise(OrderRevision orderRevision) {
		switch (state) {

		case APPROVED:
			LineItemQuantityChange change = orderLineItems.lineItemQuantityChange(orderRevision);
			if (change.newOrderTotal().isGreaterThanOrEqual(orderMinimum)) {
				throw new OrderMinimumNotMetException();
			}
			this.state = OrderState.REVISION_PENDING;
			return new ResultWithDomainEvents<>(change, singletonList(
					new OrderRevisionProposed(orderRevision, change.currentOrderTotal(), change.newOrderTotal())));

		default:
			throw new UnsupportedStateTransitionException(state);
		}
	}

	public List<OrderDomainEvent> rejectRevision() {
		switch (state) {
		case REVISION_PENDING:
			this.state = OrderState.APPROVED;
			return emptyList();
		default:
			throw new UnsupportedStateTransitionException(state);
		}
	}

	public List<OrderDomainEvent> confirmRevision(OrderRevision orderRevision) {
		switch (state) {
		case REVISION_PENDING:
			LineItemQuantityChange licd = orderLineItems.lineItemQuantityChange(orderRevision);

			orderRevision.deliveryInformation().ifPresent(newDi -> this.deliveryInformation = newDi);

			if (orderRevision.revisedOrderLineItems() != null && orderRevision.revisedOrderLineItems().size() > 0) {
				orderLineItems.updateLineItems(orderRevision);
			}

			this.state = OrderState.APPROVED;
			return singletonList(new OrderRevised(orderRevision, licd.currentOrderTotal(), licd.newOrderTotal()));
		default:
			throw new UnsupportedStateTransitionException(state);
		}
	}

	public Long getVersion() {
		return version;
	}

	public List<OrderLineItem> getLineItems() {
		return orderLineItems.getLineItems();
	}

	public OrderState getState() {
		return state;
	}

	public long getRestaurantId() {
		return restaurantId;
	}

	public Long getConsumerId() {
		return consumerId;
	}
	
	public static ResultWithDomainEvents<Order, OrderDomainEvent>
	  createOrder(long consumerId, Restaurant restaurant, DeliveryInformation deliveryInformation, List<OrderLineItem> orderLineItems) {
	    Order order = new Order(consumerId, restaurant.getId(), deliveryInformation, orderLineItems);
	    List<OrderDomainEvent> events = singletonList(new OrderCreatedEvent(
	            new OrderDetails(consumerId, restaurant.getId(), orderLineItems,
	                    order.getOrderTotal()),
	            deliveryInformation.getDeliveryAddress(),
	            restaurant.getName()));
	    return new ResultWithDomainEvents<>(order, events);
	  }

}
