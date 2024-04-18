package com.example.EMR;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmrApplication {

	private static final Logger logger = LogManager.getRootLogger();

	public static void main(String[] args) {

		SpringApplication.run(EmrApplication.class, args);
		System.out.println("Starting EMR Service");
	}


}
