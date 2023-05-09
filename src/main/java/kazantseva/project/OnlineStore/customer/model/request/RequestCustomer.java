package kazantseva.project.OnlineStore.customer.model.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RequestCustomer {

    @Size(min = 2, max = 24)
    private String name;

    @Size(min = 2, max = 24)
    private String surname;
}
