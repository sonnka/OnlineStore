package kazantseva.project.OnlineStore.model.mongo.request;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProduct {

    private String name;

    private String image;

    private String category;

    private String description;

    private BigDecimal price;
}
