package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.ShortOrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<ShortOrderDTO> getOrders(String email, long customerId, Pageable pageable);

    OrderDTO getFullOrder(String email, long customerId, long orderId);

    OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder);

    OrderDTO publishOrder(String email, long customerId, long orderId, RequestOrder newOrder);

    void deleteOrder(String email, long customerId, long orderId);

    void updateBasket(String email, String productId);
}
