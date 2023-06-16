package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.entity.CustomerRole;
import kazantseva.project.OnlineStore.model.entity.Product;
import kazantseva.project.OnlineStore.model.request.CreateProduct;
import kazantseva.project.OnlineStore.model.response.ShortProductDTO;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.repository.ProductRepository;
import kazantseva.project.OnlineStore.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    private CustomerRepository customerRepository;

    @Override
    public Page<ShortProductDTO> getProductsByPage(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ShortProductDTO::new);
    }

    @Override
    public ShortProductDTO getProduct(String email, Long productId) {
        checkAdmin(email);
        return new ShortProductDTO(productRepository.findById(productId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product with id " + productId + " not found!")));
    }

    @Override
    public ShortProductDTO updateProduct(String email, Long productId, CreateProduct newProduct) {
        checkAdmin(email);

        var product = productRepository.findById(productId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product with id " + productId + " not found!"));

        Optional.ofNullable(newProduct.getImage()).ifPresent(product::setImage);
        Optional.ofNullable(newProduct.getName()).ifPresent(product::setName);
        Optional.ofNullable(newProduct.getPrice()).ifPresent(product::setPrice);

        return new ShortProductDTO(productRepository.save(product));
    }

    @Override
    public void deleteProduct(String email, Long productId) {
        checkAdmin(email);

        var product = productRepository.findById(productId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product with id " + productId + " not found!"));

        productRepository.delete(product);
    }

    @Override
    public ShortProductDTO createProduct(String email, CreateProduct product) {
        checkAdmin(email);

        List<Product> sameProducts = productRepository.findAll().stream()
                .filter(p -> p.getName().equals(product.getName())).toList();

        if (sameProducts.size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with such name already exists");
        }

        return new ShortProductDTO(
                productRepository.save(Product.builder()
                        .name(product.getName())
                        .image(product.getImage())
                        .price(product.getPrice())
                        .build())
        );
    }

    private void checkAdmin(String email) {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with email " + email + " not found!"));
        if (!CustomerRole.ADMIN.equals(customer.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
