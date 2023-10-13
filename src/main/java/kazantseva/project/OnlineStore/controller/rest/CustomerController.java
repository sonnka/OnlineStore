package kazantseva.project.OnlineStore.controller.rest;

import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.SecurityException;
import kazantseva.project.OnlineStore.model.request.AuthRequest;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.RequestCustomer;
import kazantseva.project.OnlineStore.model.response.*;
import kazantseva.project.OnlineStore.service.CustomerService;
import kazantseva.project.OnlineStore.service.PaymentService;
import kazantseva.project.OnlineStore.util.swagger.CustomerAPI;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class CustomerController implements CustomerAPI {

    private CustomerService customerService;
    private PaymentService paymentService;

    @PostMapping("/login")
    public LoginResponse login(AuthRequest auth) throws CustomerException, SecurityException {
        return customerService.login(auth);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid CreateCustomer customer)
            throws CustomerException, SecurityException, StripeException {
        customerService.register(customer);
    }

    @GetMapping("/customers/{customer-id}")
    public FullCustomerDTO getCustomer(Authentication auth,
                                       @PathVariable("customer-id") long customerId) throws CustomerException {
        return customerService.getCustomer(auth.getName(), customerId);
    }

    @PatchMapping("/customers/{customer-id}")
    public CustomerDTO updateCustomer(Authentication auth,
                                      @PathVariable("customer-id") long customerId,
                                      @RequestBody @Valid RequestCustomer customer)
            throws CustomerException, StripeException {
        return customerService.updateCustomer(auth.getName(), customerId, customer);
    }

    @PostMapping("/customers/{customer-id}/upload")
    public void uploadAvatar(Authentication auth,
                             @PathVariable("customer-id") long customerId,
                             @RequestParam("file") MultipartFile file) throws CustomerException {
        customerService.uploadAvatar(auth.getName(), customerId, file);
    }

    @DeleteMapping("/customers/{customer-id}")
    public void deleteCustomer(Authentication auth,
                               @PathVariable("customer-id") long customerId)
            throws CustomerException, SecurityException, StripeException {
        customerService.deleteCustomer(auth.getName(), customerId);
    }

    @GetMapping("/admin/customers")
    public Page<CustomerDTO> getCustomers(Authentication auth, Pageable pageable) throws CustomerException {
        return customerService.getCustomers(auth.getName(), pageable);
    }

    @GetMapping("/admin/admins")
    public Page<AdminDTO> getAdmins(Authentication auth, Pageable pageable) throws CustomerException {
        return customerService.getAdmins(auth.getName(), pageable);
    }

    @GetMapping("/payments")
    public Page<PaymentDTO> getPayments(Authentication auth, Pageable pageable) throws CustomerException {
        return paymentService.getPayments(auth.getName(), pageable);
    }

    @PatchMapping("/admin/customers/{customer-id}/admin")
    public void toAdmin(Authentication auth,
                        @PathVariable("customer-id") long customerId)
            throws CustomerException, SecurityException, StripeException {
        customerService.toAdmin(auth.getName(), customerId);
    }

    @GetMapping("/admin/customers/{customer-id}/resend")
    public void resendEmail(Authentication auth,
                            @PathVariable("customer-id") long customerId)
            throws CustomerException, SecurityException {
        customerService.resendEmail(auth.getName(), customerId);
    }
}
