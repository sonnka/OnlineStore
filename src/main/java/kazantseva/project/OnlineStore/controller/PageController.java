package kazantseva.project.OnlineStore.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.OrderException;
import kazantseva.project.OnlineStore.exceptions.SecurityException;
import kazantseva.project.OnlineStore.model.entity.enums.Currency;
import kazantseva.project.OnlineStore.model.request.ChargeRequest;
import kazantseva.project.OnlineStore.service.CustomerService;
import kazantseva.project.OnlineStore.service.OrderService;
import kazantseva.project.OnlineStore.service.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;

@Controller
public class PageController {

    private final CustomerService customerService;
    private final OrderService orderService;
    private final StripeService stripeService;

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    public PageController(CustomerService customerService, OrderService orderService, StripeService stripeService) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.stripeService = stripeService;
    }

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

    @GetMapping("/payments_html")
    public String payments() {
        return "payments";
    }

    @GetMapping("/subscriptions_html")
    public String subscriptions() {
        return "subscription";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) throws CustomerException {
        long customerId = customerService.getCustomerId(principal.getName());

        model.addAttribute("customerId", customerId);

        return "profile";
    }

    @GetMapping("/profile/edit")
    public String profileEdit(Principal principal, Model model) throws CustomerException {
        long customerId = customerService.getCustomerId(principal.getName());

        model.addAttribute("customerId", customerId);

        return "editprofile";
    }

    @GetMapping("/profile/basket")
    public String basket(Principal principal, Model model) throws CustomerException {
        long customerId = customerService.getCustomerId(principal.getName());
        long basketId = customerService.getBasketId(principal.getName());

        model.addAttribute("orderId", -2L);
        model.addAttribute("basketId", basketId);
        model.addAttribute("customerId", customerId);

        return "basket";
    }

    @GetMapping("/profile/orders")
    public String orders(Principal principal, Model model) throws CustomerException {
        long customerId = customerService.getCustomerId(principal.getName());

        model.addAttribute("customerId", customerId);

        return "orders";
    }

    @GetMapping("/profile/orders/{order-id}")
    public String order(@PathVariable(value = "order-id") String orderId, Principal principal, Model model)
            throws CustomerException, OrderException {
        long customerId = customerService.getCustomerId(principal.getName());
        var order = orderService.getOrder(principal.getName(), customerId, Long.parseLong(orderId));

        model.addAttribute("payStatus", "");

        model.addAttribute("orderId", Long.valueOf(orderId));
        model.addAttribute("customerId", customerId);

        model.addAttribute("amount", order.getPrice().multiply(BigDecimal.valueOf(100.0)).intValue());
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", Currency.USD);

        return "order";
    }

    @PostMapping("/profile/orders/{order-id}/charge")
    public String charge(@PathVariable(value = "order-id") String orderId, Principal principal,
                         ChargeRequest chargeRequest, Model model)
            throws CustomerException, OrderException {

        long customerId = customerService.getCustomerId(principal.getName());
        var order = orderService.getOrder(principal.getName(), customerId, Long.parseLong(orderId));

        model.addAttribute("orderId", Long.valueOf(orderId));
        model.addAttribute("customerId", customerId);
        model.addAttribute("amount", order.getPrice().multiply(BigDecimal.valueOf(100.0)).intValue());
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", Currency.USD);

        chargeRequest.setDescription("Example charge");
        chargeRequest.setCurrency(Currency.EUR);

        Charge charge;

        try {
            charge = stripeService.charge(principal.getName(), chargeRequest, orderId);
        } catch (StripeException e) {
            return "redirect:/profile/orders/" + orderId + "?error_payment";
        }

        model.addAttribute("payStatus", charge.getStatus());

        return "order";
    }

    @GetMapping("/profile/orders/{order-id}/edit")
    public String editOrder(@PathVariable(value = "order-id") String orderId, Principal principal, Model model) throws CustomerException {
        long customerId = customerService.getCustomerId(principal.getName());

        model.addAttribute("orderId", Long.valueOf(orderId));
        model.addAttribute("customerId", customerId);

        return "editorder";
    }

    @GetMapping("/confirm-email")
    public String confirm(@RequestParam String token) throws CustomerException, SecurityException {
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
