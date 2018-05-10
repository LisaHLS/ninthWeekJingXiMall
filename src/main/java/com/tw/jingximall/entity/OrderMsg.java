package com.tw.jingximall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMsg {

    private Integer productId;

    private Integer purchaseCount;

}
