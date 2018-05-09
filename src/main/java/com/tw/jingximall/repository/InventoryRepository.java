package com.tw.jingximall.repository;

import com.tw.jingximall.entity.Inventory;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    //创建库存
    @Modifying
    @Transactional
    @Query(value = "insert into Inventory(id,count,lockedCount) values(?1,0,0)",nativeQuery = true)
    int saveByProductId(Integer id);

}
