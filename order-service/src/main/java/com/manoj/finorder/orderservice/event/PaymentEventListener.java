package com.manoj.finorder.orderservice.event;

import com.manoj.finorder.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {
    private final OrderService orderService;

    @KafkaListener(
            topics = "${app.kafka.topic.payment-events:payment-events}",
            groupId = "order-service-group",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void onPaymentEvent(PaymentEvent event) {
        if( event == null || event.getEventType() == null) {return;}
        if( "PaymentCaptured".equals(event.getEventType())) {
            log.info("payment_event.captured orderId: {} paymentIntentId: {}", event.getOrderId(), event.getPaymentIntentId());
            orderService.markPaid(event.getOrderId());
        } else if("PaymentFailed".equals(event.getEventType())) {
            log.info("payment_event.failed orderId: {} paymentIntentId: {}", event.getOrderId(), event.getPaymentIntentId());
            orderService.markPaymentFailed(event.getOrderId());
        } else if("PaymentRefunded".equals(event.getEventType())) {
            log.info("payment_event.refunded orderId: {} paymentIntentId: {}", event.getOrderId(), event.getPaymentIntentId());
        }
    }
}
