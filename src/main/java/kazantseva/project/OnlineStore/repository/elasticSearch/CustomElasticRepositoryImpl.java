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

import java.math.BigDecimal;
import java.time.LocalDate;

public class CustomElasticRepositoryImpl implements CustomElasticRepository {
    private static final String INDEX_NAME = "products";
    private final ElasticsearchOperations elasticsearchOperations;

    public CustomElasticRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public Page<ElasticProduct> findAllProducts(Pageable pageable) {
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

    private Page<ElasticProduct> toPage(SearchHits<ElasticProduct> searchHits, Query query) {
        SearchPage<ElasticProduct> page = SearchHitSupport.searchPageFor(searchHits, query.getPageable());

        return (Page<ElasticProduct>) SearchHitSupport.unwrapSearchHits(page);
    }
}
