package com.tw.jingximall.repository;

import com.tw.jingximall.entity.LogisticsRecord;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogisticsRecordRepository extends JpaRepository<LogisticsRecord, Integer> {

    LogisticsRecord findLogisticsRecordById(Integer id);

    LogisticsRecord findLogisticsRecordByIdAndOrderId(Integer id, Integer orderId);

    @Modifying
    @Transactional
    @Query("update LogisticsRecord l set l.logisticsStatus = 'shipping', l.outboundTime = ?3 where l.id = ?1 and l.orderId = ?2")
    int updateLogisticsStatusWithShipping(Integer id, Integer orderId, String outboundTime);

    @Modifying
    @Transactional
    @Query("update LogisticsRecord l set l.logisticsStatus = 'signed', l.signedTime = ?3 where l.id = ?1 and l.orderId = ?2")
    int updateLogisticsStatusWithSigned(Integer id, Integer orderId, String signedTime);
}
