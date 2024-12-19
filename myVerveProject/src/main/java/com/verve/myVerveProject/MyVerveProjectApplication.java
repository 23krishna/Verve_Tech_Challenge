package com.verve.myVerveProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyVerveProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyVerveProjectApplication.class, args);
	}

}
