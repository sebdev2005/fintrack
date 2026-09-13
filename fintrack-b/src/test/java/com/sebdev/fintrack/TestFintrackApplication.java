package com.sebdev.fintrack;

import org.springframework.boot.SpringApplication;

public class TestFintrackApplication {

	public static void main(String[] args) {
		SpringApplication.from(FintrackApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
