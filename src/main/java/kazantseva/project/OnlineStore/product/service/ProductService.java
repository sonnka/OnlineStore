package kazantseva.project.OnlineStore.product.service;

import kazantseva.project.OnlineStore.product.model.response.ProductDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getProducts(int page, int size, String sort, String direction);
}
