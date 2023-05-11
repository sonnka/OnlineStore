package kazantseva.project.OnlineStore.product.model.entity;

import jakarta.persistence.Column;

import java.io.Serializable;

//@Embeddable
public class OrderProductId implements Serializable {

    @Column(name = "order_id")
    private Long order;

    @Column(name = "product_id")
    private Long product;
}
