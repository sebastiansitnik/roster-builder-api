package com.sitnik.warhammer.rosterbuilderapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(title = "Warhammer Roster Builder API done by Sebastian Sitnik", version = "0.1")
)
public class RosterBuilderApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RosterBuilderApiApplication.class, args);
	}

}
