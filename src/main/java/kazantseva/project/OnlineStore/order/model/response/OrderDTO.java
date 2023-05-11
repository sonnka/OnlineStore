package kazantseva.project.OnlineStore.order.model.response;

import kazantseva.project.OnlineStore.order.model.entity.Order;
import kazantseva.project.OnlineStore.product.model.response.ProductDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderDTO (
        long id,
        String date,
        String status,
        List<ProductDTO> products,
        String deliveryAddress,
        String description,
        double price
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
