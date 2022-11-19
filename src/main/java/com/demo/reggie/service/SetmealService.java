package com.demo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.reggie.pojo.Setmeal;
import com.demo.reggie.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 保存新增套餐，保存到setmeal表和steaml_dish表
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐和关联的菜品数据
     * @param ids
     */
    void removeWithDish(List<Long> ids);

    /**
     * 修改套餐停售状态
     * @param ids
     */
    void status(List<Long> ids, int type);

    /**
     * 根据id回显SetmealDto信息
     * @param id
     */
    SetmealDto getSetmealDto(Long id);

    /**
     * 修改套餐基本信息和关联的菜品数据
     * @param setmealDto
     */
    void updateSetmealWithDish(SetmealDto setmealDto);
}
