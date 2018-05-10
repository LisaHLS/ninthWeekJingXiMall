package com.tw.jingximall.repository;

import com.tw.jingximall.entity.Inventory;
import java.util.List;
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

    //修改库存数量
    @Modifying
    @Transactional
    @Query("update Inventory set count = count + ?2 where id = ?1")
    int updateCountById(Integer productId, Integer count);

    //修改锁定库存数量
    @Modifying
    @Transactional
    @Query("update Inventory set lockedCount = lockedCount + ?2 where id = ?1")
    int updateLockedCount(Integer productId, Integer lockedCount);

    //根据商品id查找库存
    Inventory findInventoryById(Integer productId);

    //查找所有库存
    List<Inventory> findAll();

}
