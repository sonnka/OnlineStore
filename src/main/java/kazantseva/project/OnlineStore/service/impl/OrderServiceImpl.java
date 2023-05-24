package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.entity.*;
import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.request.RequestProduct;
import kazantseva.project.OnlineStore.model.response.*;
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
    public ListOrders getOrders(String email, long customerId, Pageable pageable) {
        checkCustomer(customerId, email);

        List<Order> orders = orderRepository.findByCustomerId(customerId, pageable).stream().toList();

        return ListOrders.builder()
                .totalAmount(orderRepository.findAllByCustomerId(customerId).size())
                .amount(orders.size())
                .orders(orders.stream().map(ShortOrderDTO::new).toList())
                .build();
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
    public OrderDTO createOrder(String email, long customerId, OrderDTO order, List<Long> list) {
        var customer = checkCustomer(customerId, email);

        var newOrder = new Order(null, LocalDateTime.now(ZoneOffset.UTC), Status.UNPAID, customer, new ArrayList<>(), "", "", BigDecimal.ZERO);

        newOrder = orderRepository.save(newOrder);

        if (order == null) {
            order = new OrderDTO();
        }

        order.setId(newOrder.getId());

        List<ProductDTO> newList = new ArrayList<>();

        if (list != null) {
            for (Long id : list) {
                Optional<Product> product = productRepository.findById(id);
                product.ifPresent(value -> newList.add(new ProductDTO(
                        value.getId(),
                        value.getName(),
                        value.getPrice(),
                        1)));
            }
        }

        order.setProducts(newList);

        return updateOrder(email, customerId, newOrder.getId(), order);
    }

    @Override
    public OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder) {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        return updateOrder(order, newOrder);
    }

    @Override
    public OrderDTO updateOrder(String email, long customerId, long orderId, OrderDTO newOrder) {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        return updateOrder(order, newOrder);
    }

    @Override
    public OrderDTO updateProductList(String email, long customerId, Long orderId, List<Long> list) {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        return updateProductList(order, list);
    }

    @Override
    public void deleteOrder(String email, long customerId, long orderId) {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        orderRepository.delete(order);
    }

    @Override
    public PageListOrders getPageOfProducts(String email, Pageable pageable) {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with email " + email + " not found!"));
        Page<Order> allOrders = orderRepository.findByCustomerId(customer.getId(), pageable);
        List<ShortOrderDTO> orders = allOrders.stream().map(ShortOrderDTO::new).toList();

        return PageListOrders.builder()
                .totalPages(allOrders.getTotalPages())
                .totalAmount(orderRepository.findAllByCustomerId(customer.getId()).size())
                .amount(orders.size())
                .orders(orders)
                .build();
    }

    @Override
    public List<ShortProductDTO> getOtherProduct(String orderId) {
        List<Long> products = productRepository.findByOrderId(Long.parseLong(orderId));
        List<ShortProductDTO> list = new ArrayList<>();
        for (Long id : products) {
            productRepository.findById(id).ifPresent(product -> list.add(new ShortProductDTO(product)));
        }
        return list;
    }

    @Override
    public OrderDTO removeProduct(String email, Long customerId, Long orderId, Long productId) {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        var product = productRepository.findById(productId);

        if (product.isPresent() && order.getProducts().size() > 1) {

            List<OrderProduct> newProducts = order.getProducts().stream()
                    .filter(item -> !item.getProduct().getId().equals(product.get().getId())).toList();

            order.getProducts().clear();

            order.getProducts().addAll(new ArrayList<>());

            orderRepository.save(order);

            order.getProducts().addAll(newProducts);

            BigDecimal price = order.getProducts().stream()
                    .map(prod -> BigDecimal.valueOf(prod.getAmount()).multiply(prod.getProduct().getPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            order.setPrice(price);
            return toOrderDTO(orderRepository.save(order));
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must be at least one product");

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

    private OrderDTO updateOrder(Order oldOrder, OrderDTO newOrder) {
        if (Optional.ofNullable(newOrder.getStatus()).isPresent()) {

            Status status = checkStatus(newOrder.getStatus());

            oldOrder.setStatus(status);
        }

        if (Optional.ofNullable(newOrder.getProducts()).isPresent()) {

            List<OrderProduct> products = new ArrayList<>();
            List<ProductDTO> inputProducts = newOrder.getProducts();

            for (ProductDTO current : inputProducts) {
                Optional<Product> product = productRepository.findById(current.getId());
                if (product.isPresent() && current.getCount() > 0) {
                    products.add(new OrderProduct(oldOrder, product.get(), current.getCount()));
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

    private OrderDTO updateProductList(Order oldOrder, List<Long> list) {
        if (!list.isEmpty()) {
            List<OrderProduct> newProducts = new ArrayList<>();
            for (Long id : list) {
                Optional<Product> product = productRepository.findById(id);
                product.ifPresent(elem -> newProducts.add(new OrderProduct(oldOrder, elem, 1)));
            }

            if (newProducts.size() > 0) {

                List<OrderProduct> oldProducts = oldOrder.getProducts();

                newProducts.addAll(oldProducts);

                setNewProductList(oldOrder, newProducts);

                oldOrder.setPrice(calculateNewPrice(oldOrder.getProducts()));

                return toOrderDTO(orderRepository.save(oldOrder));
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must be at least one product");
        }
        return toOrderDTO(oldOrder);
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
