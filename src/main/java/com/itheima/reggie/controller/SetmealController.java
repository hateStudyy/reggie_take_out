package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 套餐管理
 * @author coldwind
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)// allEntries 删除分类下所有的缓存数据
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息: {}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true),
            @ApiImplicitParam(name = "name",value = "套餐名称",required = false)
    })
    public R<Page> page(int page,int pageSize,String name) {
        //分页构造前期
        Page<Setmeal> pageInfo = new Page<>();
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null, Setmeal::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行查询
        setmealService.page(pageInfo, queryWrapper);

        //拷贝对象
        BeanUtils.copyProperties(pageInfo, setmealDtoPage,"records");//不需要拷贝records因为泛型不一样
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)// allEntries 删除分类下所有的缓存数据
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids: {}",ids);

        setmealService.removeWithDish(ids);

        return R.success("套餐删除成功！");
    }

    /**
     * 根据条件，查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    //使用spring cache 注解编写
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        //自己实现套餐的redis代码
        //dto对象
        List<Setmeal> list = null;
//
//        String key = "setmeal_" +setmeal.getCategoryId() + "_" +setmeal.getStatus();
//
//        //先从redis中获取缓存数据
//        list = (List<Setmeal>) redisTemplate.opsForValue().get(key);
//
//        if(list != null) {
//            //缓存中存在，直接返回 无需查询数据库
//            return R.success(list);
//        }
        //如果不存在，需要查询数据库

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(setmeal.getCategoryId()!= null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        list = setmealService.list(queryWrapper);

//        //查完后，把数据缓存到redis
//        redisTemplate.opsForValue().set(key, list,60, TimeUnit.MINUTES);
        return R.success(list);
    }

    /**
     * 2023/5/25新增
     * 修改套餐数据
     * 根据条件回表查询
     */
    @GetMapping("/{id}")
    @CacheEvict(value = "setmealCache", allEntries = true)// allEntries 删除分类下所有的缓存数据
    public R<Setmeal> getById(@PathVariable Long id){
        Setmeal setmeal = setmealService.getById(id);
        return R.success(setmeal);
        //修改后需要删除缓存中的数据
    }
}
