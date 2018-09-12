package com.cloud.priceservice;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
@RequestMapping("price")
public class PriceController {
	String productPrice="100";

	Logger logger = LoggerFactory.getLogger(PriceController.class);
	
    @GetMapping
    public Price getPrice(@RequestParam(value = "sku") final String sku, Principal user) {
        Price price = new Price();
        price.setSku(sku);
        price.setPrice(productPrice);
        
        logger.info("Price requested by user====>"+user.getName());
        
        return price;
    }
}

@Data
class Price {
    private String sku;
    private String price;
}
