package kazantseva.project.OnlineStore.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StripeProductRequest {

    @NotNull
    @NotBlank
    String name;

    String description;

    String image;

    @NotNull
    BigDecimal price;

    @NotNull
    @NotBlank
    String currency;

    @NotNull
    @NotBlank
    String recurring;
}
