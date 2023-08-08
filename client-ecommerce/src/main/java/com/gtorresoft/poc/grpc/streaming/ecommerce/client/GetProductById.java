package com.gtorresoft.poc.grpc.streaming.ecommerce.client;

import com.gtorresoft.poc.grpc.streaming.ecommerce.Product;
import com.gtorresoft.poc.grpc.streaming.ecommerce.ProductById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "products")
@RequiredArgsConstructor
public class GetProductById {

    private final StoreClientGrpc storeClientGrpc;

    @GetMapping(value = "/{id}", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<ProductResource> postProducts(@PathVariable String id) {
        ProductById productById = ProductById.newBuilder().setProductId(id).build();
        Product product = storeClientGrpc.unaryStreamingGetProductById(productById);
        return ResponseEntity.ok(ProductResource.builder()
                        .id(product.getProductId())
                        .description(product.getProductDescription())
                        .name(product.getProductName())
                        .price(product.getProductPrice())
                .build());
    }
}

