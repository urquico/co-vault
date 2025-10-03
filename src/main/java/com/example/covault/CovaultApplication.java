package com.example.covault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CovaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(CovaultApplication.class, args);
	}

}
