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
import kazantseva.project.OnlineStore.model.entity.enums.MyCurrency;
import kazantseva.project.OnlineStore.model.entity.enums.Recurring;
import kazantseva.project.OnlineStore.model.mongo.entity.StripeProduct;
import kazantseva.project.OnlineStore.model.mongo.entity.StripeSubscription;
import kazantseva.project.OnlineStore.model.request.ChargeRequest;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.StripeProductRequest;
import kazantseva.project.OnlineStore.model.response.SubscriptionDTO;
import kazantseva.project.OnlineStore.model.response.SubscriptionResponse;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.repository.PaymentRepository;
import kazantseva.project.OnlineStore.repository.mongo.StripeProductRepository;
import kazantseva.project.OnlineStore.repository.mongo.StripeSubscriptionRepository;
import kazantseva.project.OnlineStore.service.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class StripeServiceImpl implements StripeService {

    private static final String CARD_TEMPLATE = "**** **** **** ";
    private static final String PRICE_TEMPLATE = "#,##0.00";
    private final CustomerRepository customerRepository;
    private final StripeProductRepository stripeProductRepository;
    private final StripeSubscriptionRepository stripeSubscriptionRepository;
    private final PaymentRepository paymentRepository;

    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    public StripeServiceImpl(CustomerRepository customerRepository,
                             StripeProductRepository stripeProductRepository,
                             StripeSubscriptionRepository stripeSubscriptionRepository,
                             PaymentRepository paymentRepository) {
        this.customerRepository = customerRepository;
        this.stripeProductRepository = stripeProductRepository;
        this.stripeSubscriptionRepository = stripeSubscriptionRepository;
        this.paymentRepository = paymentRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public void webhook(Event event) throws StripeException, CustomStripeException {

        Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElseThrow(
                () -> new CustomStripeException(StripeExceptionProfile.SUBSCRIPTION_NOT_FOUND));

        var stripeCustomer = Customer.retrieve(subscription.getCustomer());
        var customer = customerRepository.findCustomerByStripeId(stripeCustomer.getId());

        String number = PaymentMethod.retrieve(stripeCustomer.getInvoiceSettings().getDefaultPaymentMethod())
                .getCard().getLast4();

        Price priceObject = subscription.getItems().getData().get(0).getPrice();
        Product product = Product.retrieve(priceObject.getProduct());

        String description;
        String errors = "-";

        BigDecimal price = priceObject.getUnitAmountDecimal().divide(BigDecimal.valueOf(100));
        String status = subscription.getStatus();

        String beginName = "Subscription \"" + product.getName();

        switch (event.getType()) {
            case "customer.subscription.created" -> description = beginName + "\" was activated.";
            case "customer.subscription.pending_update_applied" -> description = beginName + "\" update applied.";
            case "customer.subscription.deleted" -> description = beginName + "\" was canceled.";
            case "customer.subscription.paused" -> description = beginName + "\" was paused.";
            case "customer.subscription.pending_update_expired" ->
                    description = beginName + "\" pending update expired.";
            case "customer.subscription.resumed" -> description = beginName + "\" was resumed.";
            case "customer.subscription.trial_will_end" -> description = beginName + "\" trial will end.";
            case "customer.subscription.updated" -> description = beginName + "\" was updated.";
            default -> description = "-";
        }

        paymentRepository.save(PaymentInfo.builder()
                .customer(customer)
                .date(LocalDateTime.now(ZoneOffset.UTC))
                .card(CARD_TEMPLATE + number)
                .description(description)
                .price(price)
                .currency(priceObject.getCurrency().toUpperCase())
                .paymentStatus(status)
                .errors(errors)
                .build()
        );
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
                    .card(CARD_TEMPLATE + charge.getPaymentMethodDetails().getCard().getLast4())
                    .paymentStatus(charge.getStatus())
                    .errors(charge.getFailureMessage())
                    .build()
            );
        } catch (StripeException e) {
            var stripeCustomer = getCustomer(customer.getEmail(), customer.getStripeId());
            var number = PaymentMethod.retrieve(stripeCustomer.getInvoiceSettings().getDefaultPaymentMethod())
                    .getCard().getLast4();

            paymentRepository.save(PaymentInfo.builder()
                    .customer(customer)
                    .price(BigDecimal.valueOf(chargeRequest.getAmount()).divide(BigDecimal.valueOf(100)))
                    .currency(chargeRequest.getCurrency().toString().toUpperCase())
                    .description("Payment for order № " + id)
                    .date(LocalDateTime.now(ZoneOffset.UTC))
                    .card(CARD_TEMPLATE + number)
                    .paymentStatus(e.getCode())
                    .errors(e.getUserMessage())
                    .build()
            );
            throw e;
        }
        return charge;
    }

    @Override
    public Customer getCustomer(String email, String customerId)
            throws StripeException {
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
    public List<SubscriptionDTO> getArchiveProducts(String email)
            throws CustomerException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        checkAdminByEmail(email);

        List<StripeProduct> products = stripeProductRepository.findAllByActive(false);

        List<SubscriptionDTO> subscriptions = new ArrayList<>();

        for (StripeProduct product : products) {
            subscriptions.add(toSubscriptionDTO(product, customer.getStripeId()));
        }

        return subscriptions;
    }

    @Override
    public List<SubscriptionDTO> getActiveProducts(String email)
            throws CustomerException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        List<StripeProduct> products = stripeProductRepository.findAllByActive(true);

        List<SubscriptionDTO> subscriptions = new ArrayList<>();

        for (StripeProduct product : products) {
            subscriptions.add(toSubscriptionDTO(product, customer.getStripeId()));
        }

        return subscriptions;
    }

    @Override
    public SubscriptionResponse getProduct(String productId) throws CustomStripeException {
        return toSubscriptionResponse(stripeProductRepository.findByProductId(productId)
                .orElseThrow(() -> new CustomStripeException(StripeExceptionProfile.SUBSCRIPTION_NOT_FOUND)));
    }

    @Override
    public SubscriptionResponse createProduct(String email, StripeProductRequest productRequest)
            throws StripeException, CustomStripeException, CustomerException {
        checkAdminByEmail(email);

        ProductCreateParams params = ProductCreateParams.builder()
                .setName(productRequest.getName())
                .setDescription(productRequest.getDescription())
                .build();

        Product product = Product.create(params);


        if (productRequest.getImage() != null && !productRequest.getImage().equals("")) {
            product = updateImage(productRequest.getImage(), product);
        }

        String currency = validateCurrency(productRequest.getCurrency());
        String recurring = validateRecurring(productRequest.getRecurring());

        var price = createPrice(product.getId(),
                productRequest.getPrice().multiply(BigDecimal.valueOf(100.0)).longValue(),
                currency,
                recurring);

        return toSubscriptionResponse(stripeProductRepository.save(StripeProduct.builder()
                .active(product.getActive())
                .productId(product.getId())
                .image(getProductImage(product))
                .name(product.getName())
                .description(product.getDescription())
                .price(price.getUnitAmountDecimal().divide(BigDecimal.valueOf(100)))
                .currency(price.getCurrency().toUpperCase())
                .recurring(recurring)
                .build()));
    }

    @Override
    public SubscriptionResponse updateProduct(String email, String productId, StripeProductRequest productRequest)
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

        if (productRequest.getImage() != null && !productRequest.getImage().equals("")) {
            updatedProduct = updateImage(productRequest.getImage(), product);
        }

        StripeProduct stripeProduct = stripeProductRepository.findByProductId(productId)
                .orElseThrow(() -> new CustomStripeException(StripeExceptionProfile.SUBSCRIPTION_NOT_FOUND));

        BigDecimal oldPrice = stripeProduct.getPrice();

        stripeProduct.setActive(updatedProduct.getActive());
        stripeProduct.setImage(getProductImage(updatedProduct));
        stripeProduct.setName(updatedProduct.getName());
        stripeProduct.setDescription(updatedProduct.getDescription());
        stripeProduct.setPrice(price.getUnitAmountDecimal().divide(BigDecimal.valueOf(100)));
        stripeProduct.setCurrency(price.getCurrency().toUpperCase());
        stripeProduct.setRecurring(price.getRecurring().getInterval().toUpperCase());

        stripeProduct = stripeProductRepository.save(stripeProduct);

        if (oldPrice.compareTo(price.getUnitAmountDecimal().divide(BigDecimal.valueOf(100))) != 0) {
            updateSubscription(stripeProduct);
        }

        return toSubscriptionResponse(stripeProduct);
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

        if (Boolean.TRUE.equals(product.getActive())) {
            params = ProductUpdateParams.builder().setActive(false).build();
            priceParams = PriceUpdateParams.builder().setActive(false).build();
        } else {
            params = ProductUpdateParams.builder().setActive(true).build();
            priceParams = PriceUpdateParams.builder().setActive(true).build();
        }

        product = product.update(params);

        price.update(priceParams);

        StripeProduct stripeProduct = stripeProductRepository.findByProductId(productId)
                .orElseThrow(() -> new CustomStripeException(StripeExceptionProfile.SUBSCRIPTION_NOT_FOUND));

        stripeProduct.setActive(product.getActive());

        stripeProductRepository.save(stripeProduct);

        cancelSubscriptionByProduct(stripeProduct);
    }

    @Override
    public void createSubscription(String email, String productId)
            throws CustomerException, StripeException, CustomStripeException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        Price price = getPrice(productId);

        if (price != null) {
            SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                    .setCustomer(customer.getStripeId())
                    .addItem(SubscriptionCreateParams.Item.builder()
                            .setPrice(price.getId())
                            .build())
                    .build();

            var subscription = Subscription.create(params);

            StripeProduct product = stripeProductRepository.findByProductId(productId)
                    .orElseThrow(() -> new CustomStripeException(StripeExceptionProfile.SUBSCRIPTION_NOT_FOUND));

            stripeSubscriptionRepository.save(StripeSubscription.builder()
                    .customerId(subscription.getCustomer())
                    .subscriptionId(subscription.getId())
                    .product(product)
                    .build());
        }
    }

    @Override
    public void cancelSubscription(String email, String productId)
            throws StripeException, CustomerException, CustomStripeException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        StripeProduct product = stripeProductRepository.findByProductId(productId).orElse(null);

        if (product == null) {
            return;
        }

        StripeSubscription stripeSub = stripeSubscriptionRepository.findFirstByCustomerIdAndProduct(
                customer.getStripeId(), product).orElseThrow(
                () -> new CustomStripeException(StripeExceptionProfile.SUBSCRIPTION_NOT_FOUND));

        Subscription subscription = Subscription.retrieve(stripeSub.getSubscriptionId());

        subscription.cancel();

        stripeSubscriptionRepository.delete(stripeSub);
    }

    private void updateSubscription(StripeProduct product)
            throws StripeException {
        List<String> subscriptions = stripeSubscriptionRepository.findAllByProduct(product)
                .stream().map(StripeSubscription::getSubscriptionId).toList();

        for (String id : subscriptions) {
            Subscription subscription = Subscription.retrieve(id);

            Price price = getPrice(product.getProductId());

            if (price != null) {
                SubscriptionUpdateParams params2 = SubscriptionUpdateParams.builder()
                        .addItem(
                                SubscriptionUpdateParams.Item.builder()
                                        .setId(subscription.getItems().getData().get(0).getId())
                                        .setPrice(price.getId())
                                        .build())
                        .build();

                subscription.update(params2);
            }
        }
    }

    @Async
    public void cancelSubscriptionByProduct(StripeProduct product)
            throws StripeException {
        if (product == null) {
            return;
        }

        List<String> subscriptions = stripeSubscriptionRepository.findAllByProduct(product)
                .stream().map(StripeSubscription::getSubscriptionId).toList();

        for (String id : subscriptions) {
            Subscription subscription = Subscription.retrieve(id);

            subscription.cancel();
        }

        stripeSubscriptionRepository.deleteAllByProduct(product);
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

    private SubscriptionDTO toSubscriptionDTO(StripeProduct product, String customerId) {
        if (product == null) {
            return null;
        }

        boolean isCustomer = false;

        if (customerId != null) {
            isCustomer = stripeSubscriptionRepository.findFirstByCustomerIdAndProduct(customerId, product)
                    .isPresent();
        }

        DecimalFormat df = new DecimalFormat(PRICE_TEMPLATE);
        var priceFormat = df.format(product.getPrice());

        Currency cur = Currency.getInstance(product.getCurrency());
        String symbol = cur.getSymbol();

        return SubscriptionDTO.builder()
                .id(product.getProductId())
                .image(product.getImage())
                .name(product.getName())
                .description(product.getDescription())
                .active(product.isActive())
                .price(symbol + priceFormat)
                .recurring(product.getRecurring())
                .isCustomer(isCustomer)
                .build();
    }

    private SubscriptionResponse toSubscriptionResponse(StripeProduct product) {
        if (product == null) {
            return null;
        }

        DecimalFormat df = new DecimalFormat(PRICE_TEMPLATE);
        var priceFormat = df.format(product.getPrice());

        return SubscriptionResponse.builder()
                .id(product.getProductId())
                .image(product.getImage())
                .name(product.getName())
                .description(product.getDescription())
                .price(priceFormat)
                .recurring(product.getRecurring())
                .currency(product.getCurrency())
                .build();
    }

    private void checkAdminByEmail(String email) throws CustomerException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerException.CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        if (!CustomerRole.ADMIN.equals(customer.getRole())) {
            throw new CustomerException(CustomerException.CustomerExceptionProfile.NOT_ADMIN);
        }
    }

    private String validateCurrency(String currency) {
        return switch (currency.toUpperCase()) {
            case "EUR" -> MyCurrency.EUR.name();
            case "UAN" -> MyCurrency.UAH.name();
            default -> MyCurrency.USD.name();
        };
    }

    private String validateRecurring(String recurring) {
        return switch (recurring.toUpperCase()) {
            case "DAY" -> Recurring.DAY.name();
            case "WEEK" -> Recurring.WEEK.name();
            case "YEAR" -> Recurring.YEAR.name();
            default -> Recurring.MONTH.name();
        };
    }

    private Product updateImage(String image, Product product) throws StripeException {
        ProductUpdateParams params =
                ProductUpdateParams.builder()
                        .addAllImage(List.of(image))
                        .build();
        return product.update(params);
    }

    private String getProductImage(Product product) {
        if (product.getImages().isEmpty()) {
            return null;
        }
        return product.getImages().get(0);
    }
}