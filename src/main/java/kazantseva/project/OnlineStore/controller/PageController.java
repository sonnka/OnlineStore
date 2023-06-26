package kazantseva.project.OnlineStore.controller;

import kazantseva.project.OnlineStore.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/profile/orders/create")
    public String createOrder(Principal principal, Model model) {
        long customerId = customerService.getCustomerId(principal.getName());
        model.addAttribute("orderId", -1L);
        model.addAttribute("customerId", customerId);
        return "editorder";
    }

    @GetMapping("/profile/orders/{order-id}")
    public String order(@PathVariable(value = "order-id") String orderId, Principal principal, Model model) {
        long customerId = customerService.getCustomerId(principal.getName());
        model.addAttribute("orderId", Long.valueOf(orderId));
        model.addAttribute("customerId", customerId);
        return "order";
    }

    @GetMapping("/profile/orders/{order-id}/edit")
    public String editOrder(@PathVariable(value = "order-id") String orderId, Principal principal, Model model) {
        long customerId = customerService.getCustomerId(principal.getName());
        model.addAttribute("orderId", Long.valueOf(orderId));
        model.addAttribute("customerId", customerId);
        return "editorder";
    }


    @GetMapping("/confirm-email")
    public String confirm(@RequestParam String token) {
        customerService.login(token);
        return "login";
    }

    @GetMapping("/admin/customers_list")
    public String customers() {
        return "customers";
    }

    @GetMapping("/admin/admins_list")
    public String admins() {
        return "admins";
    }

    @GetMapping("/admin/products/{product-id}/edit")
    public String editProduct(@PathVariable(value = "product-id") String productId, Model model) {
        model.addAttribute("productId", productId);
        return "editproduct";
    }

    @GetMapping("/admin/products/create")
    public String addProduct(Model model) {
        model.addAttribute("productId", "-1");
        return "editproduct";
    }
}
