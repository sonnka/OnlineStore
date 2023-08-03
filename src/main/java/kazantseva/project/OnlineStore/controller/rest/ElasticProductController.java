package kazantseva.project.OnlineStore.controller.rest;

import kazantseva.project.OnlineStore.model.elasticSearch.entity.ElasticProduct;
import kazantseva.project.OnlineStore.service.ElasticProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ElasticProductController {

    private ElasticProductService elasticProductService;

    @GetMapping("/elastic/products")
    public Page<ElasticProduct> getAllProducts(Pageable pageable) {
        return elasticProductService.findAll(pageable);
    }

    @GetMapping("/elastic/transfer")
    public void allProductsToElastic() {
        elasticProductService.transferAllProducts();
    }

}
