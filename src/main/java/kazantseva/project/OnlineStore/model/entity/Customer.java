package kazantseva.project.OnlineStore.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    @OneToOne(mappedBy = "customer")
    private Basket basket;

    public Customer() {
        super();
        this.enabled = false;
    }
}
