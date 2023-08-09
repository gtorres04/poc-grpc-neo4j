package com.gtorresoft.poc.grpc.streaming.ecommerce.client;

import com.gtorresoft.poc.grpc.streaming.ecommerce.ProductsByName;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "products")
@RequiredArgsConstructor
public class GetProductByName {

    private final StoreClientGrpc storeClientGrpc;

    @GetMapping(consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ProductResource>> getProductsByName(@RequestParam String name) {
        ProductsByName productsByName = ProductsByName.newBuilder().setProductName(name).build();
        return ResponseEntity.ok(storeClientGrpc.serverSideStreamingGetProductsByName(productsByName).stream().map(product -> ProductResource.builder()
                .id(product.getProductId())
                .description(product.getProductDescription())
                .name(product.getProductName())
                .price(product.getProductPrice())
                .build()).collect(Collectors.toList()));
    }
}

