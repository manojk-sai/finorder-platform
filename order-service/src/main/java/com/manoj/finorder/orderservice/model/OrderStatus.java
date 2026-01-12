package com.manoj.finorder.orderservice.model;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    RESERVED,
    RESERVATION_FAILED,
    COMPLETED,
    PAID,
    CANCELLED,
    PAYMENT_FAILED
}