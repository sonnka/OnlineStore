package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.entity.Status;
import kazantseva.project.OnlineStore.model.entity.VerificationToken;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.RequestCustomer;
import kazantseva.project.OnlineStore.model.response.CustomerDTO;
import kazantseva.project.OnlineStore.model.response.FullCustomerDTO;
import kazantseva.project.OnlineStore.model.response.LoginResponse;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.repository.OrderRepository;
import kazantseva.project.OnlineStore.repository.VerificationTokenRepository;
import kazantseva.project.OnlineStore.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    final PasswordEncoder passwordEncoder;
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;
    private VerificationTokenRepository tokenRepository;

    private JavaMailSender emailSender;

    @Override
    public void register(CreateCustomer newCustomer) {
        if (customerRepository.existsByEmailIgnoreCase(newCustomer.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, newCustomer.getEmail() + " is already occupied!");
        }

        Customer customer = customerRepository.save(Customer.builder()
                .email(newCustomer.getEmail())
                .password(passwordEncoder.encode(newCustomer.getPassword()))
                .name(newCustomer.getName())
                .surname(newCustomer.getSurname())
                .build());

        VerificationToken verificationToken = new VerificationToken(customer);

        tokenRepository.save(verificationToken);

        sendMessage(customer.getEmail(), "Complete Registration", "To confirm your account, please click here : "
                + "http://localhost:8080/confirm-email?token=" + verificationToken.getToken(), "sofiia.kazantseva@faceit.com.ua");
    }

    @Override
    public LoginResponse login(Authentication auth) {
        var customer = customerRepository.findByEmailIgnoreCase(auth.getName()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong authentication data!"));

        return LoginResponse.builder().id(customer.getId()).email(customer.getEmail()).build();
    }

    @Override
    @Transactional
    public LoginResponse login(String token) {
        var customer = customerRepository.findByVerificationToken(token).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid verification token!"));

        verifyAccount(customer.getEmail(), token);

        return LoginResponse.builder().id(customer.getId()).email(customer.getEmail()).build();
    }

    @Override
    public UserDetails toUserDetails(Customer customer) {
        return User.withUsername(customer.getEmail())
                .password(customer.getPassword())
                .disabled(!customer.isEnabled())
                .roles("").build();
    }

    @Override
    public FullCustomerDTO getCustomer(String email, Long customerId) {
        var customer = findByIdAndCheckByEmail(customerId, email);
        return toFullCustomerDTO(customer);
    }

    @Override
    public Long getCustomerId(String email) {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with email " + email + " not found!"));
        return customer.getId();
    }

    @Override
    public CustomerDTO updateCustomer(String email, long customerId, RequestCustomer customer) {
        var oldCustomer = findByIdAndCheckByEmail(customerId, email);

        Optional.ofNullable(customer.getName()).ifPresent(oldCustomer::setName);
        Optional.ofNullable(customer.getSurname()).ifPresent(oldCustomer::setSurname);

        customerRepository.save(oldCustomer);

        return toCustomerDTO(oldCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(String email, long customerId) {
        var customer = findByIdAndCheckByEmail(customerId, email);

        orderRepository.deleteByCustomer(customer);
        customerRepository.delete(customer);
    }

    public void sendMessage(String to, String subject, String text, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void verifyAccount(String email, String verificationToken) {
        VerificationToken token = tokenRepository.findByToken(verificationToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No such verification token!"));

        if (token != null) {

            if (!email.equals(token.getCustomer().getEmail())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Failed email verification!");
            }

            Customer customer = customerRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Customer with email " + email + " not found!"));

            if (token.getExpiryDate().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
                tokenRepository.deleteByCustomerId(customer.getId());
                customerRepository.delete(customer);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "You token is expired! Please, register again!");
            }

            tokenRepository.deleteByCustomerId(customer.getId());
            customer.setEnabled(true);
            customerRepository.save(customer);
        } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid verification token!");
    }

    private Customer findByIdAndCheckByEmail(Long customerId, String email) {
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with ID " + customerId + " not found!"));

        if (!customer.getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return customer;
    }

    private CustomerDTO toCustomerDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .email(customer.getEmail())
                .build();
    }

    private FullCustomerDTO toFullCustomerDTO(Customer customer) {
        return FullCustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .email(customer.getEmail())
                .totalAmountOfOrders(orderRepository.findAllByCustomerId(customer.getId()).size())
                .amountOfPaidOrders(orderRepository.findAllByCustomerId(customer.getId())
                        .stream()
                        .filter(order -> order.getStatus().equals(Status.PAID))
                        .toList()
                        .size())
                .amountOfUnpaidOrders(orderRepository.findAllByCustomerId(customer.getId())
                        .stream()
                        .filter(order -> order.getStatus().equals(Status.UNPAID))
                        .toList()
                        .size())
                .build();
    }
}
