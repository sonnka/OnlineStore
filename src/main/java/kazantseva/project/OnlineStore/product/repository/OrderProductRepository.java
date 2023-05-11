package kazantseva.project.OnlineStore.product.repository;

import kazantseva.project.OnlineStore.product.model.entity.OrderProduct;
import kazantseva.project.OnlineStore.product.model.entity.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductId> {
}
