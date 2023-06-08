package kazantseva.project.OnlineStore.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "verification_tokens")
public class VerificationToken {

    private static final long EXPIRATION = 24L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "locale")
    private String locale;

    @OneToOne(targetEntity = Customer.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "customer_id")
    private Customer customer;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public VerificationToken(Customer customer, String locale) {
        this.customer = customer;
        this.expiryDate = LocalDateTime.now(ZoneOffset.UTC).plusHours(EXPIRATION);
        this.token = UUID.randomUUID().toString();
        this.locale = locale;
    }

}
