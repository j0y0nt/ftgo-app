package net.chrisrichardson.ftgo.orderservice.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.chrisrichardson.ftgo.orderservice.api.web.FtgoTypes.CreateOrderResponse;
import net.chrisrichardson.ftgo.orderservice.domain.DeliveryInformation;
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRepository;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;


import net.chrisrichardson.ftgo.orderservice.api.web.FtgoTypes.CreateOrderRequest;
import net.chrisrichardson.ftgo.orderservice.api.web.FtgoTypes.CreateOrderResponse;
import net.chrisrichardson.ftgo.orderservice.api.web.FtgoTypes.ReviseOrderRequest;
import net.chrisrichardson.ftgo.orderservice.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static java.util.stream.Collectors.toList;
@RestController
@RequestMapping(path = "/orders")
public class OrderController {

	private OrderService orderService;

	private OrderRepository orderRepository;

	public OrderController(OrderService orderService, OrderRepository orderRepository) {
		this.orderService = orderService;
		this.orderRepository = orderRepository;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	  public CreateOrderResponse create(@RequestBody CreateOrderRequest request) {
	    Order order = orderService.createOrder(request.consumerId(),
	            request.restaurantId(),
	            new DeliveryInformation(request.deliveryTime(), request.deliveryAddress()),
	            request.lineItems().stream().map(x -> new MenuItemIdAndQuantity(x.menuItemId(), x.quantity())).collect(toList())
	    );
	    return new CreateOrderResponse(order.getId());
	  }
	
	  @RequestMapping(path = "/{orderId}", method = RequestMethod.GET)
	  public ResponseEntity<GetOrderResponse> getOrder(@PathVariable long orderId) {
	    Optional<Order> order = orderRepository.findById(orderId);
	    return order.map(o -> new ResponseEntity<GetOrderResponse>(makeGetOrderResponse(o), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	private GetOrderResponse makeGetOrderResponse(Order order) {
		return new GetOrderResponse(order.getId(), order.getState(), order.getOrderTotal());
	}

	@RequestMapping(path = "/{orderId}/cancel", method = RequestMethod.POST)
	public ResponseEntity<GetOrderResponse> cancel(@PathVariable long orderId) {
		try {
			Order order = orderService.cancel(orderId);
			return new ResponseEntity<>(makeGetOrderResponse(order), HttpStatus.OK);
		} catch (OrderNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(path = "/{orderId}/revise", method = RequestMethod.POST)
	public ResponseEntity<GetOrderResponse> revise(@PathVariable long orderId,
			@RequestBody ReviseOrderRequest request) {
		try {
			Order order = orderService.reviseOrder(
				orderId,
				new OrderRevision(Optional.empty(), request.revisedOrderLineItems())
		);
			return new ResponseEntity<>(makeGetOrderResponse(order), HttpStatus.OK);
		} catch (OrderNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
