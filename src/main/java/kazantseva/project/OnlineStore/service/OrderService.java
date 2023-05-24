package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.response.ListOrders;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.PageListOrders;
import kazantseva.project.OnlineStore.model.response.ShortProductDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    ListOrders getOrders(String email, long customerId, Pageable pageable);

    OrderDTO getFullOrder(String email, long customerId, long orderId);

    void createOrder(String email, long customerId, RequestOrder order);

    OrderDTO createOrder(String email, long customerId, OrderDTO order, List<Long> list);

    OrderDTO updateOrder(String email, long customerId, long orderId, RequestOrder newOrder);

    OrderDTO updateOrder(String email, long customerId, long orderId, OrderDTO newOrder);

    OrderDTO updateProductList(String email, long customerId, Long orderId, List<Long> list);

    void deleteOrder(String email, long customerId, long orderId);

    PageListOrders getPageOfProducts(String email, Pageable pageable);

    List<ShortProductDTO> getOtherProduct(String orderId);

    OrderDTO removeProduct(String email, Long customerId, Long orderId, Long productId);
}
