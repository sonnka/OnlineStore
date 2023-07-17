package kazantseva.project.OnlineStore.controller.rest;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.validation.Valid;
import kazantseva.project.OnlineStore.exceptions.CustomStripeException;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.model.request.ChargeRequest;
import kazantseva.project.OnlineStore.model.request.StripeProductRequest;
import kazantseva.project.OnlineStore.model.response.SubscriptionDTO;
import kazantseva.project.OnlineStore.service.impl.StripeService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class StripeController {

    private StripeService stripeService;

    @PostMapping("/charge")
    public Charge charge(Authentication auth,
                         @RequestBody @Valid ChargeRequest chargeRequest)
            throws StripeException, CustomerException {
        return stripeService.charge(auth.getName(), chargeRequest);
    }

    @GetMapping("/stripe/all/products")
    public String getProducts(Authentication auth, Integer limit) throws StripeException, CustomerException {
        return stripeService.getProducts(auth.getName(), limit);
    }

    @GetMapping("/stripe/products")
    public String getActiveProducts(Authentication auth, Integer limit) throws StripeException, CustomerException {
        return stripeService.getActiveProducts(auth.getName(), limit);
    }

    @GetMapping("/stripe/products/{product-id}")
    public SubscriptionDTO getProduct(Authentication auth,
                                      @PathVariable("product-id") String productId)
            throws StripeException, CustomerException {
        return stripeService.getProduct(auth.getName(), productId);
    }

    @PostMapping("/stripe/products")
    public SubscriptionDTO createProduct(Authentication auth,
                                         @RequestBody @Valid StripeProductRequest productRequest)
            throws StripeException, CustomStripeException, CustomerException {
        return stripeService.createProduct(auth.getName(), productRequest);
    }

    @PatchMapping("/stripe/products/{product-id}")
    public SubscriptionDTO updateProduct(Authentication auth,
                                         @PathVariable("product-id") String productId,
                                         @RequestBody @Valid StripeProductRequest productRequest)
            throws StripeException, CustomStripeException, CustomerException {
        return stripeService.updateProduct(auth.getName(), productId, productRequest);
    }

    @PatchMapping("/stripe/products/{product-id}/archive")
    public void archiveProduct(Authentication auth, @PathVariable("product-id") String productId)
            throws StripeException, CustomStripeException, CustomerException {
        stripeService.archiveProduct(auth.getName(), productId);
    }

    @DeleteMapping("/stripe/products/{product-id}")
    public void deleteProduct(Authentication auth, @PathVariable("product-id") String productId)
            throws StripeException, CustomStripeException, CustomerException {
        stripeService.deleteProduct(auth.getName(), productId);
    }
}


