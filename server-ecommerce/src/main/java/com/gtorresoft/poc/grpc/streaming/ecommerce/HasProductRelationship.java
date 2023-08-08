package com.gtorresoft.poc.grpc.streaming.ecommerce;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class HasProductRelationship {
    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private ProductNode product;

    private Long amount;

    public void setProduct(ProductNode product) {
        this.product = product;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductNode getProduct() {
        return product;
    }
}
