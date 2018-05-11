package com.tw.jingximall.repository;

import com.tw.jingximall.entity.OrderInfo;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderInfo, Integer> {

    OrderInfo findOrderInfoById(Integer id);

    @Modifying
    @Transactional
    @Query("update OrderInfo set status = ?2, paidTime = ?3 where id = ?1")
    int updateOrderStatusWithPaid(Integer id, String status, String paidTime);

    @Modifying
    @Transactional
    @Query("update OrderInfo set status = ?2, withdrawnTime = ?3 where id = ?1")
    int updateOrderStatusToWithdrawn(Integer id, String status, String withdrawnTime);

    @Modifying
    @Transactional
    @Query("update OrderInfo set status = ?2, finishTime = ?3 where id = ?1")
    int updateOrderStatusToFinished(Integer id, String status, String finishTime);

    List<OrderInfo> findOrderInfoByUserId(Integer userId);
}
