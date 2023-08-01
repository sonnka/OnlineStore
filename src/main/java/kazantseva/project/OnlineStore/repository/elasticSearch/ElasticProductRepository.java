package kazantseva.project.OnlineStore.repository.elasticSearch;

import kazantseva.project.OnlineStore.model.elasticSearch.ElasticProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticProductRepository extends ElasticsearchRepository<ElasticProduct, String> {
}
