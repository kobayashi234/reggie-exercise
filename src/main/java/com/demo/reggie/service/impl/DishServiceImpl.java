package com.demo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.reggie.dto.DishDto;
import com.demo.reggie.mapper.DishMapper;
import com.demo.reggie.pojo.Dish;
import com.demo.reggie.pojo.DishFlavor;
import com.demo.reggie.service.DishFlavorService;
import com.demo.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //将dishDto部分数据保存到dish菜品表
        this.save(dishDto);
        Long id = dishDto.getId();//保存后获取自动生成的id

        List<DishFlavor> flavors = dishDto.getFlavors();
        //将菜品id和所有的口味对象绑定
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }
        //保存数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询dish信息
        Dish dish = this.getById(id);
        //查询Flavor信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper);
        //复制数据
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavorList);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);
        //清理当前菜品对应的口味数据
        LambdaUpdateWrapper<DishFlavor> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(updateWrapper);
        //添加提交的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        //将菜品id和所有的口味对象绑定
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void updateStatus(Integer type, List<Long> ids) {
        //根据id修改状态
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus, type);
        updateWrapper.in(Dish::getId, ids);
        this.update(updateWrapper);

    }

    @Override
    @Transactional
    public void deleteById(List<Long> ids) {
        //删除菜品
        this.removeByIds(ids);
        //删除口味
        LambdaUpdateWrapper<DishFlavor> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(updateWrapper);
    }


}
