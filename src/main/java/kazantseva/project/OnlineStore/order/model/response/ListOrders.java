package kazantseva.project.OnlineStore.order.model.response;

import kazantseva.project.OnlineStore.order.model.entity.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter
@Builder
public class ListOrders {

    private int amount;

    private List<ShortOrderDTO> orders;
}
