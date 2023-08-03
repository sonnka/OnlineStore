package kazantseva.project.OnlineStore.repository.elasticSearch;

import kazantseva.project.OnlineStore.model.elasticSearch.entity.ElasticProduct;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class ElasticProductRepository {

    private ElasticsearchOperations elasticsearchOperations;

    public Page<ElasticProduct> findAll(Pageable pageable) {
        Query query = Query.findAll();

        query.setPageable(pageable);
        SearchHits<ElasticProduct> searchHits = elasticsearchOperations.search(query, ElasticProduct.class,
                IndexCoordinates.of("products"));

        SearchPage<ElasticProduct> page = SearchHitSupport.searchPageFor(searchHits, query.getPageable());

        return (Page<ElasticProduct>) SearchHitSupport.unwrapSearchHits(page);
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
}
