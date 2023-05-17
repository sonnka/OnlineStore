package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.response.ListProducts;
import kazantseva.project.OnlineStore.model.response.PageListProducts;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ListProducts getProducts(Pageable pageable);

    PageListProducts getPageOfProducts(Pageable pageable);
}
