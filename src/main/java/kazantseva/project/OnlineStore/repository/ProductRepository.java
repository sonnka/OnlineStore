package kazantseva.project.OnlineStore.repository;

import kazantseva.project.OnlineStore.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);

    @Query(value = "SELECT product_id FROM products" +
            " WHERE product_id NOT IN  ( SELECT product_id FROM order_product " +
            "WHERE order_id = :orderId ) GROUP BY product_id",
            nativeQuery = true)
    List<Long> findByOrderId(@Param("orderId") Long orderId);
}
