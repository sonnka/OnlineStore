package kazantseva.project.OnlineStore.repository;

import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.entity.Order;
import kazantseva.project.OnlineStore.model.entity.enums.Status;
import kazantseva.project.OnlineStore.model.entity.enums.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomerIdAndType(Long customerId, Type type, Pageable pageable);

    List<Order> findAllByCustomerIdAndType(Long customerId, Type type);

    List<Order> findAllByCustomerIdAndTypeAndStatus(Long customerId, Type type, Status status);

    void deleteByCustomer(Customer customer);
}
