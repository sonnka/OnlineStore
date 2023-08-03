package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.elasticSearch.entity.ElasticProduct;
import kazantseva.project.OnlineStore.model.mongo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ElasticProductService {
    Page<ElasticProduct> findAll(Pageable pageable);

    void addProduct(Product product);

    void updateProduct(Product product);

    void deleteProduct(String productId);

    void transferAllProducts();
}
