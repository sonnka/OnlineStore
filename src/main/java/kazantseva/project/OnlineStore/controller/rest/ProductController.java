package kazantseva.project.OnlineStore.controller.rest;

import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.ProductException;
import kazantseva.project.OnlineStore.exceptions.SecurityException;
import kazantseva.project.OnlineStore.model.mongo.request.CreateProduct;
import kazantseva.project.OnlineStore.model.mongo.response.ShortProductDTO;
import kazantseva.project.OnlineStore.service.ProductService;
import kazantseva.project.OnlineStore.util.swagger.ProductAPI;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;


@RestController
@AllArgsConstructor
public class ProductController implements ProductAPI {

    private ProductService productService;

    @GetMapping("/products")
    public Page<ShortProductDTO> getProducts(Pageable pageable) {
        return productService.getProductsByPage(pageable);
    }

    @GetMapping("/products/search")
    public Page<ShortProductDTO> getFilteredProducts(Pageable pageable, String keyword, String rating,
                                                     BigDecimal from, BigDecimal to,
                                                     LocalDate dateFrom, LocalDate dateTo) {
        return productService.getFilteredProducts(pageable, keyword, rating, from, to, dateFrom, dateTo);
    }

    @GetMapping("/admin/products/{product-id}")
    public ShortProductDTO getProduct(@PathVariable("product-id") String productId, Authentication auth)
            throws CustomerException, ProductException {
        return productService.getProduct(auth.getName(), productId);
    }

    @PatchMapping("/admin/products/{product-id}")
    public ShortProductDTO updateProduct(@PathVariable("product-id") String productId,
                                         @RequestBody CreateProduct product,
                                         Authentication auth) throws CustomerException, ProductException {
        return productService.updateProduct(auth.getName(), productId, product);
    }

    @DeleteMapping("/admin/products/{product-id}")
    public void deleteProduct(@PathVariable("product-id") String productId,
                              Authentication auth) throws CustomerException, ProductException {
        productService.deleteProduct(auth.getName(), productId);
    }

    @PostMapping("/admin/products")
    public ShortProductDTO createProduct(Authentication auth,
                                         @RequestBody CreateProduct product) throws CustomerException {
        return productService.createProduct(auth.getName(), product);
    }

    @PostMapping("/admin/products/{product-id}/upload")
    public void uploadImage(@PathVariable("product-id") String productId,
                            @RequestParam("file") MultipartFile file,
                            Authentication auth) throws CustomerException, SecurityException, ProductException {
        productService.uploadImage(auth.getName(), productId, file);
    }

    @PostMapping("/csv/upload")
    public void uploadDataFromCsv(@RequestParam("file") MultipartFile file) throws IOException {
        productService.uploadDataFromCsv(file);
    }
}
