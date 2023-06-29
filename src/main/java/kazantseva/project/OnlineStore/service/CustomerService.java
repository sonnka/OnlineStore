package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.RequestCustomer;
import kazantseva.project.OnlineStore.model.response.AdminDTO;
import kazantseva.project.OnlineStore.model.response.CustomerDTO;
import kazantseva.project.OnlineStore.model.response.FullCustomerDTO;
import kazantseva.project.OnlineStore.model.response.LoginResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerService {
    UserDetails toUserDetails(Customer customer);

    void register(CreateCustomer customer);

    LoginResponse login(Authentication auth);

    LoginResponse login(String token);

    FullCustomerDTO getCustomer(String email, Long customerId);

    Long getBasketId(String email);

    Page<CustomerDTO> getCustomers(String email, Pageable pageable);

    Page<AdminDTO> getAdmins(String email, Pageable pageable);

    void toAdmin(String email, Long customerId);

    void resendEmail(String email, Long customerId);

    Long getCustomerId(String email);

    CustomerDTO updateCustomer(String email, long customerId, RequestCustomer customer);

    void uploadAvatar(String email, Long customerId, MultipartFile file);

    void deleteCustomer(String email, long customerId);
}
