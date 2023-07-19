package kazantseva.project.OnlineStore.repository;

import kazantseva.project.OnlineStore.model.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
}
