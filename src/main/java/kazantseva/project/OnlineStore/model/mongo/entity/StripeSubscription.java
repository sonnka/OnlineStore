package kazantseva.project.OnlineStore.model.mongo.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeSubscription {

    @Id
    private String id;

    @CsvBindByName(column = "subscription_id")
    private String subscriptionId;

    @CsvBindByName(column = "customer_id")
    private String customerId;

    @DBRef
    private StripeProduct product;
}
