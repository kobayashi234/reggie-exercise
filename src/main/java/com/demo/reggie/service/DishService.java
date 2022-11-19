package com.demo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.reggie.dto.DishDto;
import com.demo.reggie.pojo.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    /**
     * 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 查询dish信息和对应的Flavor信息，并封装成DishDto对象
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品和关联的口味表
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 修改菜品状态status
     * @param type
     * @param ids
     */
    void updateStatus(Integer type, List<Long> ids);

    /**
     * 删除和批量删除
     * @param ids
     */
    void deleteById(List<Long> ids);
}
