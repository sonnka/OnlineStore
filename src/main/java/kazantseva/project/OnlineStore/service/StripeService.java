package kazantseva.project.OnlineStore.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import kazantseva.project.OnlineStore.exceptions.CustomStripeException;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.request.ChargeRequest;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.StripeProductRequest;
import kazantseva.project.OnlineStore.model.response.SubscriptionDTO;
import kazantseva.project.OnlineStore.model.response.SubscriptionResponse;

import java.util.List;

public interface StripeService {

    void webhook(Event event) throws StripeException, CustomStripeException;

    Charge charge(String email, ChargeRequest chargeRequest, String id)
            throws StripeException, CustomerException;

    void createSubscription(String email, String productId)
            throws StripeException, CustomerException;

    String updateSubscription(String email, String subscriptionId)
            throws StripeException, CustomerException;

    void cancelSubscription(String email, String subscriptionId)
            throws StripeException, CustomerException;

    com.stripe.model.Customer getCustomer(String email, String customerId)
            throws CustomerException, StripeException;

    String createCustomer(CreateCustomer customer)
            throws StripeException;

    void updateCustomer(Customer updatedCustomer)
            throws StripeException;

    void deleteCustomer(String customerId)
            throws StripeException;

    List<SubscriptionDTO> getArchiveProducts(Integer limit)
            throws StripeException, CustomerException;

    List<SubscriptionDTO> getActiveProducts(String email, Integer limit)
            throws StripeException, CustomerException;

    SubscriptionResponse getProduct(String productId)
            throws StripeException, CustomerException;

    SubscriptionResponse createProduct(String email, StripeProductRequest productRequest)
            throws StripeException, CustomStripeException, CustomerException;

    SubscriptionResponse updateProduct(String email, String productId, StripeProductRequest productRequest)
            throws StripeException, CustomStripeException, CustomerException;

    void archiveProduct(String email, String productId)
            throws StripeException, CustomStripeException, CustomerException;

    void deleteProduct(String email, String productId)
            throws StripeException, CustomStripeException, CustomerException;
}
