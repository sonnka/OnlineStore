package kazantseva.project.OnlineStore.exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

public class CustomStripeException extends Exception {

    private final StripeExceptionProfile stripeExceptionProfile;

    public CustomStripeException(StripeExceptionProfile stripeExceptionProfile) {
        super(stripeExceptionProfile.exceptionMessage);
        this.stripeExceptionProfile = stripeExceptionProfile;
    }

    public String getName() {
        return stripeExceptionProfile.exceptionName;
    }

    public HttpStatus getResponseStatus() {
        return stripeExceptionProfile.responseStatus;
    }

    @AllArgsConstructor
    public enum StripeExceptionProfile {

        PRICE_NULL("price_is_null",
                "Product price can not be null.", HttpStatus.BAD_REQUEST);

        private final String exceptionName;
        private final String exceptionMessage;
        private final HttpStatus responseStatus;
    }
}
