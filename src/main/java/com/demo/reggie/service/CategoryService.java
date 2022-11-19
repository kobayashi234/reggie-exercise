package com.demo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.reggie.pojo.Category;

public interface CategoryService extends IService<Category> {

    /**
     * 根据id删除分类，但是要先进行判断该分类是否关联其他菜品或套餐
     * @param id
     */
    void delete(Long id);
}
