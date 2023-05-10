package kazantseva.project.OnlineStore.customer.model.entity;

import jakarta.persistence.*;
import kazantseva.project.OnlineStore.order.model.entity.Order;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surname;

    private String email;

    private String password;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH},
            mappedBy = "customer")
    private List<Order> orders;
}
