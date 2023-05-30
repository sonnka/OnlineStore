package kazantseva.project.OnlineStore.model.response;

import kazantseva.project.OnlineStore.model.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ShortOrderDTO {
    private Long id;

    private String date;

    private String status;

    private String price;

    public ShortOrderDTO(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = order.getDate().format(formatter);

        DecimalFormat df = new DecimalFormat("#,###.00");
        var price = df.format(order.getPrice());

        this.id = order.getId();
        this.date = formatDateTime;
        this.status = String.valueOf(order.getStatus());
        this.price = price;
    }
}
