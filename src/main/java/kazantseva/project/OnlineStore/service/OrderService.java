package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.ShortOrderDTO;
import kazantseva.project.OnlineStore.model.response.ShortProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    Page<ShortOrderDTO> getOrders(String email, long customerId, Pageable pageable);

    OrderDTO getFullOrder(String email, long customerId, long orderId);

    OrderDTO createOrder(String email, long customerId, RequestOrder order);

    List<ShortProductDTO> getProductList(String email, long customerId, long orderId);

    OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder);

    void deleteOrder(String email, long customerId, long orderId);
}
