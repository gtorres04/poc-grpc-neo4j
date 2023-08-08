package com.gtorresoft.poc.grpc.streaming.ecommerce.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import java.io.IOException;

@SpringBootApplication
@EnableNeo4jRepositories
public class EcommerceApplication {

	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(EcommerceApplication.class, args);
	}

}
