package kazantseva.project.OnlineStore.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kazantseva.project.OnlineStore.model.entity.enums.MyCurrency;
import lombok.Data;

@Data
public class ChargeRequest {

    private String description;

    @NotBlank
    private int amount;

    @NotNull
    @NotBlank
    private MyCurrency currency;

    private String stripeEmail;

    private String stripeToken;
}