package kazantseva.project.OnlineStore.restcontroller;

import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.ShortOrderDTO;
import kazantseva.project.OnlineStore.model.response.ShortProductDTO;
import kazantseva.project.OnlineStore.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class RestOrderController {

    private OrderService orderService;

    @GetMapping("/customers/{customer-id}/orders")
    public Page<ShortOrderDTO> getOrders(Authentication auth,
                                         @PathVariable("customer-id") long customerId,
                                         Pageable pageable) {
        return orderService.getOrders(auth.getName(), customerId, pageable);
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
                            @RequestBody RequestOrder order) {
        orderService.createOrder(auth.getName(), customerId, order);
    }

    @GetMapping("/customers/{customer-id}/orders/{order-id}/productList")
    public List<ShortProductDTO> getProductList(Authentication auth,
                                                @PathVariable("customer-id") long customerId,
                                                @PathVariable("order-id") long orderId) {
        return orderService.getProductList(auth.getName(), customerId, orderId);
    }

    @PatchMapping("/customers/{customer-id}/orders/{order-id}")
    public OrderDTO updateOrder(Authentication auth,
                                @PathVariable("customer-id") long customerId,
                                @PathVariable("order-id") long orderId,
                                @RequestBody RequestOrder order) {
        return orderService.updateOrder(auth.getName(), customerId, orderId, order);
    }

    @DeleteMapping("/customers/{customer-id}/orders/{order-id}")
    public void deleteOrder(Authentication auth,
                            @PathVariable("customer-id") long customerId,
                            @PathVariable("order-id") long orderId) {
        orderService.deleteOrder(auth.getName(), customerId, orderId);
    }
}
