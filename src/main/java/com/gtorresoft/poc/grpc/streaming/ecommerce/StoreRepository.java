package com.gtorresoft.poc.grpc.streaming.ecommerce;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends Neo4jRepository<Store, Long> {
}
