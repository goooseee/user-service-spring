package com.example.user_service_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class UserServiceSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceSpringApplication.class, args);
	}

}
