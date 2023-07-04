package kazantseva.project.OnlineStore.controller.rest;

import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.OrderException;
import kazantseva.project.OnlineStore.exceptions.ProductException;
import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.ShortOrderDTO;
import kazantseva.project.OnlineStore.service.OrderService;
import kazantseva.project.OnlineStore.util.swagger.OrderAPI;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class OrderController implements OrderAPI {

    private OrderService orderService;

    @GetMapping("/customers/{customer-id}/orders")
    public Page<ShortOrderDTO> getOrders(Authentication auth,
                                         @PathVariable("customer-id") long customerId,
                                         Pageable pageable) throws CustomerException {
        return orderService.getOrders(auth.getName(), customerId, pageable);
    }

    @GetMapping("/customers/{customer-id}/orders/{order-id}")
    public OrderDTO getFullOrder(Authentication auth,
                                 @PathVariable("customer-id") long customerId,
                                 @PathVariable("order-id") long orderId)
            throws CustomerException, OrderException {
        return orderService.getFullOrder(auth.getName(), customerId, orderId);
    }

    @PatchMapping("/customers/{customer-id}/orders/{order-id}")
    public OrderDTO updateOrder(Authentication auth,
                                @PathVariable("customer-id") long customerId,
                                @PathVariable("order-id") long orderId,
                                @RequestBody RequestOrder order) throws CustomerException, OrderException {
        return orderService.updateOrder(auth.getName(), customerId, orderId, order);
    }

    @PostMapping("/customers/{customer-id}/orders/{order-id}")
    public OrderDTO publishOrder(Authentication auth,
                                 @PathVariable("customer-id") long customerId,
                                 @PathVariable("order-id") long orderId,
                                 @RequestBody RequestOrder order) throws CustomerException, OrderException {
        return orderService.publishOrder(auth.getName(), customerId, orderId, order);
    }

    @PatchMapping("/basket/{product-id}")
    public void updateBasket(Authentication auth,
                             @PathVariable("product-id") String productId)
            throws CustomerException, OrderException, ProductException {
        orderService.updateBasket(auth.getName(), productId);
    }

    @DeleteMapping("/customers/{customer-id}/orders/{order-id}")
    public void deleteOrder(Authentication auth,
                            @PathVariable("customer-id") long customerId,
                            @PathVariable("order-id") long orderId) throws CustomerException, OrderException {
        orderService.deleteOrder(auth.getName(), customerId, orderId);
    }
}
