package kazantseva.project.OnlineStore.order.repository;

import kazantseva.project.OnlineStore.order.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
