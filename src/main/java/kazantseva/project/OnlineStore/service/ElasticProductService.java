package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.mongo.entity.Product;

public interface ElasticProductService {

    void addProduct(Product product);

    void updateProduct(Product product);

    void transferAllProducts();
}
