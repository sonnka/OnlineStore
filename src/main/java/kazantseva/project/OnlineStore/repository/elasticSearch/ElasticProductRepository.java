package kazantseva.project.OnlineStore.repository.elasticSearch;

import kazantseva.project.OnlineStore.model.elasticSearch.entity.ElasticProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticProductRepository extends ElasticsearchRepository<ElasticProduct, String>,
        CustomElasticRepository {
}
