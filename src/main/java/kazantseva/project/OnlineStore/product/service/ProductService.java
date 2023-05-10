package kazantseva.project.OnlineStore.product.service;

import kazantseva.project.OnlineStore.product.model.response.ListProducts;

public interface ProductService {
    ListProducts getProducts(int page, int size, String sort, String direction);
}
