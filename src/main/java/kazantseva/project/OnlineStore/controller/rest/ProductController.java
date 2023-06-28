package kazantseva.project.OnlineStore.controller.rest;

import kazantseva.project.OnlineStore.model.mongo.CreateProduct;
import kazantseva.project.OnlineStore.model.mongo.ShortProductDTO;
import kazantseva.project.OnlineStore.service.ProductService;
import kazantseva.project.OnlineStore.util.swagger.ProductAPI;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@AllArgsConstructor
public class ProductController implements ProductAPI {

    private ProductService productService;

    @GetMapping("/products")
    public Page<ShortProductDTO> getProducts(Pageable pageable) {
        return productService.getProductsByPage(pageable);
    }

    @GetMapping("/admin/products/{product-id}")
    public ShortProductDTO getProduct(@PathVariable("product-id") String productId, Authentication auth) {
        return productService.getProduct(auth.getName(), productId);
    }

    @PatchMapping("/admin/products/{product-id}")
    public ShortProductDTO updateProduct(@PathVariable("product-id") String productId,
                                         @RequestBody CreateProduct product,
                                         Authentication auth) {
        return productService.updateProduct(auth.getName(), productId, product);
    }

    @DeleteMapping("/admin/products/{product-id}")
    public void deleteProduct(@PathVariable("product-id") String productId,
                              Authentication auth) {
        productService.deleteProduct(auth.getName(), productId);
    }

    @PostMapping("/admin/products")
    public ShortProductDTO createProduct(Authentication auth,
                                         @RequestBody CreateProduct product) {
        return productService.createProduct(auth.getName(), product);
    }

    @PostMapping("/admin/products/{product-id}/upload")
    public void uploadImage(@PathVariable("product-id") String productId,
                            @RequestParam("file") MultipartFile file,
                            Authentication auth) {
        productService.uploadImage(auth.getName(), productId, file);
    }

    @PostMapping("/csv/upload")
    public void uploadDataFromCsv(@RequestParam("file") MultipartFile file) throws IOException {
        productService.uploadDataFromCsv(file);
    }
}
