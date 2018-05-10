package com.tw.jingximall.controller;

import com.tw.jingximall.entity.Inventory;
import com.tw.jingximall.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping("/inventories")
public class InventoryController {

    @Autowired
    InventoryRepository inventoryRepository;

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateInventory(@PathVariable Integer id, @RequestBody Inventory inventory) throws Exception {

        if (inventoryRepository.findInventoryById(id) == null) {
            return new ResponseEntity<>("there is no such product with input id .", HttpStatus.NOT_FOUND);
        }

        inventoryRepository.updateCountById(id, inventory.getCount());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
