package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.response.ListProducts;
import kazantseva.project.OnlineStore.model.response.ShortProductDTO;
import kazantseva.project.OnlineStore.repository.ProductRepository;
import kazantseva.project.OnlineStore.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Override
    public ListProducts getProducts(Pageable pageable) {
        List<ShortProductDTO> products = productRepository.findAll(pageable).stream()
                .map(ShortProductDTO::new).toList();

        return ListProducts.builder()
                .totalAmount(productRepository.findAll().size())
                .amount(products.size())
                .products(products)
                .build();
    }
}
