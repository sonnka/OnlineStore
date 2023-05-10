package kazantseva.project.OnlineStore.product.model.response;

import kazantseva.project.OnlineStore.product.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor
public class ProductDTO {

    Product product;

    int count;
}
