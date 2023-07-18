package kazantseva.project.OnlineStore.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import kazantseva.project.OnlineStore.exceptions.CustomStripeException;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.model.request.ChargeRequest;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.StripeProductRequest;
import kazantseva.project.OnlineStore.model.response.SubscriptionDTO;

import java.util.List;

public interface StripeService {

    String createSubscription(String email, String customerId, String productId)
            throws StripeException, CustomerException;

    Charge charge(String email, ChargeRequest chargeRequest)
            throws StripeException, CustomerException;

    String getCustomer(String email, String customerId)
            throws CustomerException, StripeException;

    String createCustomer(String email, CreateCustomer customer)
            throws StripeException, CustomerException;

    String updateCustomer(String email, String customerId)
            throws StripeException, CustomerException;

    void deleteCustomer(String email, String customerId)
            throws StripeException, CustomerException;

    List<SubscriptionDTO> getProducts(String email, Integer limit)
            throws StripeException, CustomerException;

    List<SubscriptionDTO> getActiveProducts(String email, Integer limit)
            throws StripeException, CustomerException;

    SubscriptionDTO getProduct(String email, String productId)
            throws StripeException, CustomerException;

    SubscriptionDTO createProduct(String email, StripeProductRequest productRequest)
            throws StripeException, CustomStripeException, CustomerException;

    SubscriptionDTO updateProduct(String email, String productId, StripeProductRequest productRequest)
            throws StripeException, CustomStripeException, CustomerException;

    void archiveProduct(String email, String productId)
            throws StripeException, CustomStripeException, CustomerException;

    void deleteProduct(String email, String productId)
            throws StripeException, CustomStripeException, CustomerException;
}
