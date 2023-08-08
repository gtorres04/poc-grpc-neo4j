package com.gtorresoft.poc.grpc.streaming.ecommerce;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Node
public class OrderNode {
    @Id
    @GeneratedValue
    private Long id;

    @Relationship(type = "IN_ORDER", direction = Relationship.Direction.OUTGOING)
    private List<HasProductRelationship> hasProductRelationships = new ArrayList<>();

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addProduct(ProductNode product, Long amount) {
        HasProductRelationship hasProductRelationship = new HasProductRelationship();
        hasProductRelationship.setProduct(product);
        hasProductRelationship.setAmount(amount);
        hasProductRelationships.add(hasProductRelationship);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<HasProductRelationship> getHasProductRelationships() {
        return hasProductRelationships;
    }

    public void setHasProductRelationships(List<HasProductRelationship> hasProductRelationships) {
        this.hasProductRelationships = hasProductRelationships;
    }

    public List<ProductNode> getProducts() {
        return this.hasProductRelationships.stream().map(HasProductRelationship::getProduct).collect(Collectors.toList());
    }
}
