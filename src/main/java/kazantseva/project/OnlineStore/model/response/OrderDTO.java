package kazantseva.project.OnlineStore.model.response;

import kazantseva.project.OnlineStore.model.entity.Order;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderDTO (
        long id,
        String date,
        String status,
        List<ProductDTO> products,
        String deliveryAddress,
        String description,
        BigDecimal price
){
    public OrderDTO(Order order){
        this(order.getId(),
                String.valueOf(order.getDate()),
                String.valueOf(order.getStatus()),
                order.getProducts().stream().map(ProductDTO::new).toList(),
                order.getDeliveryAddress(),
                order.getDescription() ,
                order.getPrice());
    }
}
