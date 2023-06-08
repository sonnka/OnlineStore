package kazantseva.project.OnlineStore.model.response;

import lombok.Builder;

@Builder
public record FullCustomerDTO(
        Long id,
        String name,
        String surname,
        String email,
        int totalAmountOfOrders,
        int amountOfUnpaidOrders,
        int amountOfPaidOrders

) {
}
