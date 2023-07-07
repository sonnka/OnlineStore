package kazantseva.project.OnlineStore.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kazantseva.project.OnlineStore.model.entity.enums.Currency;
import lombok.Data;

@Data
public class ChargeRequest {

    private String description;

    @NotBlank
    private int amount;

    @NotNull
    @NotBlank
    private Currency currency;

    private String stripeEmail;

    private String stripeToken;
}