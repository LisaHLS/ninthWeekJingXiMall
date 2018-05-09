package com.tw.jingximall.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    private Integer id;

    private Integer count;

    private Integer lockedCount;

    @OneToOne(targetEntity = Product.class)
    @JoinColumn(name = "id", insertable = false, updatable = false)
    private Product product;

}
