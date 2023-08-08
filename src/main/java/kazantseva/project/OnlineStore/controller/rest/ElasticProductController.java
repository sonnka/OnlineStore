package kazantseva.project.OnlineStore.controller.rest;

import kazantseva.project.OnlineStore.service.ElasticProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ElasticProductController {

    private ElasticProductService elasticProductService;

    @GetMapping("/elastic/transfer")
    public void allProductsToElastic() {
        elasticProductService.transferAllProducts();
    }

}
