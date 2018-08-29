package com.cloud.priceservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
@RequestMapping("products/price")
public class PriceController {
	String productPrice="200";

    @GetMapping
    public Price getPrice(@RequestParam(value = "sku") final String sku) {
        Price price = new Price();
        price.setSku(sku);
        price.setPrice(productPrice);
        return price;
    }
}

@Data
class Price {
    private String sku;
    private String price;
}
