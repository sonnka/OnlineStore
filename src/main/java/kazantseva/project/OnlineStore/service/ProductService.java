package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.mongo.CreateProduct;
import kazantseva.project.OnlineStore.model.mongo.ShortProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    Page<ShortProductDTO> getProductsByPage(Pageable pageable);

    ShortProductDTO getProduct(String email, String productId);

    ShortProductDTO updateProduct(String email, String productId, CreateProduct product);

    void deleteProduct(String email, String productId);

    ShortProductDTO createProduct(String email, CreateProduct product);

    void uploadImage(String email, String productId, MultipartFile file);
}
