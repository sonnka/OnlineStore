package kazantseva.project.OnlineStore.order.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter
public class RequestOrder {

    @NotNull
    private String status;

    private List<String> products;

    @NotNull
    @Size(min = 5)
    private String deliveryAddress;

    @NotNull
    private String description;
}
