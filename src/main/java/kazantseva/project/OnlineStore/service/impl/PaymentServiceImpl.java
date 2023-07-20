package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.model.entity.PaymentInfo;
import kazantseva.project.OnlineStore.model.response.PaymentDTO;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.repository.PaymentRepository;
import kazantseva.project.OnlineStore.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Currency;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private CustomerRepository customerRepository;
    private PaymentRepository paymentRepository;

    @Override
    public Page<PaymentDTO> getPayments(String email, Pageable pageable) throws CustomerException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerException.CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        return paymentRepository.findAllByCustomerId(customer.getId(), pageable).map(this::toPaymentDTO);
    }

    private PaymentDTO toPaymentDTO(PaymentInfo paymentInfo) {
        String formatDateTime = null;
        String price = "0.00";
        String errors = "-";
        if (paymentInfo.getDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            formatDateTime = paymentInfo.getDate().format(formatter);
        }
        if (paymentInfo.getPrice() != null && paymentInfo.getCurrency() != null) {
            DecimalFormat df = new DecimalFormat("#,##0.00");

            Currency cur = Currency.getInstance(paymentInfo.getCurrency());
            String symbol = cur.getSymbol();


            price = df.format(paymentInfo.getPrice()) + " " + symbol;
        }

        if (paymentInfo.getErrors() != null) {
            errors = paymentInfo.getErrors();
        }

        return PaymentDTO.builder()
                .id(paymentInfo.getId().toString())
                .description(paymentInfo.getDescription())
                .date(formatDateTime)
                .price(price)
                .card(paymentInfo.getCard())
                .paymentStatus(paymentInfo.getPaymentStatus())
                .errors(errors)
                .build();
    }
}
