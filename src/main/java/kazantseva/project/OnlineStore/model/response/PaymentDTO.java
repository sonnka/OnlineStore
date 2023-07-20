package kazantseva.project.OnlineStore.model.response;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private String id;

    private String price;

    private String description;

    private String date;

    private String card;

    private String paymentStatus;

    private String errors;
}
