package kazantseva.project.OnlineStore.repository.mongo;

import kazantseva.project.OnlineStore.model.mongo.entity.StripeProduct;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StripeProductRepository extends MongoRepository<StripeProduct, String> {

    Optional<StripeProduct> findByProductId(String productId);

    List<StripeProduct> findAllByActive(boolean active);
}
