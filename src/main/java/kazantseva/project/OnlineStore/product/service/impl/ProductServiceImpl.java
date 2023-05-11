package kazantseva.project.OnlineStore.product.service.impl;

import kazantseva.project.OnlineStore.product.model.response.ProductDTO;
import kazantseva.project.OnlineStore.product.repository.ProductRepository;
import kazantseva.project.OnlineStore.product.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    @Override
    public List<ProductDTO> getProducts(int page, int size, String sort, String direction) {
        Pageable pageable = direction.equals("desc") ?
                PageRequest.of(page, size, Sort.Direction.DESC, sort) :
                PageRequest.of(page, size, Sort.Direction.ASC, sort);
        return productRepository.findAll(pageable).stream().map(ProductDTO::new).toList();
    }
}
