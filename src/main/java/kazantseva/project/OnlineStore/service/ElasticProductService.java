package kazantseva.project.OnlineStore.service;

import kazantseva.project.OnlineStore.model.elasticSearch.ElasticProduct;

import java.util.List;

public interface ElasticProductService {
    List<ElasticProduct> getAllProducts();

    ElasticProduct insertProduct(ElasticProduct product);
}
