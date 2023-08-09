package com.gtorresoft.poc.grpc.streaming.ecommerce.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class OrderResource {
    private String id;
    private String status;
    private Integer itemsNumber;
    private Double totalAmount;
}
