package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.RequestCustomer;
import kazantseva.project.OnlineStore.model.response.CustomerDTO;
import kazantseva.project.OnlineStore.model.response.FullCustomerDTO;
import kazantseva.project.OnlineStore.model.response.LoginResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomerService {
    UserDetails toUserDetails(Customer customer);

    void register(CreateCustomer customer);

    LoginResponse login(Authentication auth);

    LoginResponse login(String token);

    FullCustomerDTO getCustomer(String email, Long customerId);

    Long getCustomerId(String email);

    CustomerDTO updateCustomer(String email, long customerId, RequestCustomer customer);

    void deleteCustomer(String email, long customerId);
}
