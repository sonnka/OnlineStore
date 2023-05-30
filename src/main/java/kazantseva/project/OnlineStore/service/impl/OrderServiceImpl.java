package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.entity.*;
import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.request.RequestProduct;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.ProductDTO;
import kazantseva.project.OnlineStore.model.response.ShortOrderDTO;
import kazantseva.project.OnlineStore.model.response.ShortProductDTO;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.repository.OrderRepository;
import kazantseva.project.OnlineStore.repository.ProductRepository;
import kazantseva.project.OnlineStore.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
    public Page<ShortOrderDTO> getOrders(String email, long customerId, Pageable pageable) {
        checkCustomer(customerId, email);

        return orderRepository.findByCustomerId(customerId, pageable)
                .map(ShortOrderDTO::new);
    }

    @Override
    public OrderDTO getFullOrder(String email, long customerId, long orderId) {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        return toOrderDTO(order);
    }

    @Override
    public void createOrder(String email, long customerId, RequestOrder order) {
        var customer = checkCustomer(customerId, email);

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

        if (!price.equals(BigDecimal.ZERO)) {

            var createdOrder = orderRepository.save(Order.builder()
                    .date(date)
                    .status(status)
                    .customer(customer)
                    .deliveryAddress(order.getDeliveryAddress())
                    .description(order.getDescription())
                    .price(price)
                    .build());

            for (OrderProduct product : products) {
                product.setOrder(createdOrder);
            }

            createdOrder.setProducts(products);

            orderRepository.save(createdOrder);

        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must be at least one product");
    }

    @Override
    public List<ShortProductDTO> getProductList(String email, long customerId, long orderId) {
        checkCustomer(customerId, email);
        var order = checkOrder(orderId, customerId);

        return productRepository.findProductsNotInOrder(order.getId()).stream()
                .filter(id -> productRepository.findById(id).isPresent())
                .map(id -> productRepository.findById(id).get())
                .map(ShortProductDTO::new).toList();
    }

    @Override
    public OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder) {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        return updateOrder(order, newOrder);
    }

    @Override
    public void deleteOrder(String email, long customerId, long orderId) {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        orderRepository.delete(order);
    }

    private OrderDTO updateOrder(Order oldOrder, RequestOrder newOrder) {
        if (Optional.ofNullable(newOrder.getStatus()).isPresent()) {

            Status status = checkStatus(newOrder.getStatus());

            oldOrder.setStatus(status);
        }

        if (Optional.ofNullable(newOrder.getProducts()).isPresent()) {

            List<OrderProduct> products = new ArrayList<>();
            List<RequestProduct> inputProducts = newOrder.getProducts();

            for (RequestProduct current : inputProducts) {
                Product product = productRepository.findByName(current.name());

                if (product != null && current.count() > 0) {
                    products.add(new OrderProduct(oldOrder, product, current.count()));
                }
            }
            if (products.size() > 0) {

                setNewProductList(oldOrder, products);

                oldOrder.setPrice(calculateNewPrice(oldOrder.getProducts()));

            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must be at least one product");
        }

        Optional.ofNullable(newOrder.getDeliveryAddress()).ifPresent(oldOrder::setDeliveryAddress);
        Optional.ofNullable(newOrder.getDescription()).ifPresent(oldOrder::setDescription);

        return toOrderDTO(orderRepository.save(oldOrder));

    }

    private Customer checkCustomer(long customerId, String email) {
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with ID " + customerId + " not found!"));

        if (!customer.getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return customer;
    }

    private Order checkOrder(long orderId, long customerId) {
        var order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order with ID " + orderId + " not found!"));

        if (customerId != order.getCustomer().getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return order;
    }

    private Status checkStatus(String status) {
        if (Status.UNPAID.name().equals(status)) {
            return Status.UNPAID;
        } else if (Status.PAID.name().equals(status)) {
            return Status.PAID;
        } else {
            return Status.UNPAID;
        }
    }

    private void setNewProductList(Order order, List<OrderProduct> products) {
        order.getProducts().clear();

        order.getProducts().addAll(new ArrayList<>());

        orderRepository.save(order);

        order.getProducts().addAll(products);
    }

    private BigDecimal calculateNewPrice(List<OrderProduct> products) {
        return products.stream()
                .map(product -> BigDecimal.valueOf(product.getAmount()).multiply(product.getProduct().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderDTO toOrderDTO(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = order.getDate().format(formatter);

        return new OrderDTO(order.getId(),
                formatDateTime,
                String.valueOf(order.getStatus()),
                order.getProducts().stream().map(ProductDTO::new).toList(),
                order.getDeliveryAddress(),
                order.getDescription(),
                order.getPrice()
        );
    }
}
