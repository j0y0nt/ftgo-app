package net.chrisrichardson.ftgo.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import io.eventuate.common.json.mapper.JSonMapper;

public class MoneySerializationTest {
	
	@BeforeAll
	public static void initialize() {
		CommonJsonMapperInitializer.registerMoneyModule();
	}

	public static class MoneyContainer {
		private Money price;

		@Override
		public boolean equals(Object o) {
			return EqualsBuilder.reflectionEquals(this, o);
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

		public Money getPrice() {
			return price;
		}

		public void setPrice(Money price) {
			this.price = price;
		}

		public MoneyContainer() {

		}

		public MoneyContainer(Money price) {

			this.price = price;
		}
	}

	@Test
	public void shouldSer() {
		Money price = new Money("12.34");
		MoneyContainer mc = new MoneyContainer(price);
		assertEquals("{\"price\":\"12.34\"}", JSonMapper.toJson(mc));
	}

	@Test
	public void shouldDe() {
		Money price = new Money("12.34");
		MoneyContainer mc = new MoneyContainer(price);
		assertEquals(mc, JSonMapper.fromJson("{\"price\":\"12.34\"}", MoneyContainer.class));
	}

	@Test
	public void shouldFailToDe() {
		try {
			JSonMapper.fromJson("{\"price\": { \"amount\" : \"12.34\"} }", MoneyContainer.class);
			fail("expected exception");
		} catch (RuntimeException e) {
			assertEquals(JsonMappingException.class, e.getCause().getClass());
		}
	}
}
