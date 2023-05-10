package kazantseva.project.OnlineStore.order.repository;

import kazantseva.project.OnlineStore.customer.model.entity.Customer;
import kazantseva.project.OnlineStore.order.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    List<Order> findAllByCustomerId(Long customerId);

    void deleteByCustomer(Customer customer);
}
