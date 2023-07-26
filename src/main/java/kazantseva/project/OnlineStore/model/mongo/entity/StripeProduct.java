package kazantseva.project.OnlineStore.model.mongo.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeProduct {
    @Id
    private String id;

    @CsvBindByName(column = "product_id")
    private String productId;

    private String image;

    private String name;

    private String description;

    private boolean active;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;

    private String currency;

    private String recurring;

}
