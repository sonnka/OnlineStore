package kazantseva.project.OnlineStore.exceptions.handlers;

import jakarta.validation.constraints.NotNull;
import kazantseva.project.OnlineStore.exceptions.dto.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  @NotNull HttpHeaders headers,
                                                                  @NotNull HttpStatusCode status,
                                                                  @NotNull WebRequest webRequest) {
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError -> "Field " + FieldError.getField() + ": " + FieldError.getDefaultMessage())
                .toList();

        var exceptionBody = new ExceptionResponse("validation_exceptions", errors.toString());

        return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(), status, webRequest);
    }
}
