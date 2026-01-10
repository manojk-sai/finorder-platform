package com.manoj.finorder.orderservice.repository;

import com.manoj.finorder.orderservice.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {}