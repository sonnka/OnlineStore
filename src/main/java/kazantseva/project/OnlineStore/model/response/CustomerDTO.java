package kazantseva.project.OnlineStore.model.response;

import lombok.Builder;

@Builder
public record CustomerDTO(
        Long id,
        String name,
        String surname,
        String avatar,
        String email
) {
}
