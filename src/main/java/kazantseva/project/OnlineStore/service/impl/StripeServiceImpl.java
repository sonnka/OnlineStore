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
import kazantseva.project.OnlineStore.model.entity.PaymentInfo;
import kazantseva.project.OnlineStore.model.entity.enums.CustomerRole;
import kazantseva.project.OnlineStore.model.request.ChargeRequest;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.StripeProductRequest;
import kazantseva.project.OnlineStore.model.response.SubscriptionDTO;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.repository.PaymentRepository;
import kazantseva.project.OnlineStore.service.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class StripeServiceImpl implements StripeService {

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    public StripeServiceImpl(CustomerRepository customerRepository, PaymentRepository paymentRepository) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public Charge charge(String email, ChargeRequest chargeRequest, String id)
            throws CustomerException, StripeException {
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
        chargeParams.put("description", "Payment for order № " + id);
        chargeParams.put("source", chargeRequest.getStripeToken());

        Charge charge;

        try {
            charge = Charge.create(chargeParams);

            paymentRepository.save(PaymentInfo.builder()
                    .customer(customer)
                    .price(BigDecimal.valueOf(charge.getAmount()).divide(BigDecimal.valueOf(100)))
                    .currency(charge.getCurrency().toUpperCase())
                    .description(charge.getDescription())
                    .date(LocalDateTime.now(ZoneOffset.UTC))
                    .card("**** **** **** " + charge.getPaymentMethodDetails().getCard().getLast4())
                    .paymentStatus(charge.getStatus())
                    .errors(charge.getFailureMessage())
                    .build()
            );
        } catch (StripeException e) {
            var c = getCustomer(customer.getEmail(), customer.getStripeId());
            var number = PaymentMethod.retrieve(c.getInvoiceSettings().getDefaultPaymentMethod()).getCard().getLast4();

            paymentRepository.save(PaymentInfo.builder()
                    .customer(customer)
                    .price(BigDecimal.valueOf(chargeRequest.getAmount()).divide(BigDecimal.valueOf(100)))
                    .currency(chargeRequest.getCurrency().toString().toUpperCase())
                    .description("Payment for order № " + id)
                    .date(LocalDateTime.now(ZoneOffset.UTC))
                    .card("**** **** **** " + number)
                    .paymentStatus(e.getCode())
                    .errors(e.getUserMessage())
                    .build()
            );
            throw e;
        }

        return charge;
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
    public String updateSubscription(String email, String customerId, String subscriptionId)
            throws StripeException {
        Subscription subscription = Subscription.retrieve(subscriptionId);

        String productId = subscription.getItems().getData().get(0).getPrice().getProduct();

        Price price = getPrice(productId);

        if (price != null) {
            SubscriptionUpdateParams params2 = SubscriptionUpdateParams.builder()
                    .addItem(
                            SubscriptionUpdateParams.Item.builder()
                                    .setId(subscription.getItems().getData().get(0).getId())
                                    .setPrice(price.getId())
                                    .build())
                    .build();

            subscription = subscription.update(params2);
        }
        return subscription.toJson();
    }

    @Override
    public void cancelSubscription(String email, String productId, String subscriptionId)
            throws StripeException {
        Subscription subscription = Subscription.retrieve(subscriptionId);
        subscription.cancel();
    }

    @Override
    public Customer getCustomer(String email, String customerId)
            throws CustomerException, StripeException {
        customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerException.CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        return Customer.retrieve(customerId);
    }

    @Override
    public String createCustomer(CreateCustomer customer)
            throws StripeException {

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

        return Customer.create(customerParams).getId();
    }

    @Override
    public void updateCustomer(kazantseva.project.OnlineStore.model.entity.Customer updatedCustomer)
            throws StripeException {
        Customer customer =
                Customer.retrieve(updatedCustomer.getStripeId());

        Map<String, Object> params = new HashMap<>();
        params.put("name", updatedCustomer.getName() + " " + updatedCustomer.getSurname());

        customer.update(params);
    }

    @Override
    public void deleteCustomer(String customerId)
            throws StripeException {
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

        DecimalFormat df = new DecimalFormat("#,##0.00");
        var priceFormat = df.format(price.getUnitAmountDecimal().divide(BigDecimal.valueOf(100)));

        Currency cur = Currency.getInstance(price.getCurrency().toUpperCase());
        String symbol = cur.getSymbol();

        return SubscriptionDTO.builder()
                .id(product.getId())
                .image(image)
                .name(product.getName())
                .description(product.getDescription())
                .active(product.getActive())
                .price(symbol + priceFormat)
                .recurring(price.getRecurring().getInterval().toLowerCase())
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