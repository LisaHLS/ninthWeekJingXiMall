package com.tw.jingximall.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToOne(targetEntity = OrderInfo.class)
    @JoinColumn(name ="orderId", insertable = false, updatable = false)
    private OrderInfo orderItem;

    public ProductSnap(Integer productId, Integer orderId, String productName,
        String productDescription, Integer purchasePrice, Integer purchaseCount) {
        this.productId = productId;
        this.orderId = orderId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.purchasePrice = purchasePrice;
        this.purchaseCount = purchaseCount;
    }
}
