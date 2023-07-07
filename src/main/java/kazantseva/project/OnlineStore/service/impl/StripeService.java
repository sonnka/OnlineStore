package kazantseva.project.OnlineStore.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.model.Product;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.PriceUpdateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.ProductUpdateParams;
import jakarta.annotation.PostConstruct;
import kazantseva.project.OnlineStore.model.request.ChargeRequest;
import kazantseva.project.OnlineStore.model.request.StripeProductRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StripeService {

    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public Charge charge(ChargeRequest chargeRequest) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", chargeRequest.getAmount());
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("description", chargeRequest.getDescription());
        chargeParams.put("source", chargeRequest.getStripeToken());
        return Charge.create(chargeParams);
    }

    public String createProduct(StripeProductRequest productRequest) throws StripeException {
        ProductCreateParams params = ProductCreateParams.builder()
                .setName(productRequest.getName())
                .setDescription(productRequest.getDescription())
                .build();

        Product product = Product.create(params);

        createPrice(product.getId(),
                productRequest.getPrice().multiply(BigDecimal.valueOf(100.0)).longValue(),
                productRequest.getCurrency(),
                productRequest.getRecurring());

        return product.toJson();
    }

    public String updateProduct(String productId, StripeProductRequest productRequest) throws StripeException {
        Product product = Product.retrieve(productId);

        Map<String, Object> params1 = new HashMap<>();
        params1.put("product", productId);
        PriceCollection priceCollection = Price.list(params1);
        List<Price> prices = priceCollection.getData();
        if (prices.isEmpty())
            return null;
        Price price = prices.get(0);

        Long newPrice = productRequest.getPrice().multiply(BigDecimal.valueOf(100.0)).longValue();
        String newCurrency = productRequest.getCurrency().toLowerCase();

        ProductUpdateParams params =
                ProductUpdateParams.builder()
                        .setName(productRequest.getName())
                        .setDescription(productRequest.getDescription())
                        .build();

        if (!price.getUnitAmount().equals(newPrice) || !price.getCurrency().equals(newCurrency) ||
                !price.getRecurring().getInterval().equals(productRequest.getRecurring().toLowerCase())) {

            PriceUpdateParams priceParams = PriceUpdateParams.builder().setActive(false).build();
            price.update(priceParams);

            createPrice(productId,
                    newPrice,
                    newCurrency,
                    productRequest.getRecurring());

        }

        return product.update(params).toJson();
    }

    public void archiveProduct(String productId) throws StripeException {
        Product product = Product.retrieve(productId);

        Map<String, Object> params1 = new HashMap<>();
        params1.put("product", productId);
        PriceCollection priceCollection = Price.list(params1);
        List<Price> prices = priceCollection.getData();
        if (prices.isEmpty())
            return;
        Price price = prices.get(0);

        ProductUpdateParams params;
        PriceUpdateParams priceParams;

        if (product.getActive()) {
            params = ProductUpdateParams.builder().setActive(false).build();
            priceParams = PriceUpdateParams.builder().setActive(false).build();
        } else {
            params = ProductUpdateParams.builder().setActive(true).build();
            priceParams = PriceUpdateParams.builder().setActive(true).build();
        }

        price.update(priceParams);

        product.update(params);
    }

    public void deleteProduct(String productId) throws StripeException {
        Product product = Product.retrieve(productId);

        Map<String, Object> params1 = new HashMap<>();
        params1.put("product", productId);
        PriceCollection priceCollection = Price.list(params1);
        List<Price> prices = priceCollection.getData();
        if (prices.isEmpty())
            return;
        Price price = prices.get(0);

        ProductUpdateParams params;
        PriceUpdateParams priceParams;

        params = ProductUpdateParams.builder().setActive(false).build();
        priceParams = PriceUpdateParams.builder().setActive(false).build();

        price.update(priceParams);

        product.update(params);
    }

    private void createPrice(String productId, Long price, String currency, String recurring) throws StripeException {
        PriceCreateParams newPriceParams =
                PriceCreateParams.builder()
                        .setProduct(productId)
                        .setUnitAmount(price)
                        .setCurrency(currency)
                        .setRecurring(
                                PriceCreateParams.Recurring.builder()
                                        .setInterval(getRecurring(recurring))
                                        .build()
                        )
                        .build();

        Price.create(newPriceParams);
    }

    private PriceCreateParams.Recurring.Interval getRecurring(String recurring) {
        return switch (recurring.toLowerCase()) {
            case "day" -> PriceCreateParams.Recurring.Interval.DAY;
            case "week" -> PriceCreateParams.Recurring.Interval.WEEK;
            case "year" -> PriceCreateParams.Recurring.Interval.YEAR;
            default -> PriceCreateParams.Recurring.Interval.MONTH;
        };
    }
}