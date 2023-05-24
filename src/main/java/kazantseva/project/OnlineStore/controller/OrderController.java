package kazantseva.project.OnlineStore.controller;

import kazantseva.project.OnlineStore.model.entity.Status;
import kazantseva.project.OnlineStore.model.request.RequestOrderDTO;
import kazantseva.project.OnlineStore.model.response.FormDTO;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.PageListOrders;
import kazantseva.project.OnlineStore.service.CustomerService;
import kazantseva.project.OnlineStore.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;
    private CustomerService customerService;

    @GetMapping("/v1/profile/orders")
    public String getOrders(@RequestParam(required = false, defaultValue = "1") int page,
                            @RequestParam(required = false, defaultValue = "5") int size,
                            @RequestParam(required = false, defaultValue = "price") String sort,
                            @RequestParam(required = false, defaultValue = "asc") String direction,
                            Model model, Principal principal) {
        Pageable pageable = direction.equals("desc") ?
                PageRequest.of(page - 1, size, Sort.Direction.DESC, sort) :
                PageRequest.of(page - 1, size, Sort.Direction.ASC, sort);
        PageListOrders list = orderService.getPageOfProducts(principal.getName(), pageable);
        model.addAttribute("totalPages", list.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalAmount", list.getTotalAmount());
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseDirection", direction.equals("asc") ? "desc" : "asc");
        model.addAttribute("orders", list.getOrders());
        return "orders";
    }

    @GetMapping("/v1/profile/orders/create")
    public String createOrder(Principal principal, Model model) {
        String email = principal.getName();
        model.addAttribute("customer", customerService.customerProfile(email));
        model.addAttribute("order", new RequestOrderDTO());
        return "createorder";
    }

    @GetMapping("/v1/profile/orders/{order-id}")
    public String getOrder(@PathVariable(value = "order-id") String orderId, Principal principal, Model model) {
        String email = principal.getName();
        var customer = customerService.findCustomerByEmail(email);
        var order = orderService.getFullOrder(email, customer.getId(), Long.parseLong(orderId));
        model.addAttribute("order", order);
        model.addAttribute("customer", customer);
        return "order";
    }

    @GetMapping("/v1/profile/orders/{order-id}/edit")
    public String editOrder(@PathVariable(value = "order-id") String orderId, Principal principal, Model model) {
        String email = principal.getName();
        var customer = customerService.findCustomerByEmail(email);
        var order = orderService.getFullOrder(email, customer.getId(), Long.parseLong(orderId));
        model.addAttribute("order", order);
        model.addAttribute("customer", customer);
        model.addAttribute("allTypes", new String[]{Status.PAID.name(), Status.UNPAID.name()});
        model.addAttribute("list", orderService.getOtherProduct(orderId));
        model.addAttribute("form", new FormDTO());
        return "editorder";
    }

    @PostMapping("/v1/profile/orders/{order-id}/list")
    public String addProduct(@PathVariable(value = "order-id") String orderId,
                             @ModelAttribute("form") FormDTO form,
                             Principal principal, Model model) {
        String email = principal.getName();
        var customer = customerService.findCustomerByEmail(email);
        var order = orderService.getFullOrder(email, customer.getId(), Long.parseLong(orderId));
        List<Long> list = form.getSelectedItems();
        var newOrder = orderService.updateProductList(email, customer.getId(), order.getId(), list);
        model.addAttribute("order", newOrder);
        model.addAttribute("customer", customer);
        model.addAttribute("allTypes", new String[]{Status.PAID.name(), Status.UNPAID.name()});
        model.addAttribute("list", orderService.getOtherProduct(orderId));
        model.addAttribute("form", new FormDTO());
        return "redirect:/v1/profile/orders/%s/edit".formatted(orderId);
    }

    @GetMapping("/v1/profile/orders/{order-id}/products/{product-id}/delete")
    public String deleteProduct(@PathVariable(value = "order-id") String orderId,
                                @PathVariable(value = "product-id") String productId,
                                Principal principal, Model model) {
        String email = principal.getName();
        var customer = customerService.findCustomerByEmail(email);
        var order = orderService.getFullOrder(email, customer.getId(), Long.parseLong(orderId));
        var newOrder = orderService.removeProduct(email, customer.getId(), order.getId(), Long.parseLong(productId));
        model.addAttribute("order", newOrder);
        model.addAttribute("customer", customer);
        model.addAttribute("allTypes", new String[]{Status.PAID.name(), Status.UNPAID.name()});
        model.addAttribute("list", orderService.getOtherProduct(orderId));
        model.addAttribute("form", new FormDTO());
        return "redirect:/v1/profile/orders/%s/edit".formatted(orderId);
    }

    @PostMapping("/v1/profile/orders/{order-id}/edit")
    public String updateOrder(@PathVariable(value = "order-id") String orderId,
                              @ModelAttribute("order") OrderDTO order,
                              Principal principal,
                              Model model) {
        String email = principal.getName();
        var customer = customerService.findCustomerByEmail(email);
        var newOrder = orderService.updateOrder(email, customer.getId(), Long.parseLong(orderId), order);
        model.addAttribute("order", newOrder);
        model.addAttribute("customer", customer);
        model.addAttribute("allTypes", new String[]{Status.PAID.name(), Status.UNPAID.name()});
        model.addAttribute("list", orderService.getOtherProduct(orderId));
        model.addAttribute("form", new FormDTO());
        return "redirect:/v1/profile/orders/" + orderId;
    }


}
