package kazantseva.project.OnlineStore.controller.rest;

import jakarta.validation.Valid;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.RequestCustomer;
import kazantseva.project.OnlineStore.model.response.AdminDTO;
import kazantseva.project.OnlineStore.model.response.CustomerDTO;
import kazantseva.project.OnlineStore.model.response.FullCustomerDTO;
import kazantseva.project.OnlineStore.model.response.LoginResponse;
import kazantseva.project.OnlineStore.service.CustomerService;
import kazantseva.project.OnlineStore.swagger.CustomerAPI;
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

    @PostMapping("/login")
    public LoginResponse login(Authentication auth) {
        return customerService.login(auth);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid CreateCustomer customer) {
        customerService.register(customer);
    }

    @GetMapping("/customers/{customer-id}")
    public FullCustomerDTO getCustomer(Authentication auth,
                                       @PathVariable("customer-id") long customerId) {
        return customerService.getCustomer(auth.getName(), customerId);
    }

    @PatchMapping("/customers/{customer-id}")
    public CustomerDTO updateCustomer(Authentication auth,
                                      @PathVariable("customer-id") long customerId,
                                      @RequestBody @Valid RequestCustomer customer) {
        return customerService.updateCustomer(auth.getName(), customerId, customer);
    }

    @PostMapping("/customers/{customer-id}/upload")
    public void uploadAvatar(Authentication auth,
                             @PathVariable("customer-id") long customerId,
                             @RequestParam("file") MultipartFile file) {
        customerService.uploadAvatar(auth.getName(), customerId, file);
    }

    @DeleteMapping("/customers/{customer-id}")
    public void deleteCustomer(Authentication auth,
                               @PathVariable("customer-id") long customerId) {
        customerService.deleteCustomer(auth.getName(), customerId);
    }

    @GetMapping("/admin/customers")
    public Page<CustomerDTO> getCustomers(Authentication auth, Pageable pageable) {
        return customerService.getCustomers(auth.getName(), pageable);
    }

    @GetMapping("/admin/admins")
    public Page<AdminDTO> getAdmins(Authentication auth, Pageable pageable) {
        return customerService.getAdmins(auth.getName(), pageable);
    }

    @PatchMapping("/admin/customers/{customer-id}/admin")
    public void toAdmin(Authentication auth,
                        @PathVariable("customer-id") long customerId) {
        customerService.toAdmin(auth.getName(), customerId);
    }

    @GetMapping("/admin/customers/{customer-id}/resend")
    public void resendEmail(Authentication auth,
                            @PathVariable("customer-id") long customerId) {
        customerService.resendEmail(auth.getName(), customerId);
    }
}
