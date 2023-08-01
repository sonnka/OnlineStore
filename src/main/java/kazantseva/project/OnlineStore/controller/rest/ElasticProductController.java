package kazantseva.project.OnlineStore.controller.rest;

import kazantseva.project.OnlineStore.model.elasticSearch.ElasticProduct;
import kazantseva.project.OnlineStore.service.ElasticProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class ElasticProductController {

    private ElasticProductService elasticProductService;

    @GetMapping("/elastic/products")
    public List<ElasticProduct> getAllProducts() {
        return elasticProductService.getAllProducts();
    }

    @PostMapping("/elastic/products")
    public ElasticProduct addProduct(@RequestBody ElasticProduct product) {
        return elasticProductService.insertProduct(product);
    }

}
