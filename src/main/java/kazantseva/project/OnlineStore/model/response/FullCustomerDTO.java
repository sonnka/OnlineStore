package kazantseva.project.OnlineStore.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FullCustomerDTO {

    Long id;

    String name;

    String surname;

    String email;

    int totalAmountOfOrders;

    int amountUnpaidOrders;

    int amountPaidOrders;
}
