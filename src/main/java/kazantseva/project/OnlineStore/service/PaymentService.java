package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.model.response.PaymentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    Page<PaymentDTO> getPayments(String email, Pageable pageable) throws CustomerException;
}
