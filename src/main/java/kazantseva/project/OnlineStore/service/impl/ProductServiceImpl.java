package kazantseva.project.OnlineStore.service.impl;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.CustomerException.CustomerExceptionProfile;
import kazantseva.project.OnlineStore.exceptions.ProductException;
import kazantseva.project.OnlineStore.exceptions.ProductException.ProductExceptionProfile;
import kazantseva.project.OnlineStore.model.entity.CustomerRole;
import kazantseva.project.OnlineStore.model.mongo.entity.Product;
import kazantseva.project.OnlineStore.model.mongo.request.CreateProduct;
import kazantseva.project.OnlineStore.model.mongo.response.ShortProductDTO;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.repository.mongo.ProductRepository;
import kazantseva.project.OnlineStore.service.ProductService;
import kazantseva.project.OnlineStore.util.Util;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    public static String UPLOAD_DIRECTORY = "tmp/images/products";

    private ProductRepository productRepository;
    private CustomerRepository customerRepository;

    @Override
    @Cacheable(value = "products", key = "#pageable")
    public Page<ShortProductDTO> getProductsByPage(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ShortProductDTO::new);
    }

    @Override
    public Page<ShortProductDTO> getProductsByPageAndKeyword(Pageable pageable, String keyword) {
        if (keyword == null || keyword.equals("") || keyword.equals("-")) {
            return getProductsByPage(pageable);
        }

        return productRepository.searchAllByNameContainsIgnoreCase(pageable, keyword)
                .map(ShortProductDTO::new);
    }

    @Override
    public ShortProductDTO getProduct(String email, String productId) throws ProductException, CustomerException {
        checkAdmin(email);

        return new ShortProductDTO(productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductExceptionProfile.PRODUCT_NOT_FOUND)));
    }

    @Override
    public ShortProductDTO updateProduct(String email, String productId, CreateProduct newProduct)
            throws ProductException, CustomerException {
        checkAdmin(email);

        var product = productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductExceptionProfile.PRODUCT_NOT_FOUND));

        Optional.ofNullable(newProduct.getName()).ifPresent(product::setName);
        Optional.ofNullable(newProduct.getImage()).ifPresent(product::setImage);
        Optional.ofNullable(newProduct.getCategory()).ifPresent(product::setCategory);
        Optional.ofNullable(newProduct.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(newProduct.getPrice()).ifPresent(product::setPrice);

        return new ShortProductDTO(productRepository.save(product));
    }

    @Override
    public void uploadImage(String email, String productId, MultipartFile file)
            throws ProductException, CustomerException {
        checkAdmin(email);

        var product = productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductExceptionProfile.PRODUCT_NOT_FOUND));

        if (file != null && file.getOriginalFilename() != null) {

            String fileExtension = file.getOriginalFilename().split("\\.")[1];

            String generatedFileName = "product_" + productId + "." + fileExtension;

            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, generatedFileName);

            product.setImage("");
            product.setImage(generatedFileName);
            productRepository.save(product);

            try {
                Files.createDirectories(Path.of(UPLOAD_DIRECTORY));

                Files.copy(file.getInputStream(), fileNameAndPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new ProductException(ProductExceptionProfile.FAIL_UPLOAD_IMAGE);
            }
        }
    }

    @Override
    public void deleteProduct(String email, String productId) throws ProductException, CustomerException {
        checkAdmin(email);

        var product = productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductExceptionProfile.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
    }

    @Override
    public ShortProductDTO createProduct(String email, CreateProduct product) throws CustomerException {
        checkAdmin(email);

        return new ShortProductDTO(
                productRepository.save(Product.builder()
                        .name(product.getName())
                        .category(product.getCategory())
                        .description(product.getDescription())
                        .image(product.getImage())
                        .price(product.getPrice())
                        .build())
        );
    }

    @Override
    public void uploadDataFromCsv(MultipartFile file) throws IOException {
        CsvToBean<Product> csvDataList = new CsvToBeanBuilder<Product>(new InputStreamReader(file.getInputStream()))
                .withType(Product.class)
                .withSeparator(',')
                .withIgnoreEmptyLine(true)
                .build();

        List<Product> products = csvDataList.parse();

        products.removeIf(Objects::isNull);

        List<Product> removeList = new ArrayList<>();

        for (Product product : products) {
            product.setPrice(Util.formatPrice(product.getStringPrice()));
            if (BigDecimal.ZERO.compareTo(product.getPrice()) >= 0) {
                removeList.add(product);
            } else {
                product.setRating(Util.randomRating());
                product.setAvailable(Util.randomAvailable());
                product.setCalories(Util.randomCalories());
                product.setManufacturingDate(Util.randomManufacturingDate());
                product.setExpiryDate(Util.randomExpiryDate());
            }
        }
        products.removeAll(new HashSet<>(removeList));

        productRepository.saveAll(products);
    }

    private void checkAdmin(String email) throws CustomerException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        if (!CustomerRole.ADMIN.equals(customer.getRole())) {
            throw new CustomerException(CustomerExceptionProfile.NOT_ADMIN);
        }
    }
}
