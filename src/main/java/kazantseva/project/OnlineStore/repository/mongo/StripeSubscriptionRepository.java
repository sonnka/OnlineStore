package kazantseva.project.OnlineStore.repository.mongo;

import kazantseva.project.OnlineStore.model.mongo.entity.StripeProduct;
import kazantseva.project.OnlineStore.model.mongo.entity.StripeSubscription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StripeSubscriptionRepository extends MongoRepository<StripeSubscription, String> {
    Optional<StripeSubscription> findFirstByCustomerIdAndProduct(String customerId, StripeProduct product);

    List<StripeSubscription> findAllByProduct(StripeProduct product);

    void deleteAllByProduct(StripeProduct product);
}
