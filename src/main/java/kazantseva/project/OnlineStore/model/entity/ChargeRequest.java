package kazantseva.project.OnlineStore.model.entity;

import lombok.Data;

@Data
public class ChargeRequest {

    private String description;
    private int amount;
    private Currency currency;
    private String stripeEmail;
    private String stripeToken;

    public enum Currency {
        EUR, USD
    }
}