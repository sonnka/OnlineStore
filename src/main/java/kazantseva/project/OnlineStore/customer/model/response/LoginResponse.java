package kazantseva.project.OnlineStore.customer.model.response;

import lombok.Builder;

@Builder
public record LoginResponse (
        long id,
        String email
){
}
