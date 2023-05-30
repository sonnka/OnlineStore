package kazantseva.project.OnlineStore.model.response;

import kazantseva.project.OnlineStore.model.entity.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long id;

    private String name;

    private String price;

    private int count;

    public ProductDTO(OrderProduct product) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        var price = df.format(product.getProduct().getPrice());

        this.id = product.getProduct().getId();
        this.name = product.getProduct().getName();
        this.price = price;
        this.count = product.getAmount();
    }
}
