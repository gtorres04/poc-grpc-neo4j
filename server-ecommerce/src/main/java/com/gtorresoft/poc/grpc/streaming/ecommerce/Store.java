package com.gtorresoft.poc.grpc.streaming.ecommerce;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class Store {
    @Id
    @GeneratedValue
    private Long id;
}
