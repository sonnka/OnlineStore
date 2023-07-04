package kazantseva.project.OnlineStore.service.impl;

import jakarta.mail.MessagingException;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.CustomerException.CustomerExceptionProfile;
import kazantseva.project.OnlineStore.exceptions.SecurityException;
import kazantseva.project.OnlineStore.exceptions.SecurityException.SecurityExceptionProfile;
import kazantseva.project.OnlineStore.model.entity.*;
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
import kazantseva.project.OnlineStore.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    public static String UPLOAD_DIRECTORY = "tmp/images/customers";

    private final PasswordEncoder passwordEncoder;
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;
    private VerificationTokenRepository tokenRepository;
    private EmailService emailService;

    @Override
    @Transactional
    public void register(CreateCustomer newCustomer) throws SecurityException {
        if (customerRepository.existsByEmailIgnoreCase(newCustomer.getEmail())) {
            throw new SecurityException(SecurityExceptionProfile.EMAIL_IS_OCCUPIED);
        }

        Customer customer = Customer.builder()
                .email(newCustomer.getEmail())
                .password(passwordEncoder.encode(newCustomer.getPassword()))
                .name(newCustomer.getName())
                .surname(newCustomer.getSurname())
                .role(CustomerRole.BUYER)
                .build();

        Order order = Order.builder()
                .customer(customer)
                .type(Type.DRAFT)
                .status(Status.UNPAID)
                .build();

        customer.setBasket(orderRepository.save(order).getId());

        customerRepository.save(customer);

        generateTokenAndSendMail(customer);
    }

    @Override
    public LoginResponse login(Authentication auth) throws SecurityException {
        var customer = customerRepository.findByEmailIgnoreCase(auth.getName()).orElseThrow(
                () -> new SecurityException(SecurityExceptionProfile.WRONG_AUTHENTICATION_DATA));

        return LoginResponse.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .build();
    }

    @Override
    @Transactional
    public LoginResponse login(String token) throws SecurityException, CustomerException {
        var customer = customerRepository.findByVerificationToken(token).orElseThrow(() ->
                new SecurityException(SecurityExceptionProfile.INVALID_VERIFICATION_TOKEN));

        verifyAccount(customer.getEmail(), token);

        return LoginResponse.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .build();
    }

    @Override
    public UserDetails toUserDetails(Customer customer) {
        return User.withUsername(customer.getEmail())
                .password(customer.getPassword())
                .disabled(!customer.isEnabled())
                .roles(String.valueOf(customer.getRole())).build();
    }

    @Override
    public FullCustomerDTO getCustomer(String email, Long customerId) throws CustomerException {
        var customer = findByIdAndCheckByEmail(customerId, email);

        return toFullCustomerDTO(customer);
    }

    @Override
    public Long getCustomerId(String email) throws CustomerException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        return customer.getId();
    }

    @Override
    public Long getBasketId(String email) throws CustomerException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        return customer.getBasket();
    }

    @Override
    public Page<CustomerDTO> getCustomers(String email, Pageable pageable) throws CustomerException {
        checkAdminByEmail(email);

        return customerRepository.findByRole(CustomerRole.BUYER, pageable)
                .map(this::toCustomerDTO);
    }

    @Override
    public Page<AdminDTO> getAdmins(String email, Pageable pageable) throws CustomerException {
        checkAdminByEmail(email);

        return customerRepository.findByRole(CustomerRole.ADMIN, pageable)
                .map(this::toAdminDTO);
    }

    @Override
    @Transactional
    public void toAdmin(String email, Long customerId) throws CustomerException, SecurityException {
        checkAdminByEmail(email);

        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        if (CustomerRole.ADMIN.equals(customer.getRole())) {
            throw new CustomerException(CustomerExceptionProfile.CUSTOMER_ALREADY_ADMIN);
        }

        try {
            emailService.sendAdminMail(customer.getEmail());

            orderRepository.deleteByCustomer(customer);

            customer.setRole(CustomerRole.ADMIN);
            customer.setGrantedAdminBy(email);
            customer.setDate(LocalDateTime.now(ZoneOffset.UTC));
            customer.setBasket(null);

            customerRepository.save(customer);
        } catch (MessagingException e) {
            throw new SecurityException(SecurityExceptionProfile.FAIL_SEND_EMAIL);
        }
    }

    @Override
    public CustomerDTO updateCustomer(String email, long customerId, RequestCustomer customer)
            throws CustomerException {
        var oldCustomer = findByIdAndCheckByEmail(customerId, email);

        Optional.of(customer.getName()).ifPresent(oldCustomer::setName);
        Optional.of(customer.getSurname()).ifPresent(oldCustomer::setSurname);
        Optional.ofNullable(customer.getAvatar()).ifPresent(oldCustomer::setAvatar);

        customerRepository.save(oldCustomer);

        return toCustomerDTO(oldCustomer);
    }

    @Override
    public void uploadAvatar(String email, Long customerId, MultipartFile file) throws CustomerException {
        var customer = findByIdAndCheckByEmail(customerId, email);

        if (file != null && file.getOriginalFilename() != null) {

            String fileExtension = file.getOriginalFilename().split("\\.")[1];

            String generatedFileName = "avatar_" + customerId + "." + fileExtension;

            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, generatedFileName);

            customer.setAvatar("");
            customer.setAvatar(generatedFileName);

            customerRepository.save(customer);

            try {
                Files.createDirectories(Path.of(UPLOAD_DIRECTORY));

                Files.copy(file.getInputStream(), fileNameAndPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new CustomerException(CustomerExceptionProfile.FAIL_UPLOAD_AVATAR);
            }
        }
    }

    @Override
    @Transactional
    public void deleteCustomer(String email, long customerId) throws CustomerException, SecurityException {
        var currentCustomer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        if (!customer.getEmail().equals(email)) {
            if (CustomerRole.ADMIN.equals(currentCustomer.getRole())) {
                if (!CustomerRole.ADMIN.equals(customer.getRole())) {
                    deleteByAdmin(customer);
                    return;
                }
            }
            throw new CustomerException(CustomerExceptionProfile.CANNOT_DELETE_ADMIN);
        }

        deleteYourself(customer);
    }

    @Override
    @Transactional
    public void resendEmail(String email, Long customerId) throws CustomerException, SecurityException {
        checkAdminByEmail(email);

        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        if (customer.isEnabled()) {
            throw new SecurityException(SecurityExceptionProfile.EMAIL_ALREADY_CONFIRMED);
        }

        generateTokenAndSendMail(customer);
    }

    @Transactional
    public void generateTokenAndSendMail(Customer customer) throws SecurityException {
        VerificationToken firstToken = tokenRepository.findFirstByCustomerOrderByIdDesc(customer)
                .orElse(null);

        String locale = LocaleContextHolder.getLocale().toString();

        if (firstToken != null) {
            locale = firstToken.getLocale();
        }

        tokenRepository.deleteByCustomer(customer);

        VerificationToken verificationToken = new VerificationToken(customer, locale);

        tokenRepository.save(verificationToken);

        try {
            emailService.sendConfirmationMail(verificationToken, customer.getEmail());
        } catch (MessagingException e) {
            throw new SecurityException(SecurityExceptionProfile.FAIL_SEND_EMAIL);
        }
    }

    private void deleteYourself(Customer customer) {
        tokenRepository.deleteByCustomer(customer);
        orderRepository.deleteByCustomer(customer);
        customerRepository.deleteById(customer.getId());
    }

    private void deleteByAdmin(Customer customer) throws SecurityException {
        try {
            emailService.sendDeletionMail(customer.getEmail());

            deleteYourself(customer);
        } catch (MessagingException e) {
            throw new SecurityException(SecurityExceptionProfile.FAIL_SEND_EMAIL);
        }
    }

    private void verifyAccount(String email, String verificationToken)
            throws SecurityException, CustomerException {
        VerificationToken token = tokenRepository.findByToken(verificationToken)
                .orElseThrow(() -> new SecurityException(SecurityExceptionProfile.VERIFICATION_TOKEN_NOT_FOUND));

        if (token != null) {

            if (!email.equals(token.getCustomer().getEmail())) {
                throw new SecurityException(SecurityExceptionProfile.BAD_EMAIL);
            }

            Customer customer = customerRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

            if (token.getExpiryDate().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
                tokenRepository.delete(token);
                customerRepository.delete(customer);
                throw new SecurityException(SecurityExceptionProfile.EXPIRED_VERIFICATION_TOKEN);
            }

            tokenRepository.deleteByCustomerId(customer.getId());

            customer.setEnabled(true);

            customerRepository.save(customer);
        } else throw new SecurityException(SecurityExceptionProfile.INVALID_VERIFICATION_TOKEN);
    }

    private Customer findByIdAndCheckByEmail(Long customerId, String email) throws CustomerException {
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        if (!customer.getEmail().equals(email)) {
            throw new CustomerException(CustomerExceptionProfile.EMAIL_MISMATCH);
        }

        return customer;
    }

    private void checkAdminByEmail(String email) throws CustomerException {
        var customer = customerRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new CustomerException(CustomerExceptionProfile.CUSTOMER_NOT_FOUND));

        if (!CustomerRole.ADMIN.equals(customer.getRole())) {
            throw new CustomerException(CustomerExceptionProfile.NOT_ADMIN);
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
        var drafts = orderRepository.findAllByCustomerIdAndType(customer.getId(), Type.DRAFT);

        int amount = 0;

        if (drafts.size() > 0) {
            amount = drafts.get(0).getProducts().size();
        }

        return FullCustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .email(customer.getEmail())
                .avatar(customer.getAvatar())
                .amountOfBasketElem(amount)
                .totalAmountOfOrders(orderRepository.findAllByCustomerIdAndType(customer.getId(), Type.PUBLISHED)
                        .size())
                .amountOfPaidOrders(orderRepository.findAllByCustomerIdAndTypeAndStatus(customer.getId(),
                                Type.PUBLISHED, Status.PAID)
                        .size())
                .amountOfUnpaidOrders(orderRepository.findAllByCustomerIdAndTypeAndStatus(customer.getId(),
                                Type.PUBLISHED, Status.UNPAID)
                        .size())
                .amountOfAddedAdmins(customerRepository.findAll()
                        .stream().filter(c -> customer.getEmail().equals(c.getGrantedAdminBy()))
                        .toList().size())
                .build();
    }
}
