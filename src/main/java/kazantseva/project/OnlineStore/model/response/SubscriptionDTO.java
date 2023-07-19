package kazantseva.project.OnlineStore.model.response;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SubscriptionDTO {

    private String id;

    private String image;

    private String name;

    private String description;

    private boolean active;

    private BigDecimal price;

    private String currency;

    private String recurring;
}
