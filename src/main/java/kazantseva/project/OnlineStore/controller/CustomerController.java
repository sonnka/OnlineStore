package kazantseva.project.OnlineStore.controller;

import jakarta.validation.Valid;
import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.request.RequestCustomer;
import kazantseva.project.OnlineStore.model.request.RequestCustomerDTO;
import kazantseva.project.OnlineStore.model.response.FullCustomerDTO;
import kazantseva.project.OnlineStore.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class CustomerController {

    private CustomerService customerService;

    @GetMapping("/")
    public String home(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute(customerService.findCustomerByEmail(principal.getName()).getId());
        }
        return "index";
    }

    @GetMapping("/v1/register")
    public String showRegistrationForm(Model model) {
        RequestCustomerDTO customer = new RequestCustomerDTO();
        model.addAttribute("customer", customer);
        return "register";
    }

    @PostMapping("/v1/register/save")
    public String registration(@Valid @ModelAttribute("customer") RequestCustomerDTO customerDTO,
                               BindingResult result,
                               Model model) {
        Customer existingCustomer = customerService.findCustomerByEmail(customerDTO.getEmail());

        if (existingCustomer != null && existingCustomer.getEmail() != null && !existingCustomer.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("customer", customerDTO);
            return "redirect:/v1/register?error";
        }

        customerService.saveCustomer(customerDTO);
        return "redirect:/v1/register?success";
    }

    @GetMapping("/v1/login")
    public String login() {
        return "login";
    }

    @GetMapping("/v1/profile")
    public String getProfile(Model model, Principal principal) {
        String email = principal.getName();
        model.addAttribute("customer", customerService.customerProfile(email));
        return "profile";
    }

    @GetMapping("/v1/profile/edit")
    public String editProfile(Model model, Principal principal) {
        String email = principal.getName();
        FullCustomerDTO fullCustomer = customerService.customerProfile(email);
        RequestCustomer customer = RequestCustomer.builder()
                .name(fullCustomer.getName())
                .surname(fullCustomer.getSurname())
                .build();
        model.addAttribute("customer", customer);
        model.addAttribute("email", fullCustomer.getEmail());
        model.addAttribute("totalAmountOfOrders", fullCustomer.getTotalAmountOfOrders());
        return "editprofile";
    }

    @PostMapping("/v1/profile/edit/save")
    public String saveProfile(@Valid @ModelAttribute("newCustomer") RequestCustomer newCustomer,
                              BindingResult result,
                              Model model,
                              Principal principal) {
        String email = principal.getName();

        if (result.hasErrors()) {
            FullCustomerDTO fullCustomer = customerService.customerProfile(email);
            RequestCustomer customer = RequestCustomer.builder()
                    .name(fullCustomer.getName())
                    .surname(fullCustomer.getSurname())
                    .build();
            model.addAttribute("customer", customer);
            model.addAttribute("email", fullCustomer.getEmail());
            model.addAttribute("totalAmountOfOrders", fullCustomer.getTotalAmountOfOrders());
            return "redirect:/v1/profile/edit?error";
        }
        customerService.updateCustomerProfile(email, newCustomer);
        model.addAttribute("customer", customerService.customerProfile(email));
        return "profile";
    }

    @GetMapping("/v1/profile/delete")
    public String deleteProfile(Principal principal) {
        String email = principal.getName();
        customerService.deleteProfile(email);
        return "redirect:/v1/logout";
    }
}
