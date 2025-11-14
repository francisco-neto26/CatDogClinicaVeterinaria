package br.com.catdogclinicavet.backend_api;

import org.springframework.boot.SpringApplication;

public class TestBackendApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(BackendApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
