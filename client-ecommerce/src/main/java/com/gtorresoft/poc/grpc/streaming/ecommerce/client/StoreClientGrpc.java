package com.gtorresoft.poc.grpc.streaming.ecommerce.client;

import com.gtorresoft.poc.grpc.streaming.ecommerce.Order;
import com.gtorresoft.poc.grpc.streaming.ecommerce.Product;
import com.gtorresoft.poc.grpc.streaming.ecommerce.ProductById;
import com.gtorresoft.poc.grpc.streaming.ecommerce.ProductToOder;
import com.gtorresoft.poc.grpc.streaming.ecommerce.ProductsByName;
import com.gtorresoft.poc.grpc.streaming.ecommerce.StoreProviderGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public List<Product> serverSideStreamingGetProductsByName(ProductsByName productsByName) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();

        StoreProviderGrpc.StoreProviderBlockingStub stubBlocking = StoreProviderGrpc.newBlockingStub(channel);
        StoreProviderGrpc.StoreProviderStub stub = StoreProviderGrpc.newStub(channel);

        Iterator<Product> productIterator = stubBlocking.serverSideStreamingGetProductsByName(productsByName);
        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(productIterator, 0), false)
                .collect(Collectors.toList());
    }

    public Order clientSideStreamingCreateOrder(String orderId, List<Product> products) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();

        StoreProviderGrpc.StoreProviderBlockingStub stubBlocking = StoreProviderGrpc.newBlockingStub(channel);
        StoreProviderGrpc.StoreProviderStub stub = StoreProviderGrpc.newStub(channel);

        final AtomicReference<Order> orderAtomicReference = new AtomicReference<>();
        StreamObserver<Order> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(Order order) {
                orderAtomicReference.set(order);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
        StreamObserver<Product> requestStreamObserver = stub.clientSideStreamingCreateOrder(responseObserver);
        products.forEach(requestStreamObserver::onNext);
        requestStreamObserver.onCompleted();
        return orderAtomicReference.get();
    }

    public List<Order> clientSideStreamingCreateOrderByProductId(List<ProductToOder> products, String orderId) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();

        StoreProviderGrpc.StoreProviderBlockingStub stubBlocking = StoreProviderGrpc.newBlockingStub(channel);
        StoreProviderGrpc.StoreProviderStub stub = StoreProviderGrpc.newStub(channel);

        List<Order> orders = Collections.synchronizedList(new ArrayList<>());
        StreamObserver<Order> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(Order order) {
                orders.add(order);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
        StreamObserver<ProductToOder> requestStreamObserver = stub.clientSideStreamingCreateOrderByProductId(responseObserver);
        products.forEach(requestStreamObserver::onNext);
        requestStreamObserver.onCompleted();
        return orders;
    }
}
