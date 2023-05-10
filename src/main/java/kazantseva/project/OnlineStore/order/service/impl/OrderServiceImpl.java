package kazantseva.project.OnlineStore.order.service.impl;

import kazantseva.project.OnlineStore.customer.repository.CustomerRepository;
import kazantseva.project.OnlineStore.order.model.entity.Order;
import kazantseva.project.OnlineStore.order.model.response.ListOrders;
import kazantseva.project.OnlineStore.order.model.response.OrderDTO;
import kazantseva.project.OnlineStore.order.model.response.ShortOrderDTO;
import kazantseva.project.OnlineStore.order.repository.OrderRepository;
import kazantseva.project.OnlineStore.order.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    @Override
    public ListOrders getOrders(String email, long customerId) {
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Customer with ID " + customerId + " not found!"));

        if(!customer.getEmail().equals(email)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        List<Order> orders = orderRepository.findByCustomerId(customerId);

        return ListOrders.builder()
                .amount(orders.size())
                .orders(orders.stream()
                        .map(ShortOrderDTO::new)
                        .toList()
                )
                .build();
    }

    @Override
    public OrderDTO getFullOrder(String email, long customerId, long orderId) {
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with ID " + customerId + " not found!"));

        if(!customer.getEmail().equals(email)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        var order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order with ID " + orderId + " not found!"));

        if(customerId != order.getCustomer().getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return new OrderDTO(order);
    }
}
