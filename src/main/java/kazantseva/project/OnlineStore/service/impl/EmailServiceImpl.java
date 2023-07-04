package kazantseva.project.OnlineStore.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kazantseva.project.OnlineStore.exceptions.SecurityException;
import kazantseva.project.OnlineStore.exceptions.SecurityException.SecurityExceptionProfile;
import kazantseva.project.OnlineStore.model.entity.VerificationToken;
import kazantseva.project.OnlineStore.service.EmailService;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    final ResourceBundleMessageSource messageSource;
    private final TemplateEngine templateEngine;
    private final JavaMailSender emailSender;
    private final Context context = new Context(Locale.US);

    public EmailServiceImpl(ResourceBundleMessageSource messageSource, TemplateEngine templateEngine,
                            JavaMailSender emailSender) {
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.emailSender = emailSender;
    }

    @Override
    @Transactional
    public void sendConfirmationMail(VerificationToken token, String to) throws SecurityException {
        Locale locale = new Locale(token.getLocale().split("_")[0],
                token.getLocale().split("_")[1]);

        Context context = new Context(locale);
        String link = "http://localhost:8080/confirm-email?token=";
        context.setVariable("link", link + token.getToken());

        String process = templateEngine.process("letter", context);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setSubject(messageSource.getMessage("subject", null, locale));
            helper.setText(process, true);
            helper.setTo(to);
        } catch (MessagingException e) {
            throw new SecurityException(SecurityExceptionProfile.FAIL_SEND_EMAIL);
        }

        emailSender.send(mimeMessage);
    }

    @Override
    @Transactional
    public void sendAdminMail(String to) throws SecurityException {
        String process = templateEngine.process("adminletter", context);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setSubject(messageSource.getMessage("subject3", null, Locale.US));
            helper.setText(process, true);
            helper.setTo(to);
        } catch (MessagingException e) {
            throw new SecurityException(SecurityExceptionProfile.FAIL_SEND_EMAIL);
        }

        emailSender.send(mimeMessage);
    }

    @Override
    public void sendDeletionMail(String to) throws SecurityException {
        String process = templateEngine.process("deleteletter", context);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setSubject(messageSource.getMessage("subject2", null, Locale.US));
            helper.setText(process, true);
            helper.setTo(to);
        } catch (MessagingException e) {
            throw new SecurityException(SecurityExceptionProfile.FAIL_SEND_EMAIL);
        }

        emailSender.send(mimeMessage);
    }
}
