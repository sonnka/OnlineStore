package kazantseva.project.OnlineStore.repository;

import kazantseva.project.OnlineStore.model.entity.PaymentInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentInfo, Long> {

    Page<PaymentInfo> findAllByCustomerId(Long customerId, Pageable pageable);
}
