package kazantseva.project.OnlineStore.exceptions.handlers;

import com.stripe.exception.*;
import kazantseva.project.OnlineStore.exceptions.dto.ExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class StripeExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = CardException.class)
    public ResponseEntity<Object> handleCardException(CardException exception,
                                                      WebRequest webRequest) {
        var exceptionBody = new ExceptionResponse(exception.getCode(), exception.getUserMessage());

        return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
                HttpStatus.valueOf(exception.getStatusCode()), webRequest);
    }

    @ExceptionHandler(value = InvalidRequestException.class)
    public ResponseEntity<Object> handleInvalidRequestException(InvalidRequestException exception,
                                                                WebRequest webRequest) {
        var exceptionBody = new ExceptionResponse(exception.getCode(), exception.getUserMessage());

        return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
                HttpStatus.valueOf(exception.getStatusCode()), webRequest);
    }

    @ExceptionHandler(value = ApiConnectionException.class)
    public ResponseEntity<Object> handleApiConnectionException(ApiConnectionException exception,
                                                               WebRequest webRequest) {
        var exceptionBody = new ExceptionResponse(exception.getCode(), exception.getUserMessage());

        return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
                HttpStatus.valueOf(exception.getStatusCode()), webRequest);
    }

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<Object> handleAPIException(ApiException exception,
                                                     WebRequest webRequest) {
        var exceptionBody = new ExceptionResponse(exception.getCode(), exception.getUserMessage());

        return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
                HttpStatus.valueOf(exception.getStatusCode()), webRequest);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception,
                                                                WebRequest webRequest) {
        var exceptionBody = new ExceptionResponse(exception.getCode(), exception.getUserMessage());

        return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
                HttpStatus.valueOf(exception.getStatusCode()), webRequest);
    }

    @ExceptionHandler(value = IdempotencyException.class)
    public ResponseEntity<Object> handleIdempotencyException(IdempotencyException exception,
                                                             WebRequest webRequest) {
        var exceptionBody = new ExceptionResponse(exception.getCode(), exception.getUserMessage());

        return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
                HttpStatus.valueOf(exception.getStatusCode()), webRequest);
    }

    @ExceptionHandler(value = PermissionException.class)
    public ResponseEntity<Object> handlePermissionException(PermissionException exception,
                                                            WebRequest webRequest) {
        var exceptionBody = new ExceptionResponse(exception.getCode(), exception.getUserMessage());

        return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
                HttpStatus.valueOf(exception.getStatusCode()), webRequest);
    }

    @ExceptionHandler(value = RateLimitException.class)
    public ResponseEntity<Object> handleRateLimitException(RateLimitException exception,
                                                           WebRequest webRequest) {
        var exceptionBody = new ExceptionResponse(exception.getCode(), exception.getUserMessage());

        return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
                HttpStatus.valueOf(exception.getStatusCode()), webRequest);
    }

    @ExceptionHandler(value = SignatureVerificationException.class)
    public ResponseEntity<Object> handleSignatureVerificationException(SignatureVerificationException exception,
                                                                       WebRequest webRequest) {
        var exceptionBody = new ExceptionResponse(exception.getCode(), exception.getUserMessage());

        return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
                HttpStatus.valueOf(exception.getStatusCode()), webRequest);
    }
}
