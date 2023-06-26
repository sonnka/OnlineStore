package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.entity.Order;
import kazantseva.project.OnlineStore.model.entity.OrderProduct;
import kazantseva.project.OnlineStore.model.entity.Status;
import kazantseva.project.OnlineStore.model.mongo.Product;
import kazantseva.project.OnlineStore.model.mongo.ProductDTO;
import kazantseva.project.OnlineStore.model.mongo.RequestProduct;
import kazantseva.project.OnlineStore.model.mongo.ShortProductDTO;
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
    public OrderDTO createOrder(String email, long customerId, RequestOrder order) {
        var customer = checkCustomer(customerId, email);

        LocalDateTime date = LocalDateTime.now(ZoneOffset.UTC);

        Status status = checkStatus(order.getStatus());

        List<OrderProduct> products = new ArrayList<>();
        List<RequestProduct> inputProducts = order.getProducts();

        for (RequestProduct current : inputProducts) {
            Product product = productRepository.findByName(current.name());

            if (product != null && current.count() > 0) {
                products.add(new OrderProduct(new Order(), product.getId(), current.count()));
            }
        }

        BigDecimal price = products.stream()
                .map(product -> BigDecimal.valueOf(product.getAmount()).multiply(
                        productRepository.findById(product.getProductId()).get().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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

        return toOrderDTO(orderRepository.save(createdOrder));
    }

    @Override
    public List<ShortProductDTO> getProductList(String email, long customerId, long orderId) {
        checkCustomer(customerId, email);
        var order = checkOrder(orderId, customerId);

        var productsId = order.getProducts().stream().map(OrderProduct::getProductId).toList();

        return productRepository.findByIdNotIn(productsId).stream()
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
                    products.add(new OrderProduct(oldOrder, product.getId(), current.count()));
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
                .map(product -> BigDecimal.valueOf(product.getAmount()).multiply(
                        productRepository.findById(product.getProductId()).get().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderDTO toOrderDTO(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        DecimalFormat df = new DecimalFormat("#,###.00");
        String formatDateTime = order.getDate().format(formatter);
        var price = df.format(order.getPrice());

        return new OrderDTO(order.getId(),
                formatDateTime,
                String.valueOf(order.getStatus()),
                toProductList(order.getProducts()),
                order.getDeliveryAddress(),
                order.getDescription(),
                price
        );
    }

    public List<ProductDTO> toProductList(List<OrderProduct> list) {
        DecimalFormat df = new DecimalFormat("#,###.00");
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
