package rs.ac.uns.ftn.informatika.jpa;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

/*
 * Hibernate nudi jedan kes (first-level ili L1 cache) kroz koji svi
 * zahtevi moraju proci. Second-level ili L2 cache je opcion i konfigurabilan (i eksterni za Hibernate).
 * L1 cache omogucava da, unutar sesije, zahtev za objektom iz baze uvek vraca istu instancu objekta
 * i tako sprecava konflikte u podacima i sprecava Hibernate da ucita isti objekat vise puta.
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAsync
public class JpaExampleApplication {

	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(JpaExampleApplication.class, args);
	}

}
