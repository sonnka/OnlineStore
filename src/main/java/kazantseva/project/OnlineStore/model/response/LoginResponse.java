package kazantseva.project.OnlineStore.model.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        Long id,
        String email
) {
}
