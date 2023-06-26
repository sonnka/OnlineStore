package kazantseva.project.OnlineStore.repository.mongo;

import kazantseva.project.OnlineStore.model.mongo.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {

    Product findByName(String name);


//    List<Product> findProductsNotInOrder(@Param("orderId") Long orderId);
}
