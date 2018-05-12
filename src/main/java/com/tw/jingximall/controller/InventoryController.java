package com.tw.jingximall.controller;

import com.tw.jingximall.entity.Inventory;
import com.tw.jingximall.exception.NotFoundException;
import com.tw.jingximall.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping("/inventories")
public class InventoryController {

    @Autowired
    InventoryRepository inventoryRepository;

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateInventory(@PathVariable Integer id, @RequestBody Inventory inventory) throws Exception {

        if (inventoryRepository.findInventoryById(id) == null) {
            throw new NotFoundException("product", id);
        }
        inventoryRepository.updateCountById(id, inventory.getCount());
    }

}
