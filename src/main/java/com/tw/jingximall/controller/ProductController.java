package com.tw.jingximall.controller;

import com.tw.jingximall.entity.Product;
import com.tw.jingximall.repository.InventoryRepository;
import com.tw.jingximall.repository.ProductRepository;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @PostMapping
    public ResponseEntity<?> saveProduct(@RequestBody Product product) {

        if (product.getName() == null || product.getPrice() == null) {

            return new ResponseEntity<String>("Input product invalid!", HttpStatus.BAD_REQUEST);

        }
        Integer id = productRepository.saveAndFlush(product).getId();
        URI location = URI.create("http://192.168.56.1:8083/products/" + id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        inventoryRepository.saveByProductId(id);
        return new ResponseEntity<Product>(productRepository.findProductById(id), responseHeaders, HttpStatus.CREATED);
    }


}
