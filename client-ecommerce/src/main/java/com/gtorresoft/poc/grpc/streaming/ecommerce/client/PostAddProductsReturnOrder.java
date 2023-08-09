package com.gtorresoft.poc.grpc.streaming.ecommerce.client;

import com.gtorresoft.poc.grpc.streaming.ecommerce.Order;
import com.gtorresoft.poc.grpc.streaming.ecommerce.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "orders")
@RequiredArgsConstructor
public class PostAddProductsReturnOrder {

    private final StoreClientGrpc storeClientGrpc;

    @PutMapping(value = "/{id}/products", consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity<OrderResource> putProductsInOrder(@PathVariable("id") String orderId, @RequestBody List<ProductResource> productResources) {
        List<Product> products = productResources.stream()
                .map(productResource -> Product.newBuilder()
                        .setProductId(productResource.getId())
                        .setProductName(productResource.getName())
                        .setProductDescription(productResource.getDescription())
                        .setProductPrice(productResource.getPrice())
                        .build())
                .toList();
        Order order = storeClientGrpc.clientSideStreamingCreateOrder(orderId, products);
        return ResponseEntity.ok(OrderResource.builder()
                .id(order.getOrderId())
                .status(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .itemsNumber(order.getItemsNumber())
                .build());
    }
}

