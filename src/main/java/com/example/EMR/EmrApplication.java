package com.example.EMR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmrApplication {

	public static void main(String[] args) {

		SpringApplication.run(EmrApplication.class, args);
		System.out.println("Starting EMR Service");
	}

}
