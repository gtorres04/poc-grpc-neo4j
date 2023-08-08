package com.gtorresoft.poc.grpc.streaming.ecommerce.client;

import com.gtorresoft.poc.grpc.streaming.ecommerce.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "products")
@RequiredArgsConstructor
public class PostProducts {

    private final StoreClientGrpc storeClientGrpc;

    @PostMapping(consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> postProducts(@RequestBody ProductResource productResource) {
        storeClientGrpc.unaryStreamingCreateProduct(Product.newBuilder()
                        .setProductId(productResource.getId())
                        .setProductName(productResource.getName())
                        .setProductDescription(productResource.getDescription())
                        .setProductPrice(productResource.getPrice())
                .build());
        return ResponseEntity.noContent().build();
    }
}

