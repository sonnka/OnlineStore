package kazantseva.project.OnlineStore.model.request;

import jakarta.validation.constraints.Size;
import kazantseva.project.OnlineStore.model.mongo.request.RequestProduct;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestOrder {

    private String status;

    private List<RequestProduct> products;

    @Size(min = 5)
    private String deliveryAddress;

    private String description;
}
