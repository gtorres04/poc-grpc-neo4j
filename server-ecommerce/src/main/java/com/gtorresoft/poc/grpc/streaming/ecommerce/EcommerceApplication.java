package com.gtorresoft.poc.grpc.streaming.ecommerce;

import org.neo4j.cypherdsl.core.renderer.Configuration;
import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
//import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableNeo4jRepositories
public class EcommerceApplication {
	/*private static final Logger logger = LoggerFactory.getLogger(EcommerceApplication.class.getName());
	private final int port;
	private final Server server;*/

	private final static Logger log = LoggerFactory.getLogger(EcommerceApplication.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(EcommerceApplication.class, args);
		/*EcommerceApplication storeServer = new EcommerceApplication(8980);
		storeServer.start();
		if (storeServer.server != null) {
			storeServer.server.awaitTermination();
		}*/
	}
	@Bean
	public Configuration cypherDslConfiguration() {
		return Configuration.newConfig()
				.withDialect(Dialect.NEO4J_5).build();
	}

	@Bean
	CommandLineRunner demo(PersonRepository personRepository) {
		return args -> {

			personRepository.deleteAll();

			Person greg = new Person("Greg");
			Person roy = new Person("Roy");
			Person craig = new Person("Craig");

			List<Person> team = Arrays.asList(greg, roy, craig);

			log.info("Before linking up with Neo4j...");

			team.stream().forEach(person -> log.info("\t" + person.toString()));

			personRepository.save(greg);
			personRepository.save(roy);
			personRepository.save(craig);

			greg = personRepository.findByName(greg.getName());
			greg.worksWith(roy);
			greg.worksWith(craig);
			personRepository.save(greg);

			roy = personRepository.findByName(roy.getName());
			roy.worksWith(craig);
			// We already know that roy works with greg
			personRepository.save(roy);

			// We already know craig works with roy and greg

			log.info("Lookup each person by name...");
			team.stream().forEach(person -> log.info(
					"\t" + personRepository.findByName(person.getName()).toString()));

			List<Person> teammates = personRepository.findByTeammatesName(greg.getName());
			log.info("The following have Greg as a teammate...");
			teammates.stream().forEach(person -> log.info("\t" + person.getName()));
		};
	}
	/*public EcommerceApplication(int port) throws IOException {
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
	}*/

}
