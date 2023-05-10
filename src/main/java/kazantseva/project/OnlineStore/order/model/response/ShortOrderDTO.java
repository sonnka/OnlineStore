package kazantseva.project.OnlineStore.order.model.response;

import kazantseva.project.OnlineStore.order.model.entity.Order;
import lombok.Builder;

@Builder
public record ShortOrderDTO (
        long id,
        String date,
        String status,
        int price
){
    public ShortOrderDTO(Order order){
        this(order.getId(), String.valueOf(order.getDate()),
                String.valueOf(order.getStatus()),order.getPrice());
    }
}
