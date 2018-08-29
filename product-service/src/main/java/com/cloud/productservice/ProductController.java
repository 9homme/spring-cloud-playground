package com.cloud.productservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
@RequestMapping(value = "products")
public class ProductController {

	@Autowired
	private OAuth2RestTemplate  restTemplate;

    @GetMapping("/search")
    public Product getProductsByTypeAndName(@RequestParam(value = "sku") final String sku) {
        String url = "http://PRICING-SERVICE/products/price?sku=" + sku;
        return restTemplate.getForObject(url, Product.class);
    }

}

@Data
class Product {
    private String sku;
    private String price;
}