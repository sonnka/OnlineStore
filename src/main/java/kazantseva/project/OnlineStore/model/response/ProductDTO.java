package kazantseva.project.OnlineStore.model.response;

import kazantseva.project.OnlineStore.model.entity.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter@Setter
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    private String name;

    private BigDecimal price;

    private int count;

    public ProductDTO(OrderProduct product){
        this.id = product.getProduct().getId();
        this.name = product.getProduct().getName();
        this.price = product.getProduct().getPrice();
        this.count = product.getAmount();
    }
}
