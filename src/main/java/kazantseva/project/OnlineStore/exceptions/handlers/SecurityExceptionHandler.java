package kazantseva.project.OnlineStore.exceptions.handlers;

import kazantseva.project.OnlineStore.exceptions.SecurityException;
import kazantseva.project.OnlineStore.exceptions.dto.ExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SecurityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = SecurityException.class)
    public ResponseEntity<Object> handleCustomerException(SecurityException exception,
                                                          WebRequest webRequest) {
        var exceptionBody = new ExceptionResponse(exception.getName(), exception.getMessage());

        return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
                exception.getResponseStatus(), webRequest);
    }
}
