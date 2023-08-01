package kazantseva.project.OnlineStore.model.elasticSearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElasticProduct {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;
}
