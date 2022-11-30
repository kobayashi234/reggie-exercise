package com.demo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.reggie.common.CustomException;
import com.demo.reggie.mapper.SetmealMapper;
import com.demo.reggie.pojo.Setmeal;
import com.demo.reggie.pojo.SetmealDish;
import com.demo.reggie.dto.SetmealDto;
import com.demo.reggie.service.SetmealDishService;
import com.demo.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，操作seteaml表
        this.save(setmealDto);
        //将菜品和套餐关联
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        //保存套餐和菜品的关联信息，操作setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //根据id和在售状态查询
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        long count = this.count(queryWrapper);
        //如果套餐是在售状态，抛出自定义异常
        if(count > 0){
            throw new CustomException("该套餐正在售卖中，不能删除！");
        }
        //停售状态可以删除
        this.removeByIds(ids);
        //删除关系表中的数据——setmeal_dish
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        //删除
        setmealDishService.remove(queryWrapper1);
    }

    @Override
    @Transactional
    public void status(List<Long> ids, int type) {
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Setmeal::getStatus, type);
        updateWrapper.in(Setmeal::getId, ids);
        this.update(updateWrapper);
    }

    @Override
    public SetmealDto getSetmealDto(Long id) {
        //获得该套餐基本信息
        Setmeal setmeal = this.getById(id);
        //获得该套餐的菜品数据集合
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        //封装成dto传输对象
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        setmealDto.setSetmealDishes(setmealDishList);

        return setmealDto;
    }

    @Override
    public void updateSetmealWithDish(SetmealDto setmealDto) {
        this.removeById(setmealDto.getId());
        //删除关系表中的数据——setmeal_dish
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, setmealDto.getId());
        //删除
        setmealDishService.remove(queryWrapper1);

        //再添加套餐，直接调用新增方法
        saveWithDish(setmealDto);
    }
}
