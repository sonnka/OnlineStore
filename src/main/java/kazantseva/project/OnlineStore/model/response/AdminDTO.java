package kazantseva.project.OnlineStore.model.response;

import lombok.Builder;

@Builder
public record AdminDTO(
        Long id,
        String name,
        String surname,
        String email,
        String avatar,
        String grantedAdminBy,
        String grantedDate
) {
}
