package kazantseva.project.OnlineStore.product.repository;

import kazantseva.project.OnlineStore.product.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);
}
