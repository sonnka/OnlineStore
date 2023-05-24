package kazantseva.project.OnlineStore.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    Long id;

    String date;

    String status;

    List<ProductDTO> products;

    String deliveryAddress;

    String description;

    BigDecimal price;
}
