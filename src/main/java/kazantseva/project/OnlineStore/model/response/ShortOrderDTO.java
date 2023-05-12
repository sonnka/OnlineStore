package kazantseva.project.OnlineStore.model.response;

import kazantseva.project.OnlineStore.model.entity.Order;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ShortOrderDTO (
        long id,
        String date,
        String status,
        BigDecimal price
){
    public ShortOrderDTO(Order order){
        this(order.getId(), String.valueOf(order.getDate()),
                String.valueOf(order.getStatus()),order.getPrice());
    }
}
