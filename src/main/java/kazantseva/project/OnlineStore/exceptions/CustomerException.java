package kazantseva.project.OnlineStore.exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

public class CustomerException extends Exception {

    private final CustomerExceptionProfile customerExceptionProfile;

    public CustomerException(CustomerExceptionProfile customerExceptionProfile) {
        super(customerExceptionProfile.exceptionMessage);
        this.customerExceptionProfile = customerExceptionProfile;
    }

    public String getName() {
        return customerExceptionProfile.exceptionName;
    }

    public HttpStatus getResponseStatus() {
        return customerExceptionProfile.responseStatus;
    }

    @AllArgsConstructor
    public enum CustomerExceptionProfile {

        CUSTOMER_NOT_FOUND("customer_not_found",
                "Customer is not found.", HttpStatus.NOT_FOUND),

        CUSTOMER_ALREADY_ADMIN("customer_already_admin",
                "Customer already has role of admin.", HttpStatus.BAD_REQUEST),

        CANNOT_DELETE_ADMIN("cannot_delete_admin",
                "Admin can not delete another admin.", HttpStatus.FORBIDDEN),

        NOT_ADMIN("not_admin",
                "You are not admin.", HttpStatus.FORBIDDEN),

        NOT_BUYER("not_buyer",
                "You are not buyer.", HttpStatus.FORBIDDEN),

        EMAIL_MISMATCH("email_mismatch",
                "Email provided does not match the customer's email.", HttpStatus.FORBIDDEN),

        ID_MISMATCH("id_mismatch",
                "Id provided does not match the customer's id.", HttpStatus.FORBIDDEN),

        FAIL_UPLOAD_AVATAR("fail_upload_avatar",
                "Failed to upload image, please try again.",
                HttpStatus.BAD_GATEWAY);

        private final String exceptionName;
        private final String exceptionMessage;
        private final HttpStatus responseStatus;
    }
}
