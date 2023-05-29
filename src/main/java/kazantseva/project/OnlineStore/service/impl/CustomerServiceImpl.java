package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.entity.Status;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.RequestCustomer;
import kazantseva.project.OnlineStore.model.response.CustomerDTO;
import kazantseva.project.OnlineStore.model.response.FullCustomerDTO;
import kazantseva.project.OnlineStore.model.response.LoginResponse;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.repository.OrderRepository;
import kazantseva.project.OnlineStore.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    final PasswordEncoder passwordEncoder;
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;

    @Override
    public LoginResponse login(Authentication auth) {
        var user = customerRepository.findByEmailIgnoreCase(auth.getName()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong authentication data!"));

        return LoginResponse.builder().id(user.getId()).email(user.getEmail()).build();
    }

    @Override
    public void register(CreateCustomer customer) {
        if (customerRepository.existsByEmailIgnoreCase(customer.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, customer.getEmail() + " is already occupied!");
        }

        customerRepository.save(Customer.builder()
                .email(customer.getEmail())
                .password(passwordEncoder.encode(customer.getPassword()))
                .name(customer.getName())
                .surname(customer.getSurname())
                .build());
    }

    @Override
    public UserDetails toUserDetails(Customer customer) {
        return User.withUsername(customer.getEmail()).password(customer.getPassword()).roles("").build();
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
