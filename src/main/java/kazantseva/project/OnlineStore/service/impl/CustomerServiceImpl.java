package kazantseva.project.OnlineStore.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.entity.CustomerRole;
import kazantseva.project.OnlineStore.model.entity.Status;
import kazantseva.project.OnlineStore.model.entity.VerificationToken;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.RequestCustomer;
import kazantseva.project.OnlineStore.model.response.AdminDTO;
import kazantseva.project.OnlineStore.model.response.CustomerDTO;
import kazantseva.project.OnlineStore.model.response.FullCustomerDTO;
import kazantseva.project.OnlineStore.model.response.LoginResponse;
import kazantseva.project.OnlineStore.repository.CustomerRepository;
import kazantseva.project.OnlineStore.repository.OrderRepository;
import kazantseva.project.OnlineStore.repository.VerificationTokenRepository;
import kazantseva.project.OnlineStore.service.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    final ResourceBundleMessageSource messageSource;
    final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;
    private VerificationTokenRepository tokenRepository;
    private JavaMailSender emailSender;

    @Override
    @Transactional
    public void register(CreateCustomer newCustomer) {
        if (customerRepository.existsByEmailIgnoreCase(newCustomer.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, newCustomer.getEmail() + " is already occupied!");
        }

        Customer customer = customerRepository.save(Customer.builder()
                .email(newCustomer.getEmail())
                .password(passwordEncoder.encode(newCustomer.getPassword()))
                .name(newCustomer.getName())
                .surname(newCustomer.getSurname())
                .role(CustomerRole.BUYER)
                .build());

        generateTokenAndSendMail(customer);
    }

    @Override
    public LoginResponse login(Authentication auth) {
        var customer = customerRepository.findByEmailIgnoreCase(auth.getName()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong authentication data!"));

        return LoginResponse.builder().id(customer.getId()).email(customer.getEmail()).build();
    }

    @Override
    @Transactional
    public LoginResponse login(String token) {
        var customer = customerRepository.findByVerificationToken(token).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid verification token!"));

        verifyAccount(customer.getEmail(), token);

        return LoginResponse.builder().id(customer.getId()).email(customer.getEmail()).build();
    }

    @Override
    public UserDetails toUserDetails(Customer customer) {
        return User.withUsername(customer.getEmail())
                .password(customer.getPassword())
                .disabled(!customer.isEnabled())
                .roles(String.valueOf(customer.getRole())).build();
    }

    @Override
    public FullCustomerDTO getCustomer(String email, Long customerId) {
        var customer = findByIdAndCheckByEmail(customerId, email);
        return toFullCustomerDTO(customer);
    }

    @Override
    public Page<CustomerDTO> getCustomers(String email, Pageable pageable) {
        checkAdminByEmail(email);

        return customerRepository.findByRole(CustomerRole.BUYER, pageable)
                .map(this::toCustomerDTO);
    }

    @Override
    public Page<AdminDTO> getAdmins(String email, Pageable pageable) {
        checkAdminByEmail(email);

        return customerRepository.findByRole(CustomerRole.ADMIN, pageable)
                .map(this::toAdminDTO);
    }

    @Override
    @Transactional
    public void toAdmin(String email, Long customerId) {
        checkAdminByEmail(email);

        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with ID " + customerId + " not found!"));

        if (CustomerRole.ADMIN.equals(customer.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This customer is already admin!");
        }

        try {
            sendAdminMail(customer.getEmail());
            orderRepository.deleteByCustomer(customer);
            customer.setRole(CustomerRole.ADMIN);
            customer.setGrantedAdminBy(email);
            customer.setDate(LocalDateTime.now(ZoneOffset.UTC));
            customerRepository.save(customer);
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Failed to send email, please try again!");
        }
    }

    @Override
    @Transactional
    public void resendEmail(String email, Long customerId) {
        checkAdminByEmail(email);

        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with ID " + customerId + " not found!"));

        if (customer.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This customer's email address has already been successfully confirmed");
        }

        generateTokenAndSendMail(customer);
    }

    @Override
    public Long getCustomerId(String email) {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with email " + email + " not found!"));
        return customer.getId();
    }

    @Override
    public CustomerDTO updateCustomer(String email, long customerId, RequestCustomer customer) {
        var oldCustomer = findByIdAndCheckByEmail(customerId, email);

        Optional.of(customer.getName()).ifPresent(oldCustomer::setName);
        Optional.of(customer.getSurname()).ifPresent(oldCustomer::setSurname);

        customerRepository.save(oldCustomer);

        return toCustomerDTO(oldCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(String email, long customerId) {
        var currentCustomer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with email " + email + " not found!"));

        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with ID " + customerId + " not found!"));

        if (!customer.getEmail().equals(email)) {
            if (CustomerRole.ADMIN.equals(currentCustomer.getRole())) {
                if (!CustomerRole.ADMIN.equals(customer.getRole())) {
                    deleteByAdmin(customer);
                    return;
                }
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        deleteYourself(customer);
    }

    @Transactional
    public void generateTokenAndSendMail(Customer customer) {
        VerificationToken firstToken = tokenRepository.findFirstByCustomerOrderByIdDesc(customer).orElse(null);

        String locale = LocaleContextHolder.getLocale().toString();

        if (firstToken != null) {
            locale = firstToken.getLocale();
        }

        tokenRepository.deleteByCustomer(customer);

        VerificationToken verificationToken = new VerificationToken(customer, locale);

        tokenRepository.save(verificationToken);

        try {
            sendConfirmationMail(verificationToken, customer.getEmail());
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Failed to send confirmation email, please try again in 24 hours!");
        }
    }

    private void deleteYourself(Customer customer) {
        tokenRepository.deleteByCustomer(customer);
        orderRepository.deleteByCustomer(customer);
        customerRepository.deleteById(customer.getId());
    }

    private void deleteByAdmin(Customer customer) {
        try {
            sendDeletionMail(customer.getEmail());
            tokenRepository.deleteByCustomer(customer);
            orderRepository.deleteByCustomer(customer);
            customerRepository.deleteById(customer.getId());
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Failed to send email, please try again!");
        }
    }

    @Transactional
    public void sendConfirmationMail(VerificationToken token, String to) throws MessagingException {
        Locale locale = new Locale(token.getLocale().split("_")[0], token.getLocale().split("_")[1]);

        Context context = new Context(locale);
        context.setVariable("link", "http://localhost:8080/confirm-email?token=" + token.getToken());

        String process = templateEngine.process("letter", context);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setSubject(messageSource.getMessage("subject", null, locale));
        helper.setText(process, true);
        helper.setTo(to);

        emailSender.send(mimeMessage);
    }

    @Transactional
    public void sendAdminMail(String to) throws MessagingException {
        Context context = new Context(Locale.US);

        String process = templateEngine.process("adminletter", context);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setSubject(messageSource.getMessage("subject3", null, Locale.US));
        helper.setText(process, true);
        helper.setTo(to);

        emailSender.send(mimeMessage);
    }

    private void sendDeletionMail(String to) throws MessagingException {
        Context context = new Context(Locale.US);

        String process = templateEngine.process("deleteletter", context);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setSubject(messageSource.getMessage("subject2", null, Locale.US));
        helper.setText(process, true);
        helper.setTo(to);

        emailSender.send(mimeMessage);
    }

    private void verifyAccount(String email, String verificationToken) {
        VerificationToken token = tokenRepository.findByToken(verificationToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No such verification token!"));

        if (token != null) {

            if (!email.equals(token.getCustomer().getEmail())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Failed email verification!");
            }

            Customer customer = customerRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Customer with email " + email + " not found!"));

            if (token.getExpiryDate().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
                tokenRepository.delete(token);
                customerRepository.delete(customer);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "You token is expired! Please, register again!");
            }

            tokenRepository.deleteByCustomerId(customer.getId());
            customer.setEnabled(true);
            customerRepository.save(customer);
        } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid verification token!");
    }

    private Customer findByIdAndCheckByEmail(Long customerId, String email) {
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with ID " + customerId + " not found!"));

        if (!customer.getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return customer;
    }

    private void checkAdminByEmail(String email) {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer with email " + email + " not found!"));
        if (!CustomerRole.ADMIN.equals(customer.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private CustomerDTO toCustomerDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .email(customer.getEmail())
                .build();
    }

    private AdminDTO toAdminDTO(Customer customer) {
        String date = "-";
        if (customer.getDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            date = customer.getDate().format(formatter);
        }

        return AdminDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .email(customer.getEmail())
                .grantedAdminBy(customer.getGrantedAdminBy())
                .grantedDate(date)
                .build();
    }

    private FullCustomerDTO toFullCustomerDTO(Customer customer) {
        return FullCustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .email(customer.getEmail())
                .totalAmountOfOrders(orderRepository.findAllByCustomerId(customer.getId()).size())
                .amountOfPaidOrders(orderRepository.findAllByCustomerId(customer.getId())
                        .stream()
                        .filter(order -> order.getStatus().equals(Status.PAID))
                        .toList()
                        .size())
                .amountOfUnpaidOrders(orderRepository.findAllByCustomerId(customer.getId())
                        .stream()
                        .filter(order -> order.getStatus().equals(Status.UNPAID))
                        .toList()
                        .size())
                .amountOfAddedAdmins(customerRepository.findAll()
                        .stream().filter(c -> customer.getEmail().equals(c.getGrantedAdminBy()))
                        .toList().size())
                .build();
    }
}
