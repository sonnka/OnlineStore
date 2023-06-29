package kazantseva.project.OnlineStore.model.mongo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestProduct(

        @NotNull
        @NotBlank
        String id,
        @NotNull
        @NotBlank
        String name,
        @NotBlank
        int count
) {
}
