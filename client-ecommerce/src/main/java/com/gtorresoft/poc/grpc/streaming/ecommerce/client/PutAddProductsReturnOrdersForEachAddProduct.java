package com.gtorresoft.poc.grpc.streaming.ecommerce.client;

import com.gtorresoft.poc.grpc.streaming.ecommerce.Order;
import com.gtorresoft.poc.grpc.streaming.ecommerce.ProductToOder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "orders")
@RequiredArgsConstructor
public class PutAddProductsReturnOrdersForEachAddProduct {

    private final StoreClientGrpc storeClientGrpc;

    @PostMapping(value = "/{id}/products", consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity<List<OrderResource>> postProductsInOrder(@PathVariable("id") String orderId, @RequestBody List<ProductResource> productResources) {
        List<ProductToOder> productToOders = productResources.stream()
                .map(productResource -> ProductToOder.newBuilder()
                        .setProductId(productResource.getId())
                        .build())
                .toList();
        List<Order> orders = storeClientGrpc.clientSideStreamingCreateOrderByProductId(productToOders, orderId);
        return ResponseEntity.ok(orders.stream().map(order -> OrderResource.builder()
                .id(order.getOrderId())
                .status(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .itemsNumber(order.getItemsNumber())
                .build()).toList());
    }
}

