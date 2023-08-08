package kazantseva.project.OnlineStore.model.elasticSearch.entity;

import kazantseva.project.OnlineStore.model.entity.enums.Rating;
import kazantseva.project.OnlineStore.model.mongo.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(indexName = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElasticProduct {

    @Id
    private String id;

    @Field(type = FieldType.Keyword, name = "name")
    private String name;

    @Field(type = FieldType.Text, name = "category")
    private String category;

    @Field(type = FieldType.Date, name = "manufacturingDate")
    private LocalDate manufacturingDate;

    @Field(type = FieldType.Date, name = "expiryDate")
    private LocalDate expiryDate;

    @Field(type = FieldType.Boolean, name = "available")
    private boolean available;

    @Field(type = FieldType.Integer, name = "calories")
    private int calories;

    @Field(type = FieldType.Text, name = "rating")
    private Rating rating;

    @Field(type = FieldType.Double, name = "price")
    private BigDecimal price;

    public ElasticProduct(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.category = product.getCategory();
        this.manufacturingDate = product.getManufacturingDate();
        this.expiryDate = product.getExpiryDate();
        this.available = product.isAvailable();
        this.calories = product.getCalories();
        this.rating = product.getRating();
        this.price = product.getPrice();
    }
}
