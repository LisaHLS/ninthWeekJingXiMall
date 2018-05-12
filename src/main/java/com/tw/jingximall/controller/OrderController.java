package com.tw.jingximall.controller;

import com.tw.jingximall.entity.LogisticsRecord;
import com.tw.jingximall.entity.OrderInfo;
import com.tw.jingximall.entity.OrderMsg;
import com.tw.jingximall.entity.Product;
import com.tw.jingximall.entity.ProductSnap;
import com.tw.jingximall.exception.NotFoundException;
import com.tw.jingximall.exception.StatusConflictException;
import com.tw.jingximall.repository.InventoryRepository;
import com.tw.jingximall.repository.OrderRepository;
import com.tw.jingximall.repository.ProductRepository;
import com.tw.jingximall.repository.ProductSnapRepository;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductSnapRepository productSnapRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @PostMapping
    public ResponseEntity<?> saveOrder(@RequestBody List<OrderMsg> orderMsgList) {

        OrderInfo order = orderRepository.save(new OrderInfo(1,"0","unPaid",new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
        Set<ProductSnap> purchaseItemList = initProductSnapSet(orderMsgList, order.getId());
        if(purchaseItemList.size() < orderMsgList.size()) {
            return new ResponseEntity<String>("please check if some product exist and inventory enough", HttpStatus.BAD_REQUEST);
        }

        order.setPurchaseItemList(purchaseItemList);
        order.setLogisticsInformation(new LogisticsRecord(order.getId(), "readyToShip", "null", "null","李师傅"));

        order.setTotalPrice(countTotalPrice(purchaseItemList));
        order = orderRepository.saveAndFlush(order);

        lockInventories(orderMsgList);

        HttpHeaders responseHeaders = setLocationInHeaders(order.getId());
        return new ResponseEntity<OrderInfo>(order, responseHeaders, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public OrderInfo updateOrderStatus(@PathVariable Integer id, @RequestParam(value = "orderStatus", required = false) String orderStatus) {

        OrderInfo order = orderRepository.findOrderInfoById(id);
        if (order == null) throw new NotFoundException("order", id);

        boolean canPay = order.getStatus().equals("unPaid") && orderStatus.equals("paid");
        boolean canWithdraw = order.getStatus().equals("paid") && orderStatus.equals("withdrawn");

        if(!canPay && !canWithdraw) throw new StatusConflictException("order", id, order.getStatus());

        String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if(canPay) orderRepository.updateOrderStatusWithPaid(id, orderStatus, nowDate);

        if(canWithdraw) {
            orderRepository.updateOrderStatusToWithdrawn(id, orderStatus, nowDate);
            unlockInventoriesByOrderId(id);
        }

        return orderRepository.findOrderInfoById(id);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String,Object> getOrderByOrderId(@PathVariable Integer id) {

        OrderInfo order = orderRepository.findOrderInfoById(id);
        if (order == null) throw new NotFoundException("order", id);
        Map<String,Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("totalPrice", order.getTotalPrice());
        map.put("userId", order.getUserId());
        map.put("createTime", order.getCreateTime());
        map.put("purchaseItemList", productSnapRepository.findProductSnapByOrderId(order.getId()));
        return map;
    }

    @GetMapping
    public List<OrderInfo> getOrdersByUserId(@RequestParam Integer userId) {
        return orderRepository.findOrderInfoByUserId(userId);
    }

    private Set<ProductSnap> initProductSnapSet(List<OrderMsg> orderMsgList, Integer orderId) {
        Set<ProductSnap> productSnapSet = new HashSet<>();
        for (OrderMsg msg : orderMsgList) {
            Product product = productRepository.findProductById(msg.getProductId());

            if(null != product && product.getInventory().getCount() - product.getInventory().getLockedCount() >= msg.getPurchaseCount()) {
                productSnapSet.add(new ProductSnap(orderId, product.getId(), product.getName(), product.getDescription(), product.getPrice(), msg.getPurchaseCount()));
            }
        }
        return productSnapSet;
    }

    private HttpHeaders setLocationInHeaders(Integer orderId) {
        URI location = URI.create("http://192.168.56.1:8083/orders/" + orderId);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return responseHeaders;
    }

    private String countTotalPrice(Set<ProductSnap> productSnapSet) {
        int totalPrice = 0;
        for (ProductSnap snap : productSnapSet) {
            totalPrice += snap.getPurchasePrice() * snap.getPurchaseCount();
        }
        return String.valueOf(totalPrice);
    }

    private void lockInventories(List<OrderMsg> orderMsgList) {
        for (OrderMsg msg : orderMsgList) {
            inventoryRepository.updateLockedCount(msg.getProductId(), msg.getPurchaseCount());
        }
    }

    private void unlockInventoriesByOrderId(Integer orderId) {
        List<ProductSnap> snaps = productSnapRepository.findProductSnapByOrderId(orderId);
        for (ProductSnap snap : snaps) {
            inventoryRepository.updateLockedCount(snap.getId(), -snap.getPurchaseCount());
        }
    }

}
