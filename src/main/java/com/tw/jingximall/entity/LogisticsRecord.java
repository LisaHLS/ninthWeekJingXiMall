package com.tw.jingximall.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="LogisticsRecord")
public class LogisticsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer orderId;

    private String logisticsStatus;

    private String outboundTime;

    private String signedTime;

    private String deliveryMan;

    @JsonIgnore
    @OneToOne(targetEntity = OrderInfo.class)
    @JoinColumn(name = "orderId", insertable = false, updatable = false)
    private OrderInfo order;

    public LogisticsRecord(Integer orderId, String logisticsStatus, String outboundTime,
        String signedTime, String deliveryMan) {
        this.orderId = orderId;
        this.logisticsStatus = logisticsStatus;
        this.outboundTime = outboundTime;
        this.signedTime = signedTime;
        this.deliveryMan = deliveryMan;
    }
}
