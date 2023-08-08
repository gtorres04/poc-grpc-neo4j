package com.gtorresoft.poc.grpc.streaming.ecommerce.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ProductResource{
    private String name;
    private String description;
    private String id;
    private Double price;
}
