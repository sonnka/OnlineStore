package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.elasticSearch.ElasticProduct;
import kazantseva.project.OnlineStore.repository.elasticSearch.ElasticProductRepository;
import kazantseva.project.OnlineStore.service.ElasticProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ElasticProductServiceImpl implements ElasticProductService {

    private ElasticProductRepository elasticProductRepository;

    @Override
    public List<ElasticProduct> getAllProducts() {
        return (List<ElasticProduct>) elasticProductRepository.findAll();
    }

    @Override
    public ElasticProduct insertProduct(ElasticProduct product) {
        return elasticProductRepository.save(product);
    }
}
