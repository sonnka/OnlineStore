package kazantseva.project.OnlineStore.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import jakarta.annotation.PostConstruct;
import kazantseva.project.OnlineStore.exceptions.CustomStripeException;
import kazantseva.project.OnlineStore.exceptions.CustomStripeException.StripeExceptionProfile;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.CustomerException.CustomerExceptionProfile;
import kazantseva.project.OnlineStore.model.entity.enums.CustomerRole;
import kazantseva.project.OnlineStore.model.request.ChargeRequest;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.StripeProductRequest;
import kazantseva.project.OnlineStore.model.response.SubscriptionDTO;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.service.StripeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StripeServiceImpl implements StripeService {

    private final CustomerRepository customerRepository;
    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    public StripeServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public String createSubscription(String email, String customerId, String productId)
            throws CustomerException, StripeException {
        customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        Price price = getPrice(productId);

        if (price != null) {

            SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                    .setCustomer(customerId)
                    .addItem(SubscriptionCreateParams.Item.builder()
                            .setPrice(price.getId())
                            .build())
                    .build();

            return Subscription.create(params).toJson();
        }
        return null;
    }

    @Override
    public Charge charge(String email, ChargeRequest chargeRequest)
            throws StripeException, CustomerException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElse(null);

        if (customer == null) {
            throw new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND);
        }

        if (customer.getRole() == CustomerRole.ADMIN) {
            throw new CustomerException(CustomerExceptionProfile.NOT_BUYER);
        }

        Map<String, Object> chargeParams = new HashMap<>();

        chargeParams.put("amount", chargeRequest.getAmount());
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("description", chargeRequest.getDescription());
        chargeParams.put("source", chargeRequest.getStripeToken());

        return Charge.create(chargeParams);
    }

    @Override
    public String getCustomer(String email, String customerId)
            throws CustomerException, StripeException {
        customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerException.CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        return Customer.retrieve(customerId).toJson();
    }

    @Override
    public String createCustomer(String email, CreateCustomer customer)
            throws StripeException, CustomerException {
        customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerException.CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        PaymentMethodCreateParams paymentParams = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(PaymentMethodCreateParams.Token.builder()
                        .setToken("tok_visa")
                        .build())
                .build();

        PaymentMethod paymentMethod = PaymentMethod.create(paymentParams);

        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setName(customer.getName() + " " + customer.getSurname())
                .setEmail(customer.getEmail())
                .setPaymentMethod(paymentMethod.getId())
                .setInvoiceSettings(CustomerCreateParams.InvoiceSettings.builder()
                        .setDefaultPaymentMethod(paymentMethod.getId())
                        .build())
                .build();

        return Customer.create(customerParams).toJson();
    }

    @Override
    public String updateCustomer(String email, String customerId)
            throws StripeException, CustomerException {
        Customer customer =
                Customer.retrieve(customerId);

        var updatedCustomer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        Map<String, Object> params = new HashMap<>();
        params.put("name", updatedCustomer.getName() + " " + updatedCustomer.getSurname());

        return customer.update(params).toJson();
    }

    @Override
    public void deleteCustomer(String email, String customerId)
            throws StripeException, CustomerException {
        customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        Customer customer = Customer.retrieve(customerId);

        customer.delete();
    }

    @Override
    public List<SubscriptionDTO> getProducts(String email, Integer limit)
            throws StripeException, CustomerException {
        customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        Map<String, Object> params = new HashMap<>();
        params.put("limit", limit);

        List<SubscriptionDTO> subscriptions = new ArrayList<>();
        var productList = Product.list(params).getData();

        for (Product product : productList) {
            subscriptions.add(toSubscriptionDTO(product, getPrice(product.getId())));
        }

        return subscriptions;
    }

    @Override
    public List<SubscriptionDTO> getActiveProducts(String email, Integer limit)
            throws StripeException, CustomerException {
        customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        Map<String, Object> params = new HashMap<>();
        params.put("limit", limit);
        params.put("active", true);

        List<SubscriptionDTO> subscriptions = new ArrayList<>();
        var productList = Product.list(params).getData();

        for (Product product : productList) {
            subscriptions.add(toSubscriptionDTO(product, getPrice(product.getId())));
        }

        return subscriptions;
    }

    @Override
    public SubscriptionDTO getProduct(String email, String productId)
            throws StripeException, CustomerException {
        customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        Product product = Product.retrieve(productId);
        Price price = getPrice(productId);

        return toSubscriptionDTO(product, price);
    }

    @Override
    public SubscriptionDTO createProduct(String email, StripeProductRequest productRequest)
            throws StripeException, CustomStripeException, CustomerException {
        checkAdminByEmail(email);

        ProductCreateParams params = ProductCreateParams.builder()
                .setName(productRequest.getName())
                .setDescription(productRequest.getDescription())
                .addAllImage(List.of(productRequest.getImage()))
                .build();

        Product product = Product.create(params);

        createPrice(product.getId(),
                productRequest.getPrice().multiply(BigDecimal.valueOf(100.0)).longValue(),
                productRequest.getCurrency(),
                productRequest.getRecurring());

        return toSubscriptionDTO(product, getPrice(product.getId()));
    }

    @Override
    public SubscriptionDTO updateProduct(String email, String productId, StripeProductRequest productRequest)
            throws StripeException, CustomStripeException, CustomerException {
        checkAdminByEmail(email);

        Product product = Product.retrieve(productId);

        Price price = getPrice(productId);

        if (price == null) {
            throw new CustomStripeException(StripeExceptionProfile.PRICE_NULL);
        }

        Long newPrice = productRequest.getPrice().multiply(BigDecimal.valueOf(100.0)).longValue();
        String newCurrency = productRequest.getCurrency().toLowerCase();

        ProductUpdateParams params =
                ProductUpdateParams.builder()
                        .setName(productRequest.getName())
                        .setDescription(productRequest.getDescription())
                        .addAllImage(List.of(productRequest.getImage()))
                        .build();

        if (!price.getUnitAmount().equals(newPrice) || !price.getCurrency().equals(newCurrency) ||
                !price.getRecurring().getInterval().equals(productRequest.getRecurring().toLowerCase())) {

            PriceUpdateParams priceParams = PriceUpdateParams.builder().setActive(false).build();
            price.update(priceParams);

            price = createPrice(productId,
                    newPrice,
                    newCurrency,
                    productRequest.getRecurring());
        }
        Product updatedProduct = product.update(params);

        return toSubscriptionDTO(updatedProduct, price);
    }

    @Override
    public void archiveProduct(String email, String productId)
            throws StripeException, CustomStripeException, CustomerException {
        checkAdminByEmail(email);

        Product product = Product.retrieve(productId);

        Price price = getPrice(productId);

        if (price == null) {
            throw new CustomStripeException(StripeExceptionProfile.PRICE_NULL);
        }

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

    @Override
    public void deleteProduct(String email, String productId)
            throws StripeException, CustomStripeException, CustomerException {
        checkAdminByEmail(email);

        Product product = Product.retrieve(productId);

        Price price = getPrice(productId);

        if (price == null) {
            throw new CustomStripeException(StripeExceptionProfile.PRICE_NULL);
        }

        ProductUpdateParams params;
        PriceUpdateParams priceParams;

        params = ProductUpdateParams.builder().setActive(false).build();
        priceParams = PriceUpdateParams.builder().setActive(false).build();

        price.update(priceParams);

        product.update(params);
    }

    private Price createPrice(String productId, Long price, String currency, String recurring)
            throws StripeException {
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

        return Price.create(newPriceParams);
    }

    private PriceCreateParams.Recurring.Interval getRecurring(String recurring) {
        return switch (recurring.toLowerCase()) {
            case "day" -> PriceCreateParams.Recurring.Interval.DAY;
            case "week" -> PriceCreateParams.Recurring.Interval.WEEK;
            case "year" -> PriceCreateParams.Recurring.Interval.YEAR;
            default -> PriceCreateParams.Recurring.Interval.MONTH;
        };
    }

    private Price getPrice(String productId) throws StripeException {
        Map<String, Object> params1 = new HashMap<>();
        params1.put("product", productId);

        PriceCollection priceCollection = Price.list(params1);

        List<Price> prices = priceCollection.getData();

        if (prices.isEmpty())
            return null;

        return prices.get(0);
    }

    private SubscriptionDTO toSubscriptionDTO(Product product, Price price) {
        String image = null;

        if (product.getImages() != null && product.getImages().size() > 0) {
            image = product.getImages().get(0);
        }

        return SubscriptionDTO.builder()
                .id(product.getId())
                .image(image)
                .name(product.getName())
                .description(product.getDescription())
                .active(product.getActive())
                .currency(price.getCurrency().toUpperCase())
                .price(price.getUnitAmountDecimal().divide(BigDecimal.valueOf(100)))
                .recurring(price.getRecurring().getInterval().toUpperCase())
                .build();
    }

    private void checkAdminByEmail(String email) throws CustomerException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerException.CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        if (!CustomerRole.ADMIN.equals(customer.getRole())) {
            throw new CustomerException(CustomerException.CustomerExceptionProfile.NOT_ADMIN);
        }
    }
}