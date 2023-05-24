package com.itheima.reggie.controller;

import com.itheima.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author coldwind
 * @version 1.0
 */
@RestController
@RequestMapping("/orderdetail")
@Slf4j
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;
}
