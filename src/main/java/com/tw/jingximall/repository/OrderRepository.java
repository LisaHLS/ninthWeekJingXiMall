package com.tw.jingximall.repository;

import com.tw.jingximall.entity.Order;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Order findUserOrderById(Integer id);

    @Modifying
    @Transactional
    @Query("update Order o set o.status = ?2, o.paidTime = ?3 where o.id = ?1")
    int updateOrderStatusWithPaid(Integer id, String status, String paidTime);

    @Modifying
    @Transactional
    @Query("update Order o set o.status = ?2, o.withdrawnTime = ?3 where o.id = ?1")
    int updateOrderStatusToWithdrawn(Integer id, String status, String withdrawnTime);

    @Modifying
    @Transactional
    @Query("update Order o set o.status = ?2, o.finishTime = ?3 where o.id = ?1")
    int updateOrderStatusToFinished(Integer id, String status, String finishTime);

    List<Order> findByUserId(Integer userId);
}
