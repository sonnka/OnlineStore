package kazantseva.project.OnlineStore.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ListOrders {

    private int totalAmount;

    private int amount;

    private List<ShortOrderDTO> orders;
}
