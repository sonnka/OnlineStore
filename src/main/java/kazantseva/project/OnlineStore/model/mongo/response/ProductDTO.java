package kazantseva.project.OnlineStore.model.mongo.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private String id;

    private String name;

    private String price;

    private int count;
}
