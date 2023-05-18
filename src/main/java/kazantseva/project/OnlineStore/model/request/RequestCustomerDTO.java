package kazantseva.project.OnlineStore.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCustomerDTO {

    private Long id;

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
