package kazantseva.project.OnlineStore.model.response;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SubscriptionResponse {

    private String id;

    private String name;

    private String description;

    private String image;

    private String price;

    private String currency;

    private String recurring;
}
