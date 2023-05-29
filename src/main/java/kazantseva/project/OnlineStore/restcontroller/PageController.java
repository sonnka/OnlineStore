package kazantseva.project.OnlineStore.restcontroller;

import kazantseva.project.OnlineStore.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class PageController {

    private CustomerService customerService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/products_html")
    public String products() {
        return "products";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        long customerId = customerService.getCustomerId(principal.getName());
        model.addAttribute("customerId", customerId);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String profileEdit(Principal principal, Model model) {
        long customerId = customerService.getCustomerId(principal.getName());
        model.addAttribute("customerId", customerId);
        return "editprofile";
    }

    @GetMapping("/profile/orders")
    public String orders(Principal principal, Model model) {
        long customerId = customerService.getCustomerId(principal.getName());
        model.addAttribute("customerId", customerId);
        return "orders";
    }
}
