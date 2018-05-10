package com.tw.jingximall.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private String totalPrice;

    private String status;

    private String createTime;

    private String finishTime;

    private String paidTime;

    private String withdrawnTime;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "orderItem")
    private Set<ProductSnap> purchaseItemList = new HashSet<>();

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "order")
    private LogisticsRecord logisticsInformation;


}
