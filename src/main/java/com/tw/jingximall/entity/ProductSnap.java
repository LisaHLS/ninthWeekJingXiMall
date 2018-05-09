package com.tw.jingximall.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSnap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productId;

    private Integer orderId;

    private String productName;

    private String productDescription;

    private Integer purchasePrice;

    private Integer purchaseCount;

    @ManyToOne(targetEntity = Order.class)
    @JoinColumn(name ="orderId", insertable = false, updatable = false)
    private Order orderItem;

}
