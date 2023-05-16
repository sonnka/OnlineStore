package kazantseva.project.OnlineStore.product.model.response;

import kazantseva.project.OnlineStore.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ListProducts {
    private int totalAmount;

    private int amount;

    private List<Product> products;
}
