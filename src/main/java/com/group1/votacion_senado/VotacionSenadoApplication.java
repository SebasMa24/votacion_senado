package com.group1.votacion_senado;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class VotacionSenadoApplication {
	@PostConstruct
    public void iniciarZonaHoraria() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
        System.out.println("Zona horaria configurada para: " + TimeZone.getDefault().getID());
    }
	public static void main(String[] args) {
		SpringApplication.run(VotacionSenadoApplication.class, args);
	}

}
