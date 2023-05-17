package kazantseva.project.OnlineStore.controller;

import kazantseva.project.OnlineStore.model.response.PageListProducts;
import kazantseva.project.OnlineStore.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @GetMapping("/v1/products")
    public String getProducts(@RequestParam(required = false, defaultValue = "1") int page,
                              @RequestParam(required = false, defaultValue = "3") int size,
                              @RequestParam(required = false, defaultValue = "price") String sort,
                              @RequestParam(required = false, defaultValue = "asc") String direction,
                              Model model) {
        Pageable pageable = direction.equals("desc") ?
                PageRequest.of(page - 1, size, Sort.Direction.DESC, sort) :
                PageRequest.of(page - 1, size, Sort.Direction.ASC, sort);
        PageListProducts list = productService.getPageOfProducts(pageable);
        model.addAttribute("totalPages", list.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalAmount", list.getTotalAmount());
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseDirection", direction.equals("asc") ? "desc" : "asc");
        model.addAttribute("products", list.getProducts());
        return "products.html";
    }
}
