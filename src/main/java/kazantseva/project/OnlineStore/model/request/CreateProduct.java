package kazantseva.project.OnlineStore.model.request;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProduct {

    private String image;

    private String name;

    private BigDecimal price;
}
