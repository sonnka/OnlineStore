package kazantseva.project.OnlineStore.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kazantseva.project.OnlineStore.model.entity.enums.CustomerRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(name = "stripe_id")
    private String stripeId;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "avatar")
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private CustomerRole role;

    @Column(name = "basket_id")
    private Long basket;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "granted_admin_by")
    private String grantedAdminBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "granted_date")
    private LocalDateTime date;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            mappedBy = "customer")
    @Column(name = "orders")
    private List<Order> orders;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "customer",
            orphanRemoval = true)
    @JsonIgnore
    @Column(name = "payments")
    private List<PaymentInfo> payments = new ArrayList<>();

    public Customer() {
        super();
        this.enabled = false;
    }
}
