package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.elasticSearch.entity.ElasticProduct;
import kazantseva.project.OnlineStore.model.mongo.entity.Product;
import kazantseva.project.OnlineStore.repository.elasticSearch.ElasticProductRepository;
import kazantseva.project.OnlineStore.repository.mongo.ProductRepository;
import kazantseva.project.OnlineStore.service.ElasticProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ElasticProductServiceImpl implements ElasticProductService {

    private ElasticProductRepository elasticProductRepository;
    private ProductRepository productRepository;

    @Override
    public Page<ElasticProduct> findAll(Pageable pageable) {
        return elasticProductRepository.findAll(pageable);
    }

    @Override
    public Page<ElasticProduct> search(Pageable pageable, Criteria criteria) {
        return elasticProductRepository.search(pageable, criteria);
    }

    @Override
    public Criteria searchByName(String name) {
        return elasticProductRepository.searchByName(name);
    }

    @Override
    public Criteria searchByRating(String rating) {
        return elasticProductRepository.searchByRating(rating);
    }

    @Override
    public Criteria searchByMinPrice(BigDecimal from) {
        return elasticProductRepository.searchByMinPrice(from);
    }

    @Override
    public Criteria searchByMaxPrice(BigDecimal to) {
        return elasticProductRepository.searchByMaxPrice(to);
    }

    @Override
    public Criteria searchByMinDate(LocalDate dateFrom) {
        return elasticProductRepository.searchByMinDate(dateFrom);
    }

    @Override
    public Criteria searchByMaxDate(LocalDate dateTo) {
        return elasticProductRepository.searchByMaxDate(dateTo);
    }

    @Override
    public void addProduct(Product product) {
        elasticProductRepository.save(new ElasticProduct(product));
    }

    @Override
    public void updateProduct(Product product) {
        elasticProductRepository.save(new ElasticProduct(product));
    }

    @Override
    public void deleteProduct(String productId) {
        elasticProductRepository.deleteById(productId);
    }

    @Override
    public void transferAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ElasticProduct> elasticProducts = products.stream().map(ElasticProduct::new).toList();
        elasticProductRepository.deleteAll();
        int start = 0;
        for (int i = 1000; i <= elasticProducts.size(); i += 1000) {
            List<ElasticProduct> l1 = elasticProducts.subList(start, i);
            elasticProductRepository.saveAll(l1);
            start = i;
            if (i < elasticProducts.size() && i + 1000 > elasticProducts.size()) {
                List<ElasticProduct> l2 = elasticProducts.subList(start, elasticProducts.size());
                elasticProductRepository.saveAll(l2);
            }
        }
    }
}
