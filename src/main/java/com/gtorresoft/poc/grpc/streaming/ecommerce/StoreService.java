package com.gtorresoft.poc.grpc.streaming.ecommerce;

import com.google.type.Date;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Random;

@GrpcService
public class StoreService extends StoreProviderGrpc.StoreProviderImplBase {

    @Autowired
    ProductRepository productRepository;

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
    public StreamObserver<Product> clientSideStreamingCreateOrder(final StreamObserver<Order> responseObserver) {
        return new StreamObserver<>() {

            int count;
            double price = 0.0;

            @Override
            public void onNext(Product product) {
                count++;
                price = price + product.getProductPrice();

            }

            @Override
            public void onCompleted() {

                LocalDate currentDate = LocalDate.now();
                Date orderDate = Date.newBuilder()
                        .setDay(currentDate.getDayOfMonth())
                        .setMonth(currentDate.getMonthValue())
                        .setYear(currentDate.getYear())
                        .build();

                Order order = Order.newBuilder()
                        .setOrderId(RandomStringUtils.randomAlphanumeric(10))
                        .setOrderStatus("Pending")
                        .setOrderDate(orderDate)
                        .setItemsNumber(count)
                        .setTotalAmount(price)
                        .build();

                responseObserver.onNext(order);
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                //logger.warn("error:{}", t.getMessage());

            }

        };
    }

    @Override
    public StreamObserver<Stock> bidirectionalStreamingUpdateStock(final StreamObserver<StockByProduct> responseObserver) {
        return new StreamObserver<>() {

            @Override
            public void onNext(Stock stock) {
                Random random = new Random();
                StockByProduct stockByProduct = StockByProduct.newBuilder()
                        .setProductId(stock.getProductId())
                        .setProductName(RandomStringUtils.randomAlphanumeric(10))
                        .setProductDescription(RandomStringUtils.randomAlphanumeric(10))
                        .setProductPrice(random.nextDouble())
                        .setItemsNumber(stock.getItemsNumber() + 100)
                        .build();
                responseObserver.onNext(stockByProduct);

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                //logger.warn("error:{}", t.getMessage());
            }

        };
    }

}