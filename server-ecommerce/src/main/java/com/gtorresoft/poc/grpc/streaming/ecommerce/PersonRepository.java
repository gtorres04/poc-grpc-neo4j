package com.gtorresoft.poc.grpc.streaming.ecommerce;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, Long> {
    Person findByName(String name);
    List<Person> findByTeammatesName(String name);
}
