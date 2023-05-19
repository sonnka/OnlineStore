package kazantseva.project.OnlineStore.model.response;

import kazantseva.project.OnlineStore.model.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ShortOrderDTO {
    Long id;
    String date;
    String status;
    BigDecimal price;

    public ShortOrderDTO(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = order.getDate().format(formatter);
        this.id = order.getId();
        this.date = formatDateTime;
        this.status = String.valueOf(order.getStatus());
        this.price = order.getPrice();
    }
}
