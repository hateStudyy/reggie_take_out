package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author coldwind
 * @version 1.0
 */
@Service
public interface SetmealService extends IService<Setmeal> {
    /**
     * 保存菜品信息
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除菜品与套餐关联数据
     * @param ids
     */
    void removeWithDish(List<Long> ids);
}
