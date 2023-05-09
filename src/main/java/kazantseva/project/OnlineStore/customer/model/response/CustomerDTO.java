package kazantseva.project.OnlineStore.customer.model.response;

import lombok.Builder;

@Builder
public record CustomerDTO(
        long id,
        String name,
        String surname,
        String email

) {
}
