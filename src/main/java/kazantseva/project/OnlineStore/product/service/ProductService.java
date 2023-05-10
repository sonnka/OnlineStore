package kazantseva.project.OnlineStore.product.service;

import kazantseva.project.OnlineStore.product.model.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> getProducts(int page, int size, String sort, String direction);
}
