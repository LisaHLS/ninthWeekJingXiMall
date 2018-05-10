package com.tw.jingximall.repository;

import com.tw.jingximall.entity.Product;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Transactional
    @Modifying
    @Query("update Product set name = ?2, description = ?3, price = ?4 where id = ?1")
    int update(Integer id, String name, String description, Integer price);

    Product findProductById(Integer id);

    List<Product> findAll();

    List<Product> findByName(String name);

    List<Product> findByNameAndDescriptionContaining(String name, String description);
}
