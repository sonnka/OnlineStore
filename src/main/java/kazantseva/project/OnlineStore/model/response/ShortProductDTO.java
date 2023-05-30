package kazantseva.project.OnlineStore.model.response;

import kazantseva.project.OnlineStore.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortProductDTO {

    private Long id;

    private String name;

    private String price;

    public ShortProductDTO(Product product) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        var price = df.format(product.getPrice());

        this.id = product.getId();
        this.name = product.getName();
        this.price = price;
    }
}
