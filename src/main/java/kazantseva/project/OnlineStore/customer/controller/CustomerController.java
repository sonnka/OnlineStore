package kazantseva.project.OnlineStore.customer.controller;

import jakarta.validation.Valid;
import kazantseva.project.OnlineStore.customer.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.customer.model.response.CustomerDTO;
import kazantseva.project.OnlineStore.customer.model.response.LoginResponse;
import kazantseva.project.OnlineStore.customer.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CustomerController {

    private CustomerService customerService;

    @GetMapping("/home")
    public String hello(){
        return "Hello";
    }

    @PostMapping("/login")
    public LoginResponse login(Authentication auth){
        return customerService.login(auth);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid CreateCustomer customer){
        customerService.register(customer);
    }

    @GetMapping("/customers/{customer-id}")
    public CustomerDTO getCustomer(Authentication auth,
                                   @PathVariable("customer-id") long customerId) {
        return customerService.getCustomer(auth.getName(), customerId);
    }
}
