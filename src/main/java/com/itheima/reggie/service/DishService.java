package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import org.springframework.stereotype.Service;

/**
 * @author coldwind
 * @version 1.0
 */
@Service
public interface DishService extends IService<Dish> {

    //新增菜品，同时插入对应的口味数据
    void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品和口味信息
    DishDto getByIdWithFlavor(Long id);
    //更新菜品及口味信息
    void updateWithFlavor(DishDto dishDto);
}
