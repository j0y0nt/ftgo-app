package net.chrisrichardson.eventstore.examples.customersandorders.commonswagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.eventuate.util.spring.swagger.EventuateSwaggerConfig;

@Configuration
public class CommonSwaggerConfiguration {

	@Bean
    public EventuateSwaggerConfig eventuateSwaggerConfig() {
        return () -> "net.chrisrichardson.ftgo";
    }
}
