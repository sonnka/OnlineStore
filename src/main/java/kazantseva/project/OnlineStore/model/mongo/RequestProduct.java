package kazantseva.project.OnlineStore.model.mongo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestProduct(
        @NotNull
        @NotBlank
        String name,
        @NotBlank
        int count
) {
}
