package net.chrisrichardson.ftgo.restaurantservice.domain;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;

@Transactional
public class RestaurantService {

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private RestaurantDomainEventPublisher restaurantDomainEventPublisher;

	public Restaurant create(CreateRestaurantRequest request) {
		Restaurant restaurant = new Restaurant(request.getName(), request.getMenu());
		restaurantRepository.save(restaurant);
		restaurantDomainEventPublisher.publish(restaurant, Collections
				.singletonList(new RestaurantCreated(request.getName(), request.getAddress(), request.getMenu())));
		return restaurant;
	}

	public Optional<Restaurant> findById(long restaurantId) {
		return restaurantRepository.findById(restaurantId);
	}
}
