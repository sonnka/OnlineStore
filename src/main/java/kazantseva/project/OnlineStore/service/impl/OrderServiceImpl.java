package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.CustomerException.CustomerExceptionProfile;
import kazantseva.project.OnlineStore.exceptions.OrderException;
import kazantseva.project.OnlineStore.exceptions.OrderException.OrderExceptionProfile;
import kazantseva.project.OnlineStore.exceptions.ProductException;
import kazantseva.project.OnlineStore.exceptions.ProductException.ProductExceptionProfile;
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
import org.springframework.stereotype.Service;

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
    public Page<ShortOrderDTO> getOrders(String email, long customerId, Pageable pageable) throws CustomerException {
        checkCustomer(customerId, email);

        return orderRepository.findByCustomerIdAndType(customerId, Type.PUBLISHED, pageable)
                .map(ShortOrderDTO::new);
    }

    @Override
    public OrderDTO getFullOrder(String email, long customerId, long orderId) throws CustomerException, OrderException {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        return toOrderDTO(order);
    }

    @Override
    public Order getOrder(String email, long customerId, long orderId) throws CustomerException, OrderException {
        checkCustomer(customerId, email);

        return checkOrder(orderId, customerId);
    }

    @Override
    public OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder)
            throws OrderException, CustomerException {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        if (Status.PAID == order.getStatus()) {
            throw new OrderException(OrderExceptionProfile.CANNOT_CHANGE_PAID);
        }

        var updatedOrder = updateOrder(order, newOrder);

        if (updatedOrder.getPrice().compareTo(BigDecimal.valueOf(1.0)) < 0 &&
                Type.PUBLISHED.equals(updatedOrder.getType())) {
            throw new OrderException(OrderExceptionProfile.MINIMUM_ORDER_PRICE);
        }

        return toOrderDTO(orderRepository.save(updatedOrder));
    }

    @Override
    public void updateBasket(String email, String productId)
            throws CustomerException, OrderException, ProductException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        var order = orderRepository.findById(customer.getBasket()).orElseThrow(
                () -> new OrderException(OrderExceptionProfile.ORDER_NOT_FOUND));

        List<OrderProduct> oldList = new ArrayList<>(order.getProducts());

        var product = productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductExceptionProfile.PRODUCT_NOT_FOUND));

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

    @Override
    public OrderDTO publishOrder(String email, long customerId, long orderId, RequestOrder newOrder)
            throws OrderException, CustomerException {
        var customer = checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);

        if (newOrder.getProducts().size() > 0) {
            var updatedOrder = updateOrder(order, newOrder);

            if (updatedOrder.getPrice().compareTo(BigDecimal.valueOf(1.0)) < 0) {
                throw new OrderException(OrderExceptionProfile.MINIMUM_ORDER_PRICE);
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
        } else throw new OrderException(OrderExceptionProfile.EMPTY_ORDER);
    }

    @Override
    public void deleteOrder(String email, long customerId, long orderId)
            throws OrderException, CustomerException {
        checkCustomer(customerId, email);

        var order = checkOrder(orderId, customerId);
        if (Status.UNPAID == order.getStatus()) {
            throw new OrderException(OrderExceptionProfile.CANNOT_DELETE_UNPAID);
        }
        orderRepository.delete(order);
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

    private Customer checkCustomer(long customerId, String email) throws CustomerException {
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        if (!customer.getEmail().equals(email)) {
            throw new CustomerException(CustomerExceptionProfile.EMAIL_MISMATCH);
        }

        return customer;
    }

    private Order checkOrder(long orderId, long customerId) throws OrderException, CustomerException {
        var order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(OrderExceptionProfile.ORDER_NOT_FOUND));

        if (customerId != order.getCustomer().getId()) {
            throw new CustomerException(CustomerExceptionProfile.ID_MISMATCH);
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
