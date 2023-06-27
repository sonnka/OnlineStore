package kazantseva.project.OnlineStore.model.mongo;

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

    private String id;

    private String name;

    private String category;

    private String description;

    private String image;

    private String price;

    public ShortProductDTO(Product product) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        var price = df.format(product.getPrice());

        this.id = product.getId();
        this.name = product.getName();
        this.category = product.getCategory();
        this.description = product.getDescription();
        this.image = product.getImage();
        this.price = price;
    }
}
