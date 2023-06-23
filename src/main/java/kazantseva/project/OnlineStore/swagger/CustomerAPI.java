package kazantseva.project.OnlineStore.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kazantseva.project.OnlineStore.model.request.CreateCustomer;
import kazantseva.project.OnlineStore.model.request.RequestCustomer;
import kazantseva.project.OnlineStore.model.response.AdminDTO;
import kazantseva.project.OnlineStore.model.response.CustomerDTO;
import kazantseva.project.OnlineStore.model.response.FullCustomerDTO;
import kazantseva.project.OnlineStore.model.response.LoginResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Customer", description = "the customer API")
public interface CustomerAPI {

    @Operation(summary = "Login", description = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    LoginResponse login(
            @Parameter(description = "Authentication", required = true)
            Authentication auth);

    @Operation(summary = "Register", description = "Registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful request", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)})
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    void register(
            @Parameter(description = "New customer", required = true)
            @RequestBody @Valid CreateCustomer customer);

    @Operation(summary = "Get customer", description = "Returns customer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FullCustomerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid customer id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "Viewing other customer is prohibited", content = @Content)})
    @RequestMapping(value = "/customers/{customer-id}", produces = {"application/json"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    FullCustomerDTO getCustomer(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @Parameter(description = "ID of customer", required = true)
            @PathVariable("customer-id") long customerId);

    @Operation(summary = "Update customer", description = "Returns updated customer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "Updating other customer is prohibited", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/customers/{customer-id}")
    CustomerDTO updateCustomer(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @Parameter(description = "ID of customer", required = true)
            @PathVariable("customer-id") long customerId,
            @Parameter(description = "Changed customer", required = true)
            @RequestBody @Valid RequestCustomer customer);

    @Operation(summary = "Update avatar", description = "Updates avatar of customer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "Updating other customer is prohibited", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/customers/{customer-id}/upload")
    void uploadAvatar(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @Parameter(description = "ID of customer", required = true)
            @PathVariable("customer-id") long customerId,
            @Parameter(description = "Avatar of customer (Multipart file)", required = true)
            @RequestParam("file") MultipartFile file);

    @Operation(summary = "Delete customer", description = "Deletes customer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid customer id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "Deleting other customer is prohibited", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/customers/{customer-id}")
    void deleteCustomer(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @Parameter(description = "ID of customer", required = true)
            @PathVariable("customer-id") long customerId);

    @Operation(summary = "Get customers", description = "Returns list of customers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class, subTypes = {CustomerDTO.class}))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "405", description = "You are not admin", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/customers")
    Page<CustomerDTO> getCustomers(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @ParameterObject Pageable pageable);

    @Operation(summary = "Get admins", description = "Returns list of admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class, subTypes = {AdminDTO.class}))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "405", description = "You are not admin", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/admins")
    Page<AdminDTO> getAdmins(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @ParameterObject Pageable pageable);

    @Operation(summary = "To admin", description = "Turns customer into admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid customer id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "You are not admin", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/admin/customers/{customer-id}/admin")
    void toAdmin(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @Parameter(description = "ID of customer", required = true)
            @PathVariable("customer-id") long customerId);

    @Operation(summary = "Resend email", description = "Resends confirmation email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid customer id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "You are not admin", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/customers/{customer-id}/resend")
    void resendEmail(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @Parameter(description = "ID of customer", required = true)
            @PathVariable("customer-id") long customerId);
}
