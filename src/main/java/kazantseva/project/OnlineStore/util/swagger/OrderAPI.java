package kazantseva.project.OnlineStore.util.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kazantseva.project.OnlineStore.exceptions.CustomerException;
import kazantseva.project.OnlineStore.exceptions.OrderException;
import kazantseva.project.OnlineStore.model.request.RequestOrder;
import kazantseva.project.OnlineStore.model.response.OrderDTO;
import kazantseva.project.OnlineStore.model.response.ShortOrderDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order", description = "the order API")
public interface OrderAPI {

    @Operation(summary = "Get orders", description = "Returns list of all customer`s orders.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class, subTypes = {ShortOrderDTO.class}))),
            @ApiResponse(responseCode = "400", description = "Invalid customer id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "Viewing other customer's orders is prohibited", content = @Content)})
    @RequestMapping(value = "/customers/{customer-id}/orders", produces = {"application/json"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    Page<ShortOrderDTO> getOrders(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @Parameter(description = "ID of customer", required = true)
            @PathVariable("customer-id") long customerId,
            @ParameterObject Pageable pageable) throws CustomerException;

    @Operation(summary = "Get order", description = "Returns the customer`s order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid customer id or product id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer or order not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "Viewing other customer's orders is prohibited", content = @Content)})
    @RequestMapping(value = "/customers/{customer-id}/orders/{order-id}", produces = {"application/json"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    OrderDTO getFullOrder(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @Parameter(description = "ID of customer", required = true)
            @PathVariable("customer-id") long customerId,
            @Parameter(description = "ID of order", required = true)
            @PathVariable("order-id") long orderId) throws CustomerException, OrderException;

    @Operation(summary = "Update order", description = "Returns the updated order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer or order not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "Updating other customer's orders is prohibited", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/customers/{customer-id}/orders/{order-id}")
    OrderDTO updateOrder(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @Parameter(description = "ID of customer", required = true)
            @PathVariable("customer-id") long customerId,
            @Parameter(description = "ID of order", required = true)
            @PathVariable("order-id") long orderId,
            @Parameter(description = "Changed order", required = true)
            @RequestBody RequestOrder order) throws CustomerException, OrderException;

    @Operation(summary = "Delete order", description = "Deletes order by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid customer id or product id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer or order not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "Deleting other customer's orders is prohibited", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/customers/{customer-id}/orders/{order-id}")
    void deleteOrder(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @Parameter(description = "ID of customer", required = true)
            @PathVariable("customer-id") long customerId,
            @Parameter(description = "ID of order", required = true)
            @PathVariable("order-id") long orderId) throws CustomerException, OrderException;
}
