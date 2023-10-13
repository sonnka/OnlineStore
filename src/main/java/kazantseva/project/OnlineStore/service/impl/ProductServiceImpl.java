package kazantseva.project.OnlineStore.service.impl;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.CustomerException.CustomerExceptionProfile;
import kazantseva.project.OnlineStore.exceptions.ProductException;
import kazantseva.project.OnlineStore.exceptions.ProductException.ProductExceptionProfile;
import kazantseva.project.OnlineStore.model.entity.enums.CustomerRole;
import kazantseva.project.OnlineStore.model.mongo.entity.Product;
import kazantseva.project.OnlineStore.model.mongo.request.CreateProduct;
import kazantseva.project.OnlineStore.model.mongo.response.ShortProductDTO;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.repository.mongo.ProductRepository;
import kazantseva.project.OnlineStore.service.ElasticProductService;
import kazantseva.project.OnlineStore.service.ProductService;
import kazantseva.project.OnlineStore.util.Util;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    public static final String UPLOAD_DIRECTORY = "tmp/images/products";

    private ProductRepository productRepository;
    private CustomerRepository customerRepository;
    private ElasticProductService elasticProductService;

    @Override
//    @Cacheable(value = "products", key = "#pageable")
    public Page<ShortProductDTO> getProductsByPage(Pageable pageable) {
        return elasticProductService.findAll(pageable).map(p -> productRepository.findById(p.getId()).get())
                .map(ShortProductDTO::new);
    }

    @Override
    public Page<ShortProductDTO> getFilteredProducts(Pageable pageable, String keyword, String rating,
                                                     BigDecimal from, BigDecimal to,
                                                     LocalDate dateFrom, LocalDate dateTo) {
        Criteria criteria = null;

        if (keyword != null && !keyword.isEmpty()) {
            criteria = elasticProductService.searchByName(keyword);
        }

        if (Util.isValidRating(rating)) {
            if (criteria == null) {
                criteria = elasticProductService.searchByRating(rating);
            } else {
                criteria.and(elasticProductService.searchByRating(rating));
            }
        }

        if (Util.isValidMinPrice(from)) {
            if (criteria == null) {
                criteria = elasticProductService.searchByMinPrice(from);
            } else {
                criteria.and(elasticProductService.searchByMinPrice(from));
            }
        }

        if (Util.isValidMaxPrice(to)) {
            if (criteria == null) {
                criteria = elasticProductService.searchByMaxPrice(to);
            } else {
                criteria.and(elasticProductService.searchByMaxPrice(to));
            }
        }

        if (Util.isValidMinDate(dateFrom)) {
            if (criteria == null) {
                criteria = elasticProductService.searchByMinDate(dateFrom);
            } else {
                criteria.and(elasticProductService.searchByMinDate(dateFrom));
            }
        }

        if (Util.isValidMaxDate(dateTo)) {
            if (criteria == null) {
                criteria = elasticProductService.searchByMaxDate(dateTo);
            } else {
                criteria.and(elasticProductService.searchByMaxDate(dateTo));
            }
        }

        if (criteria == null) {
            return getProductsByPage(pageable);
        }

        return elasticProductService.search(pageable, criteria)
                .map(p -> productRepository.findById(p.getId()).get())
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

        Product updatedProduct = productRepository.save(product);

        elasticProductService.updateProduct(updatedProduct);

        return new ShortProductDTO(updatedProduct);
    }

    @Override
    public void uploadImage(String email, String productId, MultipartFile file)
            throws ProductException, CustomerException {
        checkAdmin(email);

        var product = productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductExceptionProfile.PRODUCT_NOT_FOUND));

        if (file != null) {
            String fileExtension = "jpg";

            var originalFileName = file.getOriginalFilename();

            if (originalFileName != null && (originalFileName.split("\\.").length > 1)) {
                int length = originalFileName.split("\\.").length;

                fileExtension = originalFileName.split("\\.")[length - 1];
            }

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

        elasticProductService.deleteById(productId);

        productRepository.delete(product);
    }

    @Override
    public ShortProductDTO createProduct(String email, CreateProduct product) throws CustomerException {
        checkAdmin(email);

        Product createdProduct = productRepository.save(Product.builder()
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription())
                .image(product.getImage())
                .price(product.getPrice())
                .build());

        elasticProductService.addProduct(createdProduct);

        return new ShortProductDTO(createdProduct);
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
            product.setName(product.getName().trim());
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

        elasticProductService.transferAllProducts();
    }

    private void checkAdmin(String email) throws CustomerException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        if (!CustomerRole.ADMIN.equals(customer.getRole())) {
            throw new CustomerException(CustomerExceptionProfile.NOT_ADMIN);
        }
    }
}
