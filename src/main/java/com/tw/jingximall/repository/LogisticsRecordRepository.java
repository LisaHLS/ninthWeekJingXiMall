package com.tw.jingximall.repository;

import com.tw.jingximall.entity.LogisticsRecord;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogisticsRecordRepository extends JpaRepository<LogisticsRecord, Integer> {

    //根据物流订单Id查询订单
    LogisticsRecord findLogisticsRecordById(Integer id);

    //根据物流Id和订单id查询订单
    LogisticsRecord findLogisticsRecordByIdAndOrderId(Integer id, Integer orderId);

    //修改物流发货状态
    @Modifying
    @Transactional
    @Query("update LogisticsRecord l set l.logisticsStatus = 'shipping', l.outboundTime = ?3 where l.id = ?1 and l.orderId = ?2")
    int updateLogisticsStatusWithShipping(Integer id, Integer orderId, String outboundTime);

    @Modifying
    @Transactional
    @Query("update LogisticsRecord l set l.logisticsStatus = 'signed', l.signedTime = ?3 where l.id = ?1 and l.orderId = ?2")
    int updateLogisticsStatusWithSigned(Integer id, Integer orderId, String signedTime);
}
