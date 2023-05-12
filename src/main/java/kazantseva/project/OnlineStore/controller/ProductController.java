package kazantseva.project.OnlineStore.controller;

import kazantseva.project.OnlineStore.model.response.ListProducts;
import kazantseva.project.OnlineStore.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @GetMapping("/products")
    public ListProducts getProducts(Pageable pageable) {
        return productService.getProducts(pageable);
    }
}
