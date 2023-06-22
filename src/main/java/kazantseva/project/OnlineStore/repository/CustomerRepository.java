package kazantseva.project.OnlineStore.repository;

import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.entity.CustomerRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<Customer> findByEmailIgnoreCase(String email);

    @Query(value = "SELECT * FROM customers" +
            " WHERE customer_id = ( SELECT customer_id FROM verification_tokens " +
            "WHERE token = :token )",
            nativeQuery = true)
    Optional<Customer> findByVerificationToken(@Param("token") String token);

    Page<Customer> findByRole(CustomerRole role, Pageable pageable);
}
