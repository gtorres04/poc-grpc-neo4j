package com.gtorresoft.poc.grpc.streaming.ecommerce.client;

import com.gtorresoft.poc.grpc.streaming.ecommerce.Product;
import com.gtorresoft.poc.grpc.streaming.ecommerce.ProductById;
import com.gtorresoft.poc.grpc.streaming.ecommerce.StoreProviderGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

@Service
public class StoreClientGrpc {

    public void unaryStreamingCreateProduct(Product product) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();

        StoreProviderGrpc.StoreProviderBlockingStub stub = StoreProviderGrpc.newBlockingStub(channel);

        Product productCreated = stub.unaryStreamingCreateProduct(product);
        System.out.println("Product created: " + productCreated);
    }

    public Product unaryStreamingGetProductById(ProductById productById) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();

        StoreProviderGrpc.StoreProviderBlockingStub stub = StoreProviderGrpc.newBlockingStub(channel);

        return stub.unaryStreamingGetProductById(productById);
    }
}
