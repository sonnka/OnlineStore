package kazantseva.project.OnlineStore.exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

public class OrderException extends Exception {
    private final OrderExceptionProfile orderExceptionProfile;

    public OrderException(OrderExceptionProfile orderExceptionProfile) {
        super(orderExceptionProfile.exceptionMessage);
        this.orderExceptionProfile = orderExceptionProfile;
    }

    public String getName() {
        return orderExceptionProfile.exceptionName;
    }

    public HttpStatus getResponseStatus() {
        return orderExceptionProfile.responseStatus;
    }

    @AllArgsConstructor
    public enum OrderExceptionProfile {

        ORDER_NOT_FOUND("order_not_found",
                "Order is not found.", HttpStatus.NOT_FOUND),

        CANNOT_CHANGE_PAID("cannot_change_paid_order",
                "You can not change the paid order.", HttpStatus.FORBIDDEN),

        CANNOT_DELETE_UNPAID("cannot_delete_unpaid_order",
                "You can not delete the unpaid order.", HttpStatus.FORBIDDEN),

        MINIMUM_ORDER_PRICE("minimum_order_price",
                "Minimum order price is $1.", HttpStatus.BAD_REQUEST),

        EMPTY_ORDER("empty_order",
                "You can not create order without products.", HttpStatus.BAD_REQUEST);


        private final String exceptionName;
        private final String exceptionMessage;
        private final HttpStatus responseStatus;
    }
}
