package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.response.ListProducts;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ListProducts getProducts(Pageable pageable);
}
