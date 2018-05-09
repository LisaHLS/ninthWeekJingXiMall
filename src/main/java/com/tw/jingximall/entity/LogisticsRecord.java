package com.tw.jingximall.entity;

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
public class LogisticsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer orderId;

    private String logisticsStatus;

    private String outboundTime;

    private String signedTime;

    private String deliveryMan;

    @OneToOne(targetEntity = Order.class)
    @JoinColumn(name = "orderId", insertable = false, updatable = false)
    private Order order;

}
