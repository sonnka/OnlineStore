package kazantseva.project.OnlineStore.controller.rest;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.validation.Valid;
import kazantseva.project.OnlineStore.model.entity.enums.Currency;
import kazantseva.project.OnlineStore.model.request.ChargeRequest;
import kazantseva.project.OnlineStore.model.request.StripeProductRequest;
import kazantseva.project.OnlineStore.service.impl.StripeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class StripeController {

    private StripeService stripeService;

    @PostMapping("/charge")
    public Charge charge(@RequestBody @Valid ChargeRequest chargeRequest) throws StripeException {

        chargeRequest.setDescription("Example charge");
        chargeRequest.setCurrency(Currency.EUR);

        return stripeService.charge(chargeRequest);
    }

    @PostMapping("/stripe/products")
    public String createProduct(@RequestBody @Valid StripeProductRequest productRequest) throws StripeException {
        return stripeService.createProduct(productRequest);
    }

    @PatchMapping("/stripe/products/{product-id}")
    public String updateProduct(@PathVariable("product-id") String productId,
                                @RequestBody @Valid StripeProductRequest productRequest)
            throws StripeException {
        return stripeService.updateProduct(productId, productRequest);
    }

    @PatchMapping("/stripe/products/{product-id}/archive")
    public void archiveProduct(@PathVariable("product-id") String productId) throws StripeException {
        stripeService.archiveProduct(productId);
    }

    @DeleteMapping("/stripe/products/{product-id}")
    public void deleteProduct(@PathVariable("product-id") String productId) throws StripeException {
        stripeService.deleteProduct(productId);
    }
}


