package kazantseva.project.OnlineStore.model.response;

import lombok.Builder;

@Builder
public record FullCustomerDTO(
        Long id,
        String name,
        String surname,
        String email,
        String avatar,
        int amountOfBasketElem,
        int totalAmountOfOrders,
        int amountOfUnpaidOrders,
        int amountOfPaidOrders,
        int amountOfAddedAdmins

) {
}
