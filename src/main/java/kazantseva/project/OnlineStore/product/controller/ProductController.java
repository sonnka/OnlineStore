package kazantseva.project.OnlineStore.product.controller;

import kazantseva.project.OnlineStore.product.model.entity.Product;
import kazantseva.project.OnlineStore.product.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @GetMapping("/products")
    public List<Product> getProducts(@RequestParam(required = false, defaultValue = "0") int page,
                                     @RequestParam(required = false, defaultValue = "5") int size,
                                     @RequestParam(required = false, defaultValue = "name") String sort,
                                     @RequestParam(required = false, defaultValue = "asc") String direction){
        return productService.getProducts(page,size,sort,direction);
    }
}
