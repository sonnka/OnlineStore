package kazantseva.project.OnlineStore.repository.elasticSearch;

import kazantseva.project.OnlineStore.model.elasticSearch.entity.ElasticProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Criteria;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CustomElasticRepository {

    Page<ElasticProduct> findAllProducts(Pageable pageable);

    Page<ElasticProduct> search(Pageable pageable, Criteria criteria);

    Criteria searchByName(String name);

    Criteria searchByRating(String rating);

    Criteria searchByMinPrice(BigDecimal from);

    Criteria searchByMaxPrice(BigDecimal to);

    Criteria searchByMinDate(LocalDate dateFrom);

    Criteria searchByMaxDate(LocalDate dateTo);
}
