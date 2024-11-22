package net.chrisrichardson.ftgo.restaurantservice.domain;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

@Embeddable
@Access(AccessType.FIELD)
public class RestaurantMenu {

	@ElementCollection
	private List<MenuItem> menuItems;

	private RestaurantMenu() {
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public RestaurantMenu(List<MenuItem> menuItems) {

		this.menuItems = menuItems;
	}
}
