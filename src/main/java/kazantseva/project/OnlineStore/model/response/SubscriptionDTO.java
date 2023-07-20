package kazantseva.project.OnlineStore.model.response;

import lombok.*;

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

    private String price;

    private String recurring;
}
