package kazantseva.project.OnlineStore.service.impl;

import kazantseva.project.OnlineStore.model.elasticSearch.entity.ElasticProduct;
import kazantseva.project.OnlineStore.model.mongo.entity.Product;
import kazantseva.project.OnlineStore.repository.mongo.ProductRepository;
import kazantseva.project.OnlineStore.service.ElasticProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ElasticProductServiceImpl implements ElasticProductService {

    private static final String INDEX_NAME = "products";
    private final ElasticsearchOperations elasticsearchOperations;
    private ProductRepository productRepository;

    @Override
    public Page<ElasticProduct> findAll(Pageable pageable) {
        Query query = Query.findAll();
        query.setPageable(pageable);

        SearchHits<ElasticProduct> searchHits = elasticsearchOperations.search(query, ElasticProduct.class,
                IndexCoordinates.of(INDEX_NAME));

        return toPage(searchHits, query);
    }

    @Override
    public Page<ElasticProduct> search(Pageable pageable, Criteria criteria) {
        Query query = CriteriaQuery.builder(criteria)
                .build();

        query.setPageable(pageable);

        SearchHits<ElasticProduct> searchHits = elasticsearchOperations.search(query, ElasticProduct.class,
                IndexCoordinates.of(INDEX_NAME));

        return toPage(searchHits, query);
    }

    @Override
    public Criteria searchByName(String name) {
        return Criteria.where("name").matchesAll(name);
    }

    @Override
    public Criteria searchByRating(String rating) {
        return Criteria.where("rating").matchesAll(rating);
    }

    @Override
    public Criteria searchByMinPrice(BigDecimal from) {
        return Criteria.where("price").greaterThanEqual(from);
    }

    @Override
    public Criteria searchByMaxPrice(BigDecimal to) {
        return Criteria.where("price").lessThanEqual(to);
    }

    @Override
    public Criteria searchByMinDate(LocalDate dateFrom) {
        return Criteria.where("manufacturingDate").greaterThanEqual(dateFrom);
    }

    @Override
    public Criteria searchByMaxDate(LocalDate dateTo) {
        return Criteria.where("manufacturingDate").lessThanEqual(dateTo);
    }

    @Override
    public void addProduct(Product product) {
        save(new ElasticProduct(product));
    }

    @Override
    public void updateProduct(Product product) {
        save(new ElasticProduct(product));
    }

    @Override
    public ElasticProduct save(ElasticProduct product) {
        return elasticsearchOperations.save(product);
    }

    @Override
    public void saveAll(List<ElasticProduct> products) {
        elasticsearchOperations.save(products);
    }

    @Override
    public void deleteById(String productId) {
        elasticsearchOperations.delete(productId, ElasticProduct.class);
    }

    @Override
    public void deleteAll() {
        elasticsearchOperations.delete(Query.findAll(), ElasticProduct.class, IndexCoordinates.of(INDEX_NAME));
    }

    @Override
    public void transferAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ElasticProduct> elasticProducts = products.stream().map(ElasticProduct::new).toList();
        deleteAll();
        int start = 0;
        for (int i = 1000; i <= elasticProducts.size(); i += 1000) {
            List<ElasticProduct> l1 = elasticProducts.subList(start, i);
            saveAll(l1);
            start = i;
            if (i < elasticProducts.size() && i + 1000 > elasticProducts.size()) {
                List<ElasticProduct> l2 = elasticProducts.subList(start, elasticProducts.size());
                saveAll(l2);
            }
        }
    }

    private Page<ElasticProduct> toPage(SearchHits<ElasticProduct> searchHits, Query query) {
        SearchPage<ElasticProduct> page = SearchHitSupport.searchPageFor(searchHits, query.getPageable());

        return (Page<ElasticProduct>) SearchHitSupport.unwrapSearchHits(page);
    }
}
