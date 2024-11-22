package net.chrisrichardson.ftgo.orderservice.messaging;

import java.util.List;
import java.util.stream.Collectors;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.messages.Address;
import net.chrisrichardson.ftgo.restaurantservice.events.MenuItem;
import org.jetbrains.annotations.NotNull;

public class RestaurantEventMapper {

	@NotNull
	  public static List<MenuItem> fromMenuItems(List<net.chrisrichardson.ftgo.orderservice.domain.MenuItem> menuItems) {
		return menuItems.stream().map(mi -> { 
			net.chrisrichardson.ftgo.restaurantservice.events.MenuItem nmi = new net.chrisrichardson.ftgo.restaurantservice.events.MenuItem();
			nmi.setId(mi.getId());
			nmi.setName(mi.getName());
			nmi.setPrice(mi.getPrice().asString());
			return nmi;
		})
		.collect(Collectors.toList());
	    //return menuItems.stream().map(mi -> new MenuItem().withId(mi.getId()).withName(mi.getName()).withPrice(mi.getPrice().asString())).collect(Collectors.toList());
	  }

	  public static net.chrisrichardson.ftgo.consumerservice.api.messages.Address fromAddress(net.chrisrichardson.ftgo.common.Address a) {
		net.chrisrichardson.ftgo.consumerservice.api.messages.Address addrs =   new net.chrisrichardson.ftgo.consumerservice.api.messages.Address();
	  	addrs.setStreet1(a.getStreet1());
	  	addrs.setStreet2(a.getStreet2());
	  	addrs.setCity(a.getCity());
	  	addrs.setState(a.getState());
	  	addrs.setZip(a.getZip());
	    return addrs;
	  }

	  public static List<net.chrisrichardson.ftgo.orderservice.domain.MenuItem> toMenuItems(List<net.chrisrichardson.ftgo.restaurantservice.domain.MenuItem> menuItems) {
	    return menuItems.stream().map(mi -> new net.chrisrichardson.ftgo.orderservice.domain.MenuItem(mi.getId(), mi.getName(), mi.getPrice())).collect(Collectors.toList());
	  }
}
