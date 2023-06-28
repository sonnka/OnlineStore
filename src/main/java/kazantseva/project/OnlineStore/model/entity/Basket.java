package kazantseva.project.OnlineStore.model.entity;

import jakarta.persistence.*;
import kazantseva.project.OnlineStore.util.StringListConverter;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "baskets")
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "basket_id")
    private Long id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @Convert(converter = StringListConverter.class)
    @Column(name = "products", nullable = false)
    private List<String> products = new ArrayList<>();
}
