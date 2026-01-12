package com.manoj.finorder.orderservice.event;

import com.manoj.finorder.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final OrderService orderService;

    @KafkaListener(topics = "${app.kafka.topic.payment-events:payment-events}", groupId = "order-service-group")
    public void onPaymentEvent(PaymentEvent event) {
        if( event == null || event.getEventType() == null) {return;}
        if( "PaymentCaptured".equals(event.getEventType())) {
            orderService.markPaid(event.getOrderId());
        } else if("PaymentFailed".equals(event.getEventType())) {
            orderService.markPaymentFailed(event.getOrderId());
        }
    }
}
