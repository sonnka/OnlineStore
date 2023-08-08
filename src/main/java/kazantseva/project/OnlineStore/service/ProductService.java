package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.ProductException;
import kazantseva.project.OnlineStore.exceptions.SecurityException;
import kazantseva.project.OnlineStore.model.mongo.request.CreateProduct;
import kazantseva.project.OnlineStore.model.mongo.response.ShortProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface ProductService {
    Page<ShortProductDTO> getProductsByPage(Pageable pageable);

    Page<ShortProductDTO> getFilteredProducts(Pageable pageable, String keyword, String rating,
                                              BigDecimal from, BigDecimal to,
                                              LocalDate dateFrom, LocalDate dateTo);

    ShortProductDTO getProduct(String email, String productId) throws ProductException, CustomerException;

    ShortProductDTO updateProduct(String email, String productId, CreateProduct product) throws ProductException, CustomerException;

    void deleteProduct(String email, String productId) throws ProductException, CustomerException;

    ShortProductDTO createProduct(String email, CreateProduct product) throws CustomerException;

    void uploadImage(String email, String productId, MultipartFile file) throws ProductException, CustomerException, SecurityException;

    void uploadDataFromCsv(MultipartFile file) throws IOException;
}
