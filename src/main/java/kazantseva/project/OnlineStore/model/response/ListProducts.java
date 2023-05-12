package kazantseva.project.OnlineStore.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ListProducts {

    private int totalAmount;

    private int amount;

    private List<ShortProductDTO> products;
}
