package org.hys.workshop.statemachine.service;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OnLineShopService implements PaymentService{

    private static final String SHOP_NAME = "MyShop";

    @Override
    public String pay(String paymentId) {

        String result = String.format("Payment in %s with payment id %s", SHOP_NAME, paymentId);
        log.info(result);
        return result;
    }

    @Override
    public String fulfill(String paymentId) {

        String result = String.format("Fulfill in %s with payment id %s", SHOP_NAME, paymentId);
        log.info(result);
        return result;
    }

    @Override
    public String cancel(String paymentId) {

        String result = String.format("Cancel in %s with payment id %s", SHOP_NAME, paymentId);
        log.info(result);
        return result;
    }
}
