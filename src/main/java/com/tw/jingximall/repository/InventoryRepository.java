package com.tw.jingximall.repository;

import com.tw.jingximall.entity.Inventory;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    @Modifying
    @Transactional
    @Query(value = "insert into Inventory(id,count,lockedCount) values(?1,0,0)",nativeQuery = true)
    int saveByProductId(Integer id);

    @Modifying
    @Transactional
    @Query("update Inventory i set i.count = i.count + ?2 where i.id = ?1")
    int updateCountById(Integer productId, Integer count);

    @Modifying
    @Transactional
    @Query("update Inventory i set i.lockedCount = i.lockedCount + ?2 where i.id = ?1")
    int updateLockedCount(Integer productId, Integer lockedCount);

    Inventory findInventoryById(Integer productId);

    List<Inventory> findAll();

}
