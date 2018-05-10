package com.tw.jingximall.repository;

import com.tw.jingximall.entity.ProductSnap;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSnapRepository extends JpaRepository<ProductSnap, Integer> {

    //根据订单id查找库存
    List<ProductSnap> findProductSnapByOrderId(Integer orderId);
}
