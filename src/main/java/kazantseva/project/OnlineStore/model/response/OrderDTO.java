package kazantseva.project.OnlineStore.model.response;

import kazantseva.project.OnlineStore.model.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    Long id;

    String date;

    String status;

    List<ProductDTO> products;

    String deliveryAddress;

    String description;

    BigDecimal price;

    public OrderDTO(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = order.getDate().format(formatter);

        this.id = order.getId();
        this.date = formatDateTime;
        this.status = String.valueOf(order.getStatus());
        this.products = order.getProducts().stream().map(ProductDTO::new).toList();
        this.deliveryAddress = order.getDeliveryAddress();
        this.description = order.getDescription();
        this.price = order.getPrice();
    }
}
