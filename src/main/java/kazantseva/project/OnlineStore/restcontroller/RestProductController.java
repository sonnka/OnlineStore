package kazantseva.project.OnlineStore.restcontroller;

import kazantseva.project.OnlineStore.model.request.CreateProduct;
import kazantseva.project.OnlineStore.model.response.ShortProductDTO;
import kazantseva.project.OnlineStore.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@AllArgsConstructor
public class RestProductController {

    private ProductService productService;

    @GetMapping("/products")
    public Page<ShortProductDTO> getProducts(Pageable pageable) {
        return productService.getProductsByPage(pageable);
    }

    @GetMapping("/admin/products/{product-id}")
    public ShortProductDTO getProduct(@PathVariable("product-id") Long productId, Authentication auth) {
        return productService.getProduct(auth.getName(), productId);
    }

    @PatchMapping("/admin/products/{product-id}")
    public ShortProductDTO updateProduct(@PathVariable("product-id") Long productId,
                                         @RequestBody CreateProduct product,
                                         Authentication auth) {
        return productService.updateProduct(auth.getName(), productId, product);
    }

    @DeleteMapping("/admin/products/{product-id}")
    public void deleteProduct(@PathVariable("product-id") Long productId,
                              Authentication auth) {
        productService.deleteProduct(auth.getName(), productId);
    }

    @PostMapping("/admin/products")
    public ShortProductDTO createProduct(Authentication auth,
                                         @RequestBody CreateProduct product) {
        return productService.createProduct(auth.getName(), product);
    }

    @PostMapping("/admin/products/{product-id}/upload")
    public void uploadImage(@PathVariable("product-id") Long productId,
                            @RequestParam("file") MultipartFile file,
                            Authentication auth) {
        productService.uploadImage(auth.getName(), productId, file);
    }
}
