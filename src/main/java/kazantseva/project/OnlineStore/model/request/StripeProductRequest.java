package kazantseva.project.OnlineStore.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StripeProductRequest {

    @NotNull
    @NotBlank
    private String name;

    private String description;

    private String image;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @NotBlank
    private String currency;

    @NotNull
    @NotBlank
    private String recurring;
}
