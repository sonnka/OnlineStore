package kazantseva.project.OnlineStore.model.response;

import kazantseva.project.OnlineStore.model.mongo.response.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    Long id;

    String date;

    String status;

    String type;

    List<ProductDTO> products;

    String deliveryAddress;

    String description;

    String price;
}
