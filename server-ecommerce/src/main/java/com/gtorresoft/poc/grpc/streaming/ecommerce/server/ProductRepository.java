package com.gtorresoft.poc.grpc.streaming.ecommerce.server;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends Neo4jRepository<ProductNode, Long> {
    List<ProductNode> findByName(String name);
}
