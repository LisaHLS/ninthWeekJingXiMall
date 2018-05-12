package com.tw.jingximall.controller;

import com.tw.jingximall.entity.Inventory;
import com.tw.jingximall.entity.Product;
import com.tw.jingximall.exception.NotFoundException;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<?> saveProduct(@RequestBody Product product) {

        if (product.getName() == null || product.getPrice() == null) {
            return new ResponseEntity<String>("Input product invalid!", HttpStatus.BAD_REQUEST);
        }

        Inventory inventory = new Inventory();
        inventory.setCount(0);
        inventory.setLockedCount(0);
        product.setInventory(inventory);
        Product savedProduct = productRepository.saveAndFlush(product);

        URI location = URI.create("http://192.168.56.1:8083/products/" + savedProduct.getId());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return new ResponseEntity<Product>(savedProduct, responseHeaders, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(@PathVariable Integer id, @RequestBody Product product) throws Exception {

        if (productRepository.findProductById(id) == null) {
            throw new NotFoundException("product", id);
        }
        productRepository.update(id, product.getName(), product.getDescription(), product.getPrice());
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Product getProduct(@PathVariable Integer id) {

        Product product = productRepository.findProductById(id);
        if (product == null) throw new NotFoundException("product", id);
        return product;
    }

    @GetMapping
    public List<Product> getProducts(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "description", required = false) String description) {

        if(!StringUtils.isEmpty(name) && !StringUtils.isEmpty(description)) return productRepository.findByNameAndDescriptionContaining(name, description);

        if(!StringUtils.isEmpty(name)) return productRepository.findByName(name);

        return productRepository.findAll();
    }
}
