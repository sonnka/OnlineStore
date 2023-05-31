package kazantseva.project.OnlineStore.repository;

import kazantseva.project.OnlineStore.model.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByCustomerId(Long customerId);
}
