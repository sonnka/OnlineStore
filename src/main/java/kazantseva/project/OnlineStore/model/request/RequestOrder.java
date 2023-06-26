package kazantseva.project.OnlineStore.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kazantseva.project.OnlineStore.model.mongo.RequestProduct;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestOrder {

    @NotNull
    private String status;

    @NotNull
    private List<RequestProduct> products;

    @NotNull
    @Size(min = 5)
    private String deliveryAddress;

    @NotNull
    private String description;
}
