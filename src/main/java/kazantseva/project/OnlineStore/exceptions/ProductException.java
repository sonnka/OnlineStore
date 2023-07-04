package kazantseva.project.OnlineStore.exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

public class ProductException extends Exception {
    private final ProductExceptionProfile productExceptionProfile;

    public ProductException(ProductExceptionProfile productExceptionProfile) {
        super(productExceptionProfile.exceptionMessage);
        this.productExceptionProfile = productExceptionProfile;
    }

    public String getName() {
        return productExceptionProfile.exceptionName;
    }

    public HttpStatus getResponseStatus() {
        return productExceptionProfile.responseStatus;
    }

    @AllArgsConstructor
    public enum ProductExceptionProfile {

        PRODUCT_NOT_FOUND("product_not_found",
                "Product is not found.", HttpStatus.NOT_FOUND),

        FAIL_UPLOAD_IMAGE("fail_upload_image",
                "Failed to upload image, please try again.",
                HttpStatus.BAD_GATEWAY);

        private final String exceptionName;
        private final String exceptionMessage;
        private final HttpStatus responseStatus;
    }
}
