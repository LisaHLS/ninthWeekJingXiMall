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

    //修改商品信息
    @Transactional
    @Modifying
    @Query("update Product set name = ?2, description = ?3, price = ?4 where id = ?1")
    int update(Integer id, String name, String description, Integer price);

    //根据商品id查找商品
    Product findProductById(Integer id);

    //查找所有商品
    List<Product> findAll();

    //根据name查询
    List<Product> findByName(String name);

    //根据name和描述模糊查询
    List<Product> findByNameAndDescriptionContaining(String name, String description);

}
