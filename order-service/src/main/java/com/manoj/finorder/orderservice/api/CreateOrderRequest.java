package com.manoj.finorder.orderservice.api;

import com.manoj.finorder.orderservice.model.OrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotBlank
    private String CustomerId;

    @Valid
    @NotEmpty
    private List<OrderItem> orderItems;
}