package kazantseva.project.OnlineStore.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter@Setter
public class CreateCustomer {
    @NotNull
    @Size(min = 2, max = 24)
    private String name;

    @NotNull
    @Size(min = 2, max = 24)
    private String surname;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 6)
    private String password;
}
