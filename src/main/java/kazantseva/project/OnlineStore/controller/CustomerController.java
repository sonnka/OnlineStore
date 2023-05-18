package kazantseva.project.OnlineStore.controller;

import jakarta.validation.Valid;
import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.request.RequestCustomerDTO;
import kazantseva.project.OnlineStore.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class CustomerController {

    private CustomerService customerService;

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
            return "/v1/register";
        }

        customerService.saveCustomer(customerDTO);
        return "redirect:/v1/register?success";
    }
}
