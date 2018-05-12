package com.tw.jingximall.controller;

import com.tw.jingximall.entity.LogisticsRecord;
import com.tw.jingximall.entity.ProductSnap;
import com.tw.jingximall.exception.NotFoundException;
import com.tw.jingximall.exception.StatusConflictException;
import com.tw.jingximall.repository.InventoryRepository;
import com.tw.jingximall.repository.LogisticsRecordRepository;
import com.tw.jingximall.repository.OrderRepository;
import com.tw.jingximall.repository.ProductSnapRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping("/logisticsRecords")
public class LogisticsRecordController {

    @Autowired
    private LogisticsRecordRepository logisticsRecordRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductSnapRepository productSnapRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public LogisticsRecord getLogisticsRecordById(@PathVariable Integer id) {

        LogisticsRecord logisticsRecord = logisticsRecordRepository.findLogisticsRecordById(id);
        if (logisticsRecord == null) throw new NotFoundException("logisticsRecord", id);
        return logisticsRecord;
    }

    @RequestMapping(value = "/{id}/orders/{orderId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLogisticsRecordStatus(@PathVariable Integer id, @PathVariable Integer orderId, @RequestParam String logisticsStatus) {

        LogisticsRecord logisticsRecord = logisticsRecordRepository.findLogisticsRecordByIdAndOrderId(id, orderId);

        if (logisticsRecord == null) throw new NotFoundException("logisticsRecord", id);

        boolean canShip = logisticsRecord.getLogisticsStatus().equals("readyToShip") && logisticsStatus.equals("shipping");
        boolean canSign = logisticsRecord.getLogisticsStatus().equals("shipping") && logisticsStatus.equals("signed");

        if(!canShip && !canSign) throw new StatusConflictException("order", id, logisticsRecord.getLogisticsStatus());

        String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if(canShip) logisticsRecordRepository.updateLogisticsStatusWithShipping(id, orderId, nowDate);

        if(canSign) {
            logisticsRecordRepository.updateLogisticsStatusWithSigned(id, orderId, nowDate);
            updateOrderStatusAndInventories(orderId, nowDate);
        }
    }

    private void updateOrderStatusAndInventories(Integer orderId, String nowDate) {

        orderRepository.updateOrderStatusToFinished(orderId, "finished", nowDate);

        List<ProductSnap> productSnaps = productSnapRepository.findProductSnapByOrderId(orderId);
        for (ProductSnap snap : productSnaps) {
            inventoryRepository.updateCountById(snap.getProductId(), -snap.getPurchaseCount());
            inventoryRepository.updateLockedCount(snap.getProductId(), -snap.getPurchaseCount());
        }
    }
}
