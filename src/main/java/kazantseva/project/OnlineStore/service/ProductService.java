package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.response.ShortProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ShortProductDTO> getProductsByPage(Pageable pageable);
}
