package kazantseva.project.OnlineStore.product.model.response;

import kazantseva.project.OnlineStore.product.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    private String name;

    private double price;

    public ProductDTO(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
    }
}
