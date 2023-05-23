package kazantseva.project.OnlineStore.model.response;

import kazantseva.project.OnlineStore.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortProductDTO {

    private Long id;

    private String name;

    private BigDecimal price;

    public ShortProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
    }
}
