package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.response.ListProducts;

public interface ProductService {
    ListProducts getProducts(int page, int size, String sort, String direction);
}
