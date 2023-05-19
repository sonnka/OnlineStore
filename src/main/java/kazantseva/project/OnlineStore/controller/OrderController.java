package kazantseva.project.OnlineStore.controller;

import kazantseva.project.OnlineStore.model.response.PageListOrders;
import kazantseva.project.OnlineStore.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class OrderController {

    OrderService orderService;

    @GetMapping("/v1/profile/orders")
    public String getOrders(@RequestParam(required = false, defaultValue = "1") int page,
                            @RequestParam(required = false, defaultValue = "3") int size,
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
}
