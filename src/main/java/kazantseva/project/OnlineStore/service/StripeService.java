package kazantseva.project.OnlineStore.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import kazantseva.project.OnlineStore.exceptions.CustomStripeException;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.request.ChargeRequest;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.StripeProductRequest;
import kazantseva.project.OnlineStore.model.response.SubscriptionDTO;

import java.util.List;

public interface StripeService {

    Charge charge(String email, ChargeRequest chargeRequest, String id)
            throws StripeException, CustomerException;

    String createSubscription(String email, String customerId, String productId)
            throws StripeException, CustomerException;

    String updateSubscription(String email, String customerId, String subscriptionId)
            throws StripeException, CustomerException;

    void cancelSubscription(String email, String customerId, String subscriptionId)
            throws StripeException, CustomerException;

    com.stripe.model.Customer getCustomer(String email, String customerId)
            throws CustomerException, StripeException;

    String createCustomer(CreateCustomer customer)
            throws StripeException;

    void updateCustomer(Customer updatedCustomer)
            throws StripeException;

    void deleteCustomer(String customerId)
            throws StripeException;

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
