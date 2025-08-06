package com.example.Oboe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy

public class OboeApplication {

	public static void main(String[] args) {
		SpringApplication.run(OboeApplication.class, args);
		System.out.println("Application started successfully!");

	}

}
