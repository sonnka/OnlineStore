package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.response.ShortProductDTO;
import kazantseva.project.OnlineStore.repository.ProductRepository;
import kazantseva.project.OnlineStore.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Override
    public Page<ShortProductDTO> getProductsByPage(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ShortProductDTO::new);
    }
}
