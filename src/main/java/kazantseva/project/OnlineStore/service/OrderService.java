package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.OrderException;
import kazantseva.project.OnlineStore.exceptions.ProductException;
import kazantseva.project.OnlineStore.model.entity.Order;
import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.ShortOrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<ShortOrderDTO> getOrders(String email, long customerId, Pageable pageable) throws CustomerException;

    OrderDTO getFullOrder(String email, long customerId, long orderId) throws CustomerException, OrderException;

    Order getOrder(String email, long customerId, long orderId) throws CustomerException, OrderException;

    OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder) throws OrderException, CustomerException;

    OrderDTO publishOrder(String email, long customerId, long orderId, RequestOrder newOrder) throws OrderException, CustomerException;

    void deleteOrder(String email, long customerId, long orderId) throws OrderException, CustomerException;

    void updateBasket(String email, String productId) throws CustomerException, OrderException, ProductException;
}
