package kazantseva.project.OnlineStore.controller.rest;

import com.stripe.exception.SignatureVerificationException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StripeController {

    @Autowired
    private StripeService stripeService;

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            String subscriptionId = event.getDataObjectDeserializer().getObject().get().toJson();
            System.out.println(subscriptionId);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/charge/{id}")
    public Charge charge(Authentication auth,
                         @RequestBody @Valid ChargeRequest chargeRequest,
                         @PathVariable("id") String id)
            throws StripeException, CustomerException {
        return stripeService.charge(auth.getName(), chargeRequest, id);
    }

    @PostMapping("/stripe/customers/{customer-id}/subscription/{product-id}")
    public String createSubscription(Authentication auth,
                                     @PathVariable("customer-id") String customerId,
                                     @PathVariable("product-id") String productId)
            throws StripeException, CustomerException {
        return stripeService.createSubscription(auth.getName(), customerId, productId);
    }

    @PatchMapping("/stripe/customers/{customer-id}/subscription/{subscription-id}")
    public String updateSubscription(Authentication auth,
                                     @PathVariable("customer-id") String customerId,
                                     @PathVariable("subscription-id") String subscriptionId)
            throws StripeException, CustomerException {
        return stripeService.updateSubscription(auth.getName(), customerId, subscriptionId);
    }

    @DeleteMapping("/stripe/customers/{customer-id}/subscription/{subscription-id}")
    public void deleteSubscription(Authentication auth,
                                   @PathVariable("customer-id") String customerId,
                                   @PathVariable("subscription-id") String subscriptionId)
            throws StripeException, CustomerException {
        stripeService.cancelSubscription(auth.getName(), customerId, subscriptionId);
    }

//    @GetMapping("/stripe/customers/{customer-id}")
//    public String getCustomer(Authentication auth,
//                              @PathVariable("customer-id") String customerId)
//            throws StripeException, CustomerException {
//        return stripeService.getCustomer(auth.getName(), customerId);
//    }

    @GetMapping("/stripe/all/products")
    public List<SubscriptionDTO> getProducts(Authentication auth, Integer limit)
            throws StripeException, CustomerException {
        return stripeService.getProducts(auth.getName(), limit);
    }

    @GetMapping("/stripe/products")
    public List<SubscriptionDTO> getActiveProducts(Authentication auth, Integer limit)
            throws StripeException, CustomerException {
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


