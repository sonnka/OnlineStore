package kazantseva.project.OnlineStore.service;

import jakarta.mail.MessagingException;
import kazantseva.project.OnlineStore.exceptions.SecurityException;
import kazantseva.project.OnlineStore.model.entity.VerificationToken;

public interface EmailService {

    void sendConfirmationMail(VerificationToken token, String to) throws MessagingException, SecurityException;

    void sendAdminMail(String to) throws MessagingException, SecurityException;

    void sendDeletionMail(String to) throws MessagingException, SecurityException;
}
