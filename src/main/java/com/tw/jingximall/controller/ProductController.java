package com.tw.jingximall.controller;

import com.tw.jingximall.entity.Product;
import com.tw.jingximall.repository.InventoryRepository;
import com.tw.jingximall.repository.ProductRepository;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateProduct(@PathVariable Integer id, @RequestBody Product product) throws Exception {

        if (productRepository.findProductById(id) == null) {
            return new ResponseEntity<>("there is no such product with input id .", HttpStatus.NOT_FOUND);
        }

        productRepository.update(id, product.getName(), product.getDescription(), product.getPrice());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getProduct(@PathVariable Integer id) {

        Product product = productRepository.findProductById(id);
        if (product == null) {
            return new ResponseEntity<String>("there is no such product with input id.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @GetMapping
    public List<Product> getProducts(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "description", required = false) String description) {

        if(!StringUtils.isEmpty(name) && !StringUtils.isEmpty(description)) return productRepository.findByNameAndDescriptionContaining(name, description);

        if(!StringUtils.isEmpty(name)) return productRepository.findByName(name);

        return productRepository.findAll();
    }
}
