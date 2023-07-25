package kazantseva.project.OnlineStore.controller.rest;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import kazantseva.project.OnlineStore.exceptions.CustomStripeException;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.model.request.ChargeRequest;
import kazantseva.project.OnlineStore.model.request.StripeProductRequest;
import kazantseva.project.OnlineStore.model.response.SubscriptionDTO;
import kazantseva.project.OnlineStore.service.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StripeController {

    private final StripeService stripeService;

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String webhookSecret;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/webhook")
    public void handleStripeWebhook(@RequestBody String payload,
                                    @RequestHeader("Stripe-Signature") String sigHeader)
            throws StripeException, CustomStripeException {

        Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        stripeService.webhook(event);
    }

    @PostMapping("/charge/{id}")
    public Charge charge(Authentication auth,
                         @RequestBody @Valid ChargeRequest chargeRequest,
                         @PathVariable("id") String id)
            throws StripeException, CustomerException {
        return stripeService.charge(auth.getName(), chargeRequest, id);
    }

    @PostMapping("/stripe/subscription/{product-id}")
    public void createSubscription(Authentication auth,
                                   @PathVariable("product-id") String productId)
            throws StripeException, CustomerException {
        stripeService.createSubscription(auth.getName(), productId);
    }

//    @PatchMapping("/stripe/subscription/{subscription-id}")
//    public String updateSubscription(Authentication auth,
//                                     @PathVariable("subscription-id") String subscriptionId)
//            throws StripeException, CustomerException {
//        return stripeService.updateSubscription(auth.getName(), subscriptionId);
//    }

    @DeleteMapping("/stripe/subscription/{subscription-id}")
    public void deleteSubscription(Authentication auth,
                                   @PathVariable("subscription-id") String subscriptionId)
            throws StripeException, CustomerException {
        stripeService.cancelSubscription(auth.getName(), subscriptionId);
    }

    @GetMapping("/stripe/products")
    public List<SubscriptionDTO> getActiveProducts(Authentication auth, Integer limit)
            throws StripeException, CustomerException {
        return stripeService.getActiveProducts(auth.getName(), limit);
    }

    @GetMapping("/stripe/products/archive")
    public List<SubscriptionDTO> getArchiveProducts(Integer limit)
            throws StripeException, CustomerException {
        return stripeService.getArchiveProducts(limit);
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


