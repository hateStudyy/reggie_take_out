package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.core.annotation.Order;

/**
 * @author coldwind
 * @version 1.0
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
