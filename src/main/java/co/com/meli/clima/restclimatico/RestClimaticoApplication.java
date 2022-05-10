package co.com.meli.clima.restclimatico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("co.com.meli.clima.restclimatico.infrastructure.restcontroller")
public class RestClimaticoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestClimaticoApplication.class, args);
	}

}
