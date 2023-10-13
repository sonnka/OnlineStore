package kazantseva.project.OnlineStore.service;

import com.stripe.exception.StripeException;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.SecurityException;
import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.request.AuthRequest;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.RequestCustomer;
import kazantseva.project.OnlineStore.model.response.AdminDTO;
import kazantseva.project.OnlineStore.model.response.CustomerDTO;
import kazantseva.project.OnlineStore.model.response.FullCustomerDTO;
import kazantseva.project.OnlineStore.model.response.LoginResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerService {
    UserDetails toUserDetails(Customer customer);

    void register(CreateCustomer customer) throws CustomerException, SecurityException, StripeException;

    LoginResponse login(AuthRequest auth) throws CustomerException, SecurityException;

    LoginResponse login(String token) throws CustomerException, SecurityException;

    FullCustomerDTO getCustomer(String email, Long customerId) throws CustomerException;

    Long getBasketId(String email) throws CustomerException;

    Page<CustomerDTO> getCustomers(String email, Pageable pageable) throws CustomerException;

    Page<AdminDTO> getAdmins(String email, Pageable pageable) throws CustomerException;

    void toAdmin(String email, Long customerId) throws CustomerException, SecurityException, StripeException;

    void resendEmail(String email, Long customerId) throws CustomerException, SecurityException;

    Long getCustomerId(String email) throws CustomerException;

    CustomerDTO updateCustomer(String email, long customerId, RequestCustomer customer) throws CustomerException, StripeException;

    void uploadAvatar(String email, Long customerId, MultipartFile file) throws CustomerException;

    void deleteCustomer(String email, long customerId) throws CustomerException, SecurityException, StripeException;
}
