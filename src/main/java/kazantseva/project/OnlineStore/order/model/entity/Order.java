package kazantseva.project.OnlineStore.order.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kazantseva.project.OnlineStore.customer.model.entity.Customer;
import kazantseva.project.OnlineStore.product.model.entity.OrderProduct;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "order")
    @JsonIgnore
    private List<OrderProduct> products;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    private String description;

    private double price;
}
