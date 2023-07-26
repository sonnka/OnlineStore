package kazantseva.project.OnlineStore.repository.mongo;

import kazantseva.project.OnlineStore.model.mongo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    Page<Product> searchAllByNameContainsIgnoreCase(Pageable pageable, String keyword);
}
