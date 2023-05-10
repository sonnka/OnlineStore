package kazantseva.project.OnlineStore.order.service.impl;

import kazantseva.project.OnlineStore.customer.repository.CustomerRepository;
import kazantseva.project.OnlineStore.order.model.entity.Order;
import kazantseva.project.OnlineStore.order.model.request.RequestOrder;
import kazantseva.project.OnlineStore.order.model.response.ListOrders;
import kazantseva.project.OnlineStore.order.model.response.OrderDTO;
import kazantseva.project.OnlineStore.order.model.response.ShortOrderDTO;
import kazantseva.project.OnlineStore.order.repository.OrderRepository;
import kazantseva.project.OnlineStore.order.service.OrderService;
import kazantseva.project.OnlineStore.product.model.entity.Product;
import kazantseva.project.OnlineStore.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;
    @Override
    public ListOrders getOrders(String email, long customerId, int page, int size, String sort, String direction) {
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Customer with ID " + customerId + " not found!"));

        if(!customer.getEmail().equals(email)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Pageable pageable = direction.equals("desc") ?
                PageRequest.of(page, size, Sort.Direction.DESC, sort) :
                PageRequest.of(page, size, Sort.Direction.ASC, sort);

        List<Order> orders = orderRepository.findByCustomerId(customerId, pageable).stream().toList();

        return ListOrders.builder()
                .totalAmount(orderRepository.findAllByCustomerId(customerId).size())
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
    public void createOrder(String email, long customerId, RequestOrder order) {
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with ID " + customerId + " not found!"));

        if(!customer.getEmail().equals(email)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC);

        String status = order.getStatus();
        if(!status.equals("UNPAID") && !status.equals("PAID")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be UNPAID or PAID");
        }

        List<Product> allProducts = productRepository.findAll();

        List<Product> products = allProducts.stream()
                .filter(product -> order.getProducts().contains(product.getName()))
                .toList();

        double price = products.stream().mapToDouble(Product::getPrice).sum();

        if(price != 0.0) {
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
        }else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Must be at least one product");
    }

    @Override
    public OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder) {
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

        return updateOrder(order, newOrder);

    }

    @Override
    public void deleteOrder(String email, long customerId, long orderId) {
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

        orderRepository.delete(order);
    }

    private OrderDTO updateOrder(Order oldOrder, RequestOrder newOrder){
        if(Optional.ofNullable(newOrder.getStatus()).isPresent()){
            String status = newOrder.getStatus();
            if(!status.equals("UNPAID") && !status.equals("PAID")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be UNPAID or PAID");
            }
            oldOrder.setStatus(status);
        }

        if(Optional.ofNullable(newOrder.getProducts()).isPresent()){
            List<Product> allProducts = productRepository.findAll();

            List<Product> products = allProducts.stream()
                    .filter(product -> newOrder.getProducts().contains(product.getName()))
                    .toList();

            oldOrder.setProducts(new ArrayList<>(products));
        }

        Optional.ofNullable(newOrder.getDeliveryAddress()).ifPresent(oldOrder::setDeliveryAddress);
        Optional.ofNullable(newOrder.getDescription()).ifPresent(oldOrder::setDescription);

        double price = oldOrder.getProducts().stream().mapToDouble(Product::getPrice).sum();

        oldOrder.setPrice(price);

        if(price != 0.0) {
            orderRepository.save(oldOrder);
            return new OrderDTO(oldOrder);
        }else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must be at least one product");
    }
}
