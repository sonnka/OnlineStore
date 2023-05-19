package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.response.ListOrders;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.PageListOrders;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    ListOrders getOrders(String email, long customerId, Pageable pageable);

    OrderDTO getFullOrder(String email, long customerId, long orderId);

    void createOrder(String email, long customerId, RequestOrder order);

    OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder);

    void deleteOrder(String email, long customerId, long orderId);

    PageListOrders getPageOfProducts(String email, Pageable pageable);
}
