package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * @author coldwind
 * @version 1.0
 */
public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
