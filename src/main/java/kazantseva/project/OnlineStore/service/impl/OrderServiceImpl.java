package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.model.entity.Order;
import kazantseva.project.OnlineStore.model.entity.Status;
import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.response.ListOrders;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.ShortOrderDTO;
import kazantseva.project.OnlineStore.repository.OrderRepository;
import kazantseva.project.OnlineStore.service.OrderService;
import kazantseva.project.OnlineStore.model.entity.OrderProduct;
import kazantseva.project.OnlineStore.model.entity.Product;
import kazantseva.project.OnlineStore.model.request.RequestProduct;
import kazantseva.project.OnlineStore.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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
        checkCustomer(customerId,email);

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
        checkCustomer(customerId,email);

        var order = checkOrder(orderId, customerId);

        return new OrderDTO(order);
    }

    @Override
    public void createOrder(String email, long customerId, RequestOrder order) {
        var customer = checkCustomer(customerId,email);

        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC);

        Status status = checkStatus(order.getStatus());

        List<OrderProduct> products = new ArrayList<>();
        List<RequestProduct> inputProducts = order.getProducts();

        for (RequestProduct current : inputProducts) {
            Product product = productRepository.findByName(current.name());

            if (product != null && current.count() > 0) {
                products.add(new OrderProduct(new Order(), product, current.count()));
            }
        }

        BigDecimal price = products.stream()
                .map(product -> BigDecimal.valueOf(product.getAmount()).multiply(product.getProduct().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(!price.equals(BigDecimal.ZERO)) {

            var createdOrder = orderRepository.save(Order.builder()
                    .date(date)
                    .status(status)
                    .customer(customer)
                    .deliveryAddress(order.getDeliveryAddress())
                    .description(order.getDescription())
                    .price(price)
                    .build()
            );

            for (OrderProduct product : products) {
                product.setOrder(createdOrder);
            }

            createdOrder.setProducts(products);

            orderRepository.save(createdOrder);

        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Must be at least one product");
    }

    @Override
    public OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder) {
        checkCustomer(customerId,email);

        var order = checkOrder(orderId, customerId);

        return updateOrder(order, newOrder);
    }

    @Override
    public void deleteOrder(String email, long customerId, long orderId) {
        checkCustomer(customerId,email);

        var order = checkOrder(orderId, customerId);

        orderRepository.delete(order);
    }

    private Customer checkCustomer(long customerId, String email){
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with ID " + customerId + " not found!"));

        if(!customer.getEmail().equals(email)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return customer;
    }

    private Order checkOrder(long orderId, long customerId){
        var order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order with ID " + orderId + " not found!"));

        if(customerId != order.getCustomer().getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return order;
    }

    private Status checkStatus(String status) {
        if(Status.UNPAID.name().equals(status)) {
            return Status.UNPAID;
        } else if(Status.PAID.name().equals(status)) {
            return Status.PAID;
        } else {
            return Status.UNPAID;
        }
    }

    private OrderDTO updateOrder(Order oldOrder, RequestOrder newOrder){
        if(Optional.ofNullable(newOrder.getStatus()).isPresent()){

            Status status = checkStatus(newOrder.getStatus());

            oldOrder.setStatus(status);
        }

        if(Optional.ofNullable(newOrder.getProducts()).isPresent()){

            List<OrderProduct> products = new ArrayList<>();
            List<RequestProduct> inputProducts = newOrder.getProducts();

            for (RequestProduct current : inputProducts) {
                Product product = productRepository.findByName(current.name());

                if (product != null && current.count() >= 0) {
                    products.add(new OrderProduct(oldOrder, product, current.count()));
                }
            }

            oldOrder.setProducts(products);
        }

        Optional.ofNullable(newOrder.getDeliveryAddress()).ifPresent(oldOrder::setDeliveryAddress);
        Optional.ofNullable(newOrder.getDescription()).ifPresent(oldOrder::setDescription);

        BigDecimal price = oldOrder.getProducts().stream()
                .map(product -> BigDecimal.valueOf(product.getAmount()).multiply(product.getProduct().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        oldOrder.setPrice(price);

        if(!price.equals(BigDecimal.ZERO)) {
            orderRepository.save(oldOrder);

            return new OrderDTO(oldOrder);

        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Must be at least one product");
    }
}
