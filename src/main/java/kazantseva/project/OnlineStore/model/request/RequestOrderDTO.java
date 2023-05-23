package kazantseva.project.OnlineStore.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kazantseva.project.OnlineStore.model.response.ProductDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class RequestOrderDTO {

    private Long id;

    @NotNull
    private String status;

    @NotNull
    private List<ProductDTO> products;

    @NotNull
    @Size(min = 5)
    private String deliveryAddress;

    @NotNull
    private String description;

    @NotNull
    private BigDecimal price = BigDecimal.valueOf(0);
}
