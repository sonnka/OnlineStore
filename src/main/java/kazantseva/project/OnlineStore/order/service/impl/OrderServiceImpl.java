package kazantseva.project.OnlineStore.order.service.impl;

import kazantseva.project.OnlineStore.customer.repository.CustomerRepository;
import kazantseva.project.OnlineStore.order.model.entity.Order;
import kazantseva.project.OnlineStore.order.model.entity.Status;
import kazantseva.project.OnlineStore.order.model.request.CreateOrder;
import kazantseva.project.OnlineStore.order.model.response.ListOrders;
import kazantseva.project.OnlineStore.order.model.response.OrderDTO;
import kazantseva.project.OnlineStore.order.model.response.ShortOrderDTO;
import kazantseva.project.OnlineStore.order.repository.OrderRepository;
import kazantseva.project.OnlineStore.order.service.OrderService;
import kazantseva.project.OnlineStore.product.model.entity.Product;
import kazantseva.project.OnlineStore.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;
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

    @Override
    public void createOrder(String email, long customerId, CreateOrder order) {
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with ID " + customerId + " not found!"));

        if(!customer.getEmail().equals(email)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC);

        Status status = checkStatus(order.getStatus());

        List<Product> allProducts = productRepository.findAll();

        List<Product> products = allProducts.stream()
                .filter(product -> order.getProducts().contains(product.getName()))
                .toList();

        double price = products.stream().mapToDouble(Product::getPrice).sum();

        if(price != 0) {
            orderRepository.save(Order.builder()
                    .date(date)
                    .status(status)
                    .customer(customer)
                    .products(products)
                    .deliveryAddress(order.getDeliveryAddress())
                    .description(order.getDescription())
                    .price(price)
                    .build()
            );
        }
    }

    private Status checkStatus(String status){
        if(status.equals("UNPAID")){
            return Status.UNPAID;
        } else if(status.equals("PAID")){
            return Status.PAID;
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Status must be UNPAID or PAID");
        }
    }
}
