package net.chrisrichardson.ftgo.orderservice.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.orderservice.domain.OrderServiceWithRepositoriesConfiguration;

@Configuration
@Import({ OrderServiceWithRepositoriesConfiguration.class, TramEventSubscriberConfiguration.class })
public class OrderServiceMessagingConfiguration {

	@Bean
	public OrderEventConsumer orderEventConsumer(OrderService orderService) {
		return new OrderEventConsumer(orderService);
	}

	@Bean
	public DomainEventDispatcher domainEventDispatcher(OrderEventConsumer orderEventConsumer,
			DomainEventDispatcherFactory domainEventDispatcherFactory) {
		return domainEventDispatcherFactory.make("orderServiceEvents", orderEventConsumer.domainEventHandlers());
	}
}
