package net.chrisrichardson.ftgo.restaurantservice.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import net.chrisrichardson.ftgo.common.CommonConfiguration;

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@EntityScan
@Import({ TramEventsPublisherConfiguration.class, CommonConfiguration.class })
public class RestaurantServiceDomainConfiguration {

	@Bean
	public RestaurantService restaurantService() {
		return new RestaurantService();
	}

	@Bean
	public RestaurantDomainEventPublisher restaurantDomainEventPublisher(DomainEventPublisher domainEventPublisher) {
		return new RestaurantDomainEventPublisher(domainEventPublisher);
	}
}
