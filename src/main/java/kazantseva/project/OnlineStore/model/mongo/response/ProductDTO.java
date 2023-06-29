package kazantseva.project.OnlineStore.model.mongo.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private String id;

    private String name;

    private String price;

    private int count;

//    public ProductDTO(OrderProduct product) {
//        DecimalFormat df = new DecimalFormat("#,###.00");
//        var price = df.format(product.getProduct().getPrice());
//
//        this.id = product.getProduct().getId();
//        this.name = product.getProduct().getName();
//        this.price = price;
//        this.count = product.getAmount();
//    }
}
