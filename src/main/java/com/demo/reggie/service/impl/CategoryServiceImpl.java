package com.demo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.reggie.common.CustomException;
import com.demo.reggie.mapper.CategoryMapper;
import com.demo.reggie.pojo.Category;
import com.demo.reggie.pojo.Dish;
import com.demo.reggie.pojo.Setmeal;
import com.demo.reggie.service.CategoryService;
import com.demo.reggie.service.DishService;
import com.demo.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void delete(Long id) {
        //查询该分类是否关联了菜品，是则抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishLambdaQueryWrapper);
        //已经关联菜品，抛出异常
        if(count > 0){
            throw new CustomException("当前分类关联了菜品，不能删除");
        }
        //查询该分类是否关联了套餐，是则抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        //已经关联套餐，抛出异常
        if (count1 > 0){
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        //没有关联，可以删除
        super.removeById(id);
    }
}
