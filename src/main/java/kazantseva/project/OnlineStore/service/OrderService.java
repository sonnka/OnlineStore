package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.response.ListOrders;
import kazantseva.project.OnlineStore.model.response.OrderDTO;

public interface OrderService {
    ListOrders getOrders(String email, long customerId, int page, int size, String sort, String direction);

    OrderDTO getFullOrder(String email, long customerId, long orderId);

    void createOrder(String email, long customerId, RequestOrder order);

    OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder);

    void deleteOrder(String email, long customerId, long orderId);
}
