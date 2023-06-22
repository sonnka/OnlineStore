package kazantseva.project.OnlineStore.service;

import jakarta.mail.MessagingException;
import kazantseva.project.OnlineStore.model.entity.VerificationToken;

public interface EmailService {

    void sendConfirmationMail(VerificationToken token, String to) throws MessagingException;

    void sendAdminMail(String to) throws MessagingException;

    void sendDeletionMail(String to) throws MessagingException;
}
