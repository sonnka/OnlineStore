package kazantseva.project.OnlineStore.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kazantseva.project.OnlineStore.model.request.CreateProduct;
import kazantseva.project.OnlineStore.model.response.ShortProductDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Product", description = "the product API")
public interface ProductAPI {
    @Operation(summary = "Get all products", description = "Returns page of products.")
    @ApiResponse(responseCode = "200", description = "Successful request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class, subTypes = {ShortProductDTO.class})))
    @RequestMapping(value = "/products", produces = {"application/json"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    Page<ShortProductDTO> getProducts(@ParameterObject Pageable pageable);

    @Operation(summary = "Get product", description = "Returns product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShortProductDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)})
    @RequestMapping(value = "/admin/products/{product-id}", produces = {"application/json"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    ShortProductDTO getProduct(
            @Parameter(description = "ID of product", required = true)
            @PathVariable("product-id") Long productId,
            @Parameter(description = "Authentication", required = true)
            Authentication auth);

    @Operation(summary = "Update product", description = "Returns updated product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShortProductDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "You are not admin", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/admin/products/{product-id}")
    ShortProductDTO updateProduct(
            @Parameter(description = "ID of product", required = true)
            @PathVariable("product-id") final Long productId,
            @Parameter(description = "Product with changes", required = true)
            @RequestBody final CreateProduct product,
            @Parameter(description = "Authentication", required = true)
            Authentication auth);

    @Operation(summary = "Delete product", description = "Deletes product by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "You are not admin", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/admin/products/{product-id}")
    void deleteProduct(
            @Parameter(description = "ID of product", required = true)
            @PathVariable("product-id") final Long productId,
            @Parameter(description = "Authentication", required = true)
            Authentication auth);

    @Operation(summary = "Create product", description = "Returns created product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShortProductDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/products")
    ShortProductDTO createProduct(
            @Parameter(description = "Authentication", required = true)
            Authentication auth,
            @Parameter(description = "New product", required = true)
            @RequestBody final CreateProduct product);

    @Operation(summary = "Upload image", description = "Uploads and saves image of product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful request", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "405", description = "You are not admin", content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/admin/products/{product-id}/upload")
    void uploadImage(
            @Parameter(description = "ID of product", required = true)
            @PathVariable("product-id") Long productId,
            @Parameter(description = "Image of product (Multipart file)", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Authentication", required = true)
            Authentication auth);
}
