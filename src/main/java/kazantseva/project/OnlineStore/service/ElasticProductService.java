package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.elasticSearch.entity.ElasticProduct;
import kazantseva.project.OnlineStore.model.mongo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Criteria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ElasticProductService {

    Page<ElasticProduct> findAll(Pageable pageable);

    Page<ElasticProduct> search(Pageable pageable, Criteria criteria);

    Criteria searchByName(String name);

    Criteria searchByRating(String rating);

    Criteria searchByMinPrice(BigDecimal from);

    Criteria searchByMaxPrice(BigDecimal to);

    Criteria searchByMinDate(LocalDate dateFrom);

    Criteria searchByMaxDate(LocalDate dateTo);

    void addProduct(Product product);

    void updateProduct(Product product);

    ElasticProduct save(ElasticProduct product);

    void saveAll(List<ElasticProduct> products);

    void deleteById(String productId);

    void deleteAll();

    void transferAllProducts();
}
