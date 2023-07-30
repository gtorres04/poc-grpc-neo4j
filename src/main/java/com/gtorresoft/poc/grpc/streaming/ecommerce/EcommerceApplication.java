package com.gtorresoft.poc.grpc.streaming.ecommerce;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

//@SpringBootApplication
public class EcommerceApplication {
	private static final Logger logger = LoggerFactory.getLogger(EcommerceApplication.class.getName());
	private final int port;
	private final Server server;

	public static void main(String[] args) throws IOException, InterruptedException {
		//SpringApplication.run(EcommerceApplication.class, args);
		EcommerceApplication storeServer = new EcommerceApplication(8980);
		storeServer.start();
		if (storeServer.server != null) {
			storeServer.server.awaitTermination();
		}
	}
	public EcommerceApplication(int port) {
		this.port = port;
		server = ServerBuilder.forPort(port)
				.addService(new StoreService())
				.build();
	}

	public void start() throws IOException {
		server.start();
		logger.info("Server started, listening on " + port);
		Runtime.getRuntime()
				.addShutdownHook(new Thread(() -> {
                    System.err.println("shutting down server");
                    try {
                        EcommerceApplication.this.stop();
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.err);
                    }
                    System.err.println("server shutted down");
                }));
	}

	public void stop() throws InterruptedException {
		if (server != null) {
			server.shutdown()
					.awaitTermination(30, TimeUnit.SECONDS);
		}
	}

}
