package kazantseva.project.OnlineStore.model.response;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SubscriptionDTO {

    String id;

    String image;

    String name;

    String description;

    boolean active;

    BigDecimal price;

    String currency;

    String recurring;
}
