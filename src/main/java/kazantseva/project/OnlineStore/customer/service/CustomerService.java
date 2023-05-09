package kazantseva.project.OnlineStore.customer.service;

import kazantseva.project.OnlineStore.customer.model.entity.Customer;
import kazantseva.project.OnlineStore.customer.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.customer.model.request.RequestCustomer;
import kazantseva.project.OnlineStore.customer.model.response.CustomerDTO;
import kazantseva.project.OnlineStore.customer.model.response.LoginResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomerService {
    LoginResponse login(Authentication auth);
    UserDetails toUserDetails(Customer customer);
    CustomerDTO getCustomer(String email, Long customerId);
    void register(CreateCustomer customer);

    CustomerDTO updateCustomer(String email, long customerId, RequestCustomer customer);

    void deleteCustomer(String email, long customerId);
}
