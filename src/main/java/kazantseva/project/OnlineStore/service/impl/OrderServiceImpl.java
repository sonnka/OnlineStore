package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.entity.*;
import kazantseva.project.OnlineStore.model.mongo.entity.Product;
import kazantseva.project.OnlineStore.model.mongo.request.RequestProduct;
import kazantseva.project.OnlineStore.model.mongo.response.ProductDTO;
import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.ShortOrderDTO;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.repository.OrderRepository;
import kazantseva.project.OnlineStore.repository.mongo.ProductRepository;
import kazantseva.project.OnlineStore.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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

        return orderRepository.findByCustomerIdAndType(customerId, Type.PUBLISHED, pageable)
                .map(ShortOrderDTO::new);
    }

    @Override
    public OrderDTO getFullOrder(String email, long customerId, long orderId) {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        return toOrderDTO(order);
    }

    @Override
    public Order getOrder(String email, long customerId, long orderId) {
        checkCustomer(customerId, email);

        return checkOrder(orderId, customerId);
    }

    @Override
    public OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder) {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        if (Status.PAID == order.getStatus()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can not change the paid order.");
        }

        var updatedOrder = updateOrder(order, newOrder);

        if (updatedOrder.getPrice().compareTo(BigDecimal.valueOf(1.0)) < 0 &&
                Type.PUBLISHED.equals(updatedOrder.getType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please note that the minimum " +
                    "order amount is $1.");
        }

        return toOrderDTO(orderRepository.save(updatedOrder));
    }

    @Override
    public OrderDTO publishOrder(String email, long customerId, long orderId, RequestOrder newOrder) {
        var customer = checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        if (newOrder.getProducts().size() > 0) {
            var updatedOrder = updateOrder(order, newOrder);

            if (updatedOrder.getPrice().compareTo(BigDecimal.valueOf(1.0)) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please note that the minimum " +
                        "order amount is $1.");
            }

            updatedOrder.setType(Type.PUBLISHED);
            updatedOrder.setDate(LocalDateTime.now(ZoneOffset.UTC));

            customer.setBasket(orderRepository.save(Order.builder()
                            .customer(customer)
                            .type(Type.DRAFT)
                            .status(Status.UNPAID)
                            .build())
                    .getId());

            return toOrderDTO(orderRepository.save(updatedOrder));
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can not create order without products!");
    }

    @Override
    public void deleteOrder(String email, long customerId, long orderId) {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);
        if (Status.UNPAID == order.getStatus()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can not delete the unpaid order!");
        }
        orderRepository.delete(order);
    }

    @Override
    public void updateBasket(String email, String productId) {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with email " + email + " not found!"));

        var order = orderRepository.findById(customer.getBasket()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order with id " + customer.getBasket() + " not found!"));

        List<OrderProduct> oldList = new ArrayList<>(order.getProducts());

        var product = productRepository.findById(productId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product with id " + productId + " not found!"));

        if (oldList.stream().map(OrderProduct::getProductId).toList().contains(productId)) {
            for (OrderProduct orderProduct : oldList) {
                if (orderProduct.getProductId().equals(productId)) {
                    int amount = orderProduct.getAmount() + 1;
                    orderProduct.setAmount(amount);
                    break;
                }
            }
        } else {
            oldList.add(new OrderProduct(order, product.getId(), 1));
        }

        setNewProductList(order, oldList);

        order.setPrice(calculateNewPrice(order.getProducts()));

        orderRepository.save(order);
    }

    private Order updateOrder(Order oldOrder, RequestOrder newOrder) {
        if (Optional.ofNullable(newOrder.getStatus()).isPresent()) {

            Status status = checkStatus(newOrder.getStatus());

            oldOrder.setStatus(status);
        }

        if (Optional.ofNullable(newOrder.getProducts()).isPresent()) {

            List<OrderProduct> products = new ArrayList<>();
            List<RequestProduct> inputProducts = newOrder.getProducts();

            for (RequestProduct current : inputProducts) {
                Product product = productRepository.findById(current.id()).orElse(null);

                if (product != null && current.count() > 0) {
                    products.add(new OrderProduct(oldOrder, product.getId(), current.count()));
                }
            }

            setNewProductList(oldOrder, products);

            oldOrder.setPrice(calculateNewPrice(oldOrder.getProducts()));
        }

        Optional.ofNullable(newOrder.getDeliveryAddress()).ifPresent(oldOrder::setDeliveryAddress);
        Optional.ofNullable(newOrder.getDescription()).ifPresent(oldOrder::setDescription);

        return oldOrder;

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

        order.getProducts().addAll(products);
    }

    private BigDecimal calculateNewPrice(List<OrderProduct> products) {
        return products.stream()
                .map(product -> BigDecimal.valueOf(product.getAmount()).multiply(
                        productRepository.findById(product.getProductId()).get().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderDTO toOrderDTO(Order order) {
        String formatDateTime = null;
        var price = "0.00";
        if (order.getDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            formatDateTime = order.getDate().format(formatter);
        }
        if (order.getPrice() != null) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            price = df.format(order.getPrice());
        }

        return new OrderDTO(order.getId(),
                formatDateTime,
                String.valueOf(order.getStatus()),
                String.valueOf(order.getType()),
                toProductList(order.getProducts()),
                order.getDeliveryAddress(),
                order.getDescription(),
                price
        );
    }

    public List<ProductDTO> toProductList(List<OrderProduct> list) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        List<ProductDTO> products = new ArrayList<>();
        for (OrderProduct orderProduct : list) {
            var product = productRepository.findById(orderProduct.getProductId());
            if (product.isPresent()) {
                var price = df.format(product.get().getPrice());
                products.add(ProductDTO.builder()
                        .id(product.get().getId())
                        .name(product.get().getName())
                        .price(price)
                        .count(orderProduct.getAmount())
                        .build());
            }
        }
        return products;
    }
}
