package com.gtorresoft.poc.grpc.streaming.ecommerce.server;

import com.google.type.Date;
import com.gtorresoft.poc.grpc.streaming.ecommerce.Order;
import com.gtorresoft.poc.grpc.streaming.ecommerce.Product;
import com.gtorresoft.poc.grpc.streaming.ecommerce.ProductById;
import com.gtorresoft.poc.grpc.streaming.ecommerce.ProductToOder;
import com.gtorresoft.poc.grpc.streaming.ecommerce.ProductsByName;
import com.gtorresoft.poc.grpc.streaming.ecommerce.StoreProviderGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Objects;

@GrpcService
public class StoreService extends StoreProviderGrpc.StoreProviderImplBase {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Override
    public void unaryStreamingCreateProduct(Product request, StreamObserver<Product> responseObserver) {
        ProductNode productNode = new ProductNode();
        productNode.setName(request.getProductName());
        productNode.setDescription(request.getProductDescription());
        productNode.setPrice(request.getProductPrice());
        productRepository.save(productNode);
        responseObserver.onNext(request.toBuilder().setProductId(productNode.getId().toString()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void unaryStreamingGetProductById(ProductById request, StreamObserver<Product> responseObserver) {
        productRepository.findById(Long.parseLong(request.getProductId()))
                .ifPresentOrElse(
                        productNode -> {
                            Product response = Product.newBuilder()
                                    .setProductId(productNode.getId().toString())
                                    .setProductName(productNode.getName())
                                    .setProductDescription(productNode.getDescription())
                                    .setProductPrice(productNode.getPrice())
                                    .build();
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(new RuntimeException("Product not found"))
                );

    }

    @Override
    public void serverSideStreamingGetProductsByName(ProductsByName request, StreamObserver<Product> responseObserver) {
        productRepository.findByName(request.getProductName())
                .forEach(productNode -> {
                    Product response = Product.newBuilder()
                            .setProductId(productNode.getId().toString())
                            .setProductName(productNode.getName())
                            .setProductDescription(productNode.getDescription())
                            .setProductPrice(productNode.getPrice())
                            .build();
                    responseObserver.onNext(response);
                });
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ProductToOder> clientSideStreamingCreateOrderByProductId(StreamObserver<Order> responseObserver) {
        return new StreamObserver<>() {
            OrderNode orderNode;

            @Override
            public void onNext(ProductToOder productToOder) {
                 if (Objects.isNull(this.orderNode)) {
                    OrderNode orderNodeToCreate = new OrderNode();
                    orderNodeToCreate.setStatus("CREATED");
                    this.orderNode = orderRepository.save(orderNodeToCreate);
                }
                orderRepository.findById(this.orderNode.getId())
                        .ifPresentOrElse(orderNode -> {
                            productRepository.findById(Long.parseLong(productToOder.getProductId())).ifPresentOrElse(
                                    productNode -> {
                                        this.orderNode.setStatus("ADDING_PRODUCTS");
                                        this.orderNode.addProduct(productNode, (long) productToOder.getAmount());
                                        this.orderNode = orderRepository.save(this.orderNode);
                                        Order order = Order.newBuilder()
                                                .setOrderId(this.orderNode.getId().toString())
                                                .setOrderStatus(this.orderNode.getStatus())
                                                .setItemsNumber(this.orderNode.getProducts().size())
                                                .setTotalAmount(this.orderNode.getProducts().stream().mapToDouble(ProductNode::getPrice).sum())
                                                .build();
                                        responseObserver.onNext(order);
                                    }, () -> {
                                        throw new RuntimeException("Product not found");
                                    });
                        }, () -> {
                            throw new RuntimeException("Order not found");
                        });

            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                Order order = Order.newBuilder()
                        .setOrderId(this.orderNode.getId().toString())
                        .setOrderStatus(this.orderNode.getStatus())
                        .setOrderDate(Date.newBuilder()
                                .setDay(LocalDate.now().getDayOfMonth())
                                .setMonth(LocalDate.now().getMonthValue())
                                .setYear(LocalDate.now().getYear())
                                .build())
                        .setItemsNumber(this.orderNode.getProducts().size())
                        .setTotalAmount(this.orderNode.getProducts().stream().mapToDouble(ProductNode::getPrice).sum())
                        .build();
                responseObserver.onNext(order);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Product> clientSideStreamingCreateOrder(final StreamObserver<Order> responseObserver) {
        return new StreamObserver<>() {
            OrderNode orderNode;

            @Override
            public void onNext(Product productToOder) {
                if (Objects.isNull(this.orderNode)) {
                    OrderNode orderNodeToCreate = new OrderNode();
                    orderNodeToCreate.setStatus("CREATED");
                    this.orderNode = orderRepository.save(orderNodeToCreate);
                }
                orderRepository.findById(this.orderNode.getId())
                        .ifPresentOrElse(orderNode -> {
                            productRepository.findById(Long.parseLong(productToOder.getProductId())).ifPresentOrElse(
                                    productNode -> {
                                        this.orderNode.setStatus("ADDING_PRODUCTS");
                                        this.orderNode.addProduct(productNode, (long) 0);
                                        this.orderNode = orderRepository.save(this.orderNode);
                                    }, () -> {
                                        throw new RuntimeException("Product not found");
                                    });
                        }, () -> {
                            throw new RuntimeException("Order not found");
                        });

            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                Order order = Order.newBuilder()
                        .setOrderId(this.orderNode.getId().toString())
                        .setOrderStatus(this.orderNode.getStatus())
                        .setOrderDate(Date.newBuilder()
                                .setDay(LocalDate.now().getDayOfMonth())
                                .setMonth(LocalDate.now().getMonthValue())
                                .setYear(LocalDate.now().getYear())
                                .build())
                        .setItemsNumber(this.orderNode.getProducts().size())
                        .setTotalAmount(this.orderNode.getProducts().stream().mapToDouble(ProductNode::getPrice).sum())
                        .build();
                responseObserver.onNext(order);
                responseObserver.onCompleted();
            }
        };
    }

}