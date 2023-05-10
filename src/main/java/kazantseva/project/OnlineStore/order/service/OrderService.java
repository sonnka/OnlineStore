package kazantseva.project.OnlineStore.order.service;

import kazantseva.project.OnlineStore.order.model.request.CreateOrder;
import kazantseva.project.OnlineStore.order.model.response.ListOrders;
import kazantseva.project.OnlineStore.order.model.response.OrderDTO;

public interface OrderService {
    ListOrders getOrders(String email, long customerId);

    OrderDTO getFullOrder(String email, long customerId, long orderId);

    void createOrder(String email, long customerId, CreateOrder order);
}
