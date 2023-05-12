package kazantseva.project.OnlineStore.repository;

import kazantseva.project.OnlineStore.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);
}
