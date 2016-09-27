package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Sparj class is a Spring boot class. 
 * 
 *
 * @author Prag Kamra
 * @author vinay Yadav
 * @author Seema Makkar
 * @author vivek 
 * @author Rishabh Jain
 *
 */

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages={"com.example"})
public class Sparj {

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Sparj.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Sparj.class, args);
	}
}
