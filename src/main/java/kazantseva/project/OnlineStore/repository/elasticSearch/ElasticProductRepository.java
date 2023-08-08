package kazantseva.project.OnlineStore.repository.elasticSearch;

import kazantseva.project.OnlineStore.model.elasticSearch.entity.ElasticProduct;
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
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ElasticProductRepository {

    private static final String INDEX_NAME = "products";
    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticProductRepository(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public Page<ElasticProduct> findAll(Pageable pageable) {
        Query query = Query.findAll();
        query.setPageable(pageable);

        SearchHits<ElasticProduct> searchHits = elasticsearchOperations.search(query, ElasticProduct.class,
                IndexCoordinates.of(INDEX_NAME));

        return toPage(searchHits, query);
    }

    public Criteria searchByName(String name) {
        return Criteria.where("name").matchesAll(name);
    }

    public Criteria searchByRating(String rating) {
        return Criteria.where("rating").matchesAll(rating);
    }

    public Criteria searchByMinPrice(BigDecimal from) {
        return Criteria.where("price").greaterThanEqual(from);
    }

    public Criteria searchByMaxPrice(BigDecimal to) {
        return Criteria.where("price").lessThanEqual(to);
    }

    public Criteria searchByMinDate(LocalDate dateFrom) {
        return Criteria.where("manufacturingDate").greaterThanEqual(dateFrom);
    }

    public Criteria searchByMaxDate(LocalDate dateTo) {
        return Criteria.where("manufacturingDate").lessThanEqual(dateTo);
    }

    public void deleteAll() {
        elasticsearchOperations.delete(Query.findAll(), ElasticProduct.class);
    }

    public void deleteById(String id) {
        elasticsearchOperations.delete(id, ElasticProduct.class);
    }

    public ElasticProduct save(ElasticProduct product) {
        return elasticsearchOperations.save(product);
    }

    public void saveAll(List<ElasticProduct> products) {
        elasticsearchOperations.save(products);
    }

    public Page<ElasticProduct> search(Pageable pageable, Criteria criteria) {
        Query query = CriteriaQuery.builder(criteria)
                .build();

        query.setPageable(pageable);

        SearchHits<ElasticProduct> searchHits = elasticsearchOperations.search(query, ElasticProduct.class,
                IndexCoordinates.of(INDEX_NAME));

        return toPage(searchHits, query);
    }

    private Page<ElasticProduct> toPage(SearchHits<ElasticProduct> searchHits, Query query) {
        SearchPage<ElasticProduct> page = SearchHitSupport.searchPageFor(searchHits, query.getPageable());

        return (Page<ElasticProduct>) SearchHitSupport.unwrapSearchHits(page);
    }
}
