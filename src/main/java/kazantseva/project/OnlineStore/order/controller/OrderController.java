package kazantseva.project.OnlineStore.order.controller;

import kazantseva.project.OnlineStore.order.model.request.CreateOrder;
import kazantseva.project.OnlineStore.order.model.response.ListOrders;
import kazantseva.project.OnlineStore.order.model.response.OrderDTO;
import kazantseva.project.OnlineStore.order.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @GetMapping("/customers/{customer-id}/orders")
    public ListOrders getOrders(Authentication auth,
                                @PathVariable("customer-id") long customerId) {
        return orderService.getOrders(auth.getName(), customerId);
    }

    @GetMapping("/customers/{customer-id}/orders/{order-id}")
    public OrderDTO getFullOrder(Authentication auth,
                                 @PathVariable("customer-id") long customerId,
                                 @PathVariable("order-id") long orderId) {
        return orderService.getFullOrder(auth.getName(), customerId, orderId);
    }

    @PostMapping("/customers/{customer-id}/orders")
    public void createOrder(Authentication auth,
                                   @PathVariable("customer-id") long customerId,
                                   @RequestBody CreateOrder order) {
        orderService.createOrder(auth.getName(), customerId, order);
    }

}
