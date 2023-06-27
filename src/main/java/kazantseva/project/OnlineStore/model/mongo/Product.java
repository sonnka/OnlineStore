package kazantseva.project.OnlineStore.model.mongo;

import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kazantseva.project.OnlineStore.model.entity.Rating;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private String id;

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "category")
    private String category;

    @CsvBindByName(column = "description")
    private String description;

    private LocalDate manufacturingDate;

    private LocalDate expiryDate;

    private boolean available;

    private int calories;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    private String image;

    @CsvBindByName(column = "price")
    private String stringPrice;

    private BigDecimal price;
}
