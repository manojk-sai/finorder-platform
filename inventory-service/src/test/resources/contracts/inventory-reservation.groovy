package contracts

        import org.springframework.cloud.contract.spec.Contract

        Contract.make {
            description("Reserve inventory for a confirmed order and emit reservation result")
            label("inventory_reserved")
            input {
                triggeredBy("triggerInventoryReserveRequested()")
            }
            outputMessage {
                sentTo("inventory-events")
                body(
                    eventType: "InventoryReserved",
                    orderId: "order-123",
                    orderItems: [
                        [sku: "SKU-1", quantity: 2]
                    ],
                    reason: null,
                    occuredAt: anyIso8601WithOffset()
                )
                headers {
                    header("contentType", applicationJson())
                }
            }
        }