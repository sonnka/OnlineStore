package kazantseva.project.OnlineStore.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RequestCustomer {

    @NotNull
    @NotBlank
    @Size(min = 2, max = 24)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 24)
    private String surname;

    private String avatar;
}
