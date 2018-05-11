package com.tw.jingximall.controller;

import com.tw.jingximall.entity.LogisticsRecord;
import com.tw.jingximall.entity.OrderInfo;
import com.tw.jingximall.entity.OrderMsg;
import com.tw.jingximall.entity.Product;
import com.tw.jingximall.entity.ProductSnap;
import com.tw.jingximall.repository.InventoryRepository;
import com.tw.jingximall.repository.LogisticsRecordRepository;
import com.tw.jingximall.repository.OrderRepository;
import com.tw.jingximall.repository.ProductRepository;
import com.tw.jingximall.repository.ProductSnapRepository;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private LogisticsRecordRepository logisticsRecordRepository;

    @PostMapping
    public ResponseEntity<?> saveOrder(@RequestBody List<OrderMsg> orderMsgList) {


        OrderInfo order = orderRepository.save(initOrderInfo());
        Set<ProductSnap> purchaseItemList = initProductSnapSet(orderMsgList, order.getId());
        if(purchaseItemList.size() < orderMsgList.size()) {
            return new ResponseEntity<String>("please check if some product exist and inventory enough", HttpStatus.BAD_REQUEST);
        }
//        OrderInfo order = initOrderInfo();

        order.setPurchaseItemList(purchaseItemList);
        order.setLogisticsInformation(initLogisticsRecord(order.getId()));

//        OrderInfo order = orderRepository.saveAndFlush(initOrderInfo());
//        boolean createProductSnapsSuccess = createProductSnaps(orderMsgList, order.getId());
//
//        if (!createProductSnapsSuccess) {
//            return new ResponseEntity<String>("please check if product exist and inventory enough", HttpStatus.BAD_REQUEST);
//        }
//        HttpHeaders responseHeaders = setLocationInHeaders(order.getId());

        order.setTotalPrice(countTotalPrice(purchaseItemList));
        order = orderRepository.saveAndFlush(order);

        lockInventories(orderMsgList);

        HttpHeaders responseHeaders = setLocationInHeaders(order.getId());
        return new ResponseEntity<OrderInfo>(order, responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer id, @RequestParam(value = "orderStatus", required = false) String orderStatus) {

        OrderInfo order = orderRepository.findOrderInfoById(id);
        if (order == null) {
            return new ResponseEntity<String>("the order with this id do not exist.", HttpStatus.NOT_FOUND);
        }

        boolean canPay = order.getStatus().equals("unPaid") && orderStatus.equals("paid");
        boolean canWithdraw = order.getStatus().equals("paid") && orderStatus.equals("withdrawn");
        if(!canPay && !canWithdraw) return new ResponseEntity<String>("The order has already been " + order.getStatus(), HttpStatus.BAD_REQUEST);

        String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if(canPay) orderRepository.updateOrderStatusWithPaid(id, orderStatus, nowDate);
        if(canWithdraw) {
            orderRepository.updateOrderStatusToWithdrawn(id, orderStatus, nowDate);
            unlockInventoriesByOrderId(id);
        }

//        if(canPay) createLogisticsRecord(id);
//
//        if(canWithdraw) unlockInventoriesByOrderId(id);
//
//        updateOrderStatusByInputState(id, orderStatus);

        return new ResponseEntity<OrderInfo>(orderRepository.findOrderInfoById(id), HttpStatus.NO_CONTENT);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderByOrderId(@PathVariable Integer id) {

        OrderInfo order = orderRepository.findOrderInfoById(id);
        if (order == null) return new ResponseEntity<String>("the order with this id do not exist.", HttpStatus.NOT_FOUND);

        order.setPurchaseItemList(new HashSet(productSnapRepository.findProductSnapByOrderId(order.getId())));
        return new ResponseEntity<OrderInfo>(order, HttpStatus.OK);
    }

    @GetMapping
    public List<OrderInfo> getOrdersByUserId(@RequestParam Integer userId) {
        return orderRepository.findOrderInfoByUserId(userId);
    }

    private OrderInfo initOrderInfo(){
        OrderInfo order = new OrderInfo();
        order.setTotalPrice("0");
        order.setStatus("unPaid");
        order.setUserId(1);
        order.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return order;
    }

    private Set<ProductSnap> initProductSnapSet(List<OrderMsg> orderMsgList, Integer orderId) {
        Set<ProductSnap> productSnapSet = new HashSet<>();
        for (OrderMsg msg : orderMsgList) {
            Product product = productRepository.findProductById(msg.getProductId());

            if(null != product && product.getInventory().getCount() - product.getInventory().getLockedCount() >= msg.getPurchaseCount()) {
                ProductSnap snap = new ProductSnap();
                snap.setOrderId(orderId);
                snap.setProductId(product.getId());
                snap.setProductName(product.getName());
                snap.setProductDescription(product.getDescription());
                snap.setPurchasePrice(product.getPrice());
                snap.setPurchaseCount(msg.getPurchaseCount());

                productSnapSet.add(snap);
            }
        }
        return productSnapSet;
    }

    private LogisticsRecord initLogisticsRecord(Integer orderId) {
        LogisticsRecord logisticsRecord = new LogisticsRecord();
        logisticsRecord.setOrderId(orderId);
        logisticsRecord.setLogisticsStatus("readyToShip");
        logisticsRecord.setOutboundTime("null");
        logisticsRecord.setSignedTime("null");
        logisticsRecord.setDeliveryMan("李师傅");
        return logisticsRecord;
    }

//    private void updateOrderStatusByInputState(Integer id, String orderStatus) {
//
//        String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//        if (orderStatus.equals("paid")) orderRepository.updateOrderStatusWithPaid(id, orderStatus, nowDate);
//        if (orderStatus.equals("withdrawn")) orderRepository.updateOrderStatusToWithdrawn(id, orderStatus, nowDate);
//    }

    private HttpHeaders setLocationInHeaders(Integer orderId) {
        URI location = URI.create("http://192.168.56.1:8083/orders/" + orderId);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return responseHeaders;
    }

//    private boolean createProductSnaps(List<OrderMsg> orderMsgList, Integer orderId) {
//        for (OrderMsg msg : orderMsgList) {
//            Product product = productRepository.findProductById(msg.getProductId());
//            if (product == null) return false;
//
//            Inventory inventory = inventoryRepository.findInventoryById(msg.getProductId());
//            if (inventory.getCount() - inventory.getLockedCount() < msg.getPurchaseCount()) return false;
//
//            ProductSnap productSnap = new ProductSnap(product.getId(), orderId, product.getName(), product.getDescription(), product.getPrice(), msg.getPurchaseCount());
//            productSnapRepository.saveAndFlush(productSnap);
//        }
//        return true;
//    }

    private String countTotalPrice(Set<ProductSnap> productSnapSet) {
        int totalPrice = 0;
        for (ProductSnap snap : productSnapSet) {
            totalPrice += snap.getPurchasePrice() * snap.getPurchaseCount();
        }
        return String.valueOf(totalPrice);
    }

//    private String countTotalPrice(Integer orderId) {
//        List<ProductSnap> snaps = productSnapRepository.findProductSnapByOrderId(orderId);
//        int totalPrice = 0;
//        for (ProductSnap snap : snaps) {
//            totalPrice += snap.getPurchasePrice() * snap.getPurchaseCount();
//        }
//        return String.valueOf(totalPrice);
//    }

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

//    private void createLogisticsRecord(Integer orderId) {
//        LogisticsRecord logisticsRecord = new LogisticsRecord();
//        logisticsRecord.setOrderId(orderId);
//        logisticsRecord.setLogisticsStatus("readyToShip");
//        logisticsRecord.setOutboundTime("null");
//        logisticsRecord.setSignedTime("null");
//        logisticsRecord.setDeliveryMan("李师傅");
//        logisticsRecordRepository.save(logisticsRecord);
//    }

}
