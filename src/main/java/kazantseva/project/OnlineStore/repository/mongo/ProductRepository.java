package kazantseva.project.OnlineStore.repository.mongo;

import kazantseva.project.OnlineStore.model.mongo.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    Product findByName(String name);

    List<Product> findByIdNotIn(List<String> ids);
}
