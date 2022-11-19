package com.demo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.reggie.common.R;
import com.demo.reggie.dto.DishDto;
import com.demo.reggie.pojo.Category;
import com.demo.reggie.pojo.Dish;
import com.demo.reggie.pojo.DishFlavor;
import com.demo.reggie.service.CategoryService;
import com.demo.reggie.service.DishFlavorService;
import com.demo.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);
        return R.success("添加菜品成功");
    }

    /**
     * 修改菜品
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);
        return R.success("添加菜品成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids){
        dishService.deleteById(ids);
        return R.success("删除成功");
    }

    /**
     * 分页显示菜品列表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据菜品名称进行模糊查询
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, queryWrapper);
        //将除结果集之外的数据复制到dishDto
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();//查询的结果集数据
        List<DishDto> dishDtos = new ArrayList<>();

        //遍历为dishDto的categoryName赋值
        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record, dishDto);//复制
            Long categoryId = record.getCategoryId();//获得分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            //将分类对象的name属性值赋值给dishDto
            if(category != null)
                dishDto.setCategoryName(category.getName());
            //存入集合中
            dishDtos.add(dishDto);
        }
        //将集合赋值给分页对象
        dishDtoPage.setRecords(dishDtos);
        return R.success(dishDtoPage);
    }

    /**
     * 修改菜品之前的回显菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishInfo(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品状态
     * @param type
     * @param ids
     * @return
     */
    @PostMapping("/status/{type}")
    public R<String> status(@PathVariable("type") Integer type, @RequestParam("ids") List<Long> ids){
        dishService.updateStatus(type, ids);
        return R.success("状态修改成功");
    }

    /**
     * 根据分类id获得属于该分类下的菜品信息集合。
     * 复用手机端分类下的菜品与口味列表展示
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据菜品名称模糊查询
        queryWrapper.like(dish.getName() != null, Dish::getName, dish.getName());
        //根据分类id查询
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //查询状态为1(起售状态)的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //菜品数据集合
        List<Dish> records = dishService.list(queryWrapper);
        //将分类名称和口味数据绑定到对应菜品上
        List<DishDto> dishDtos = new ArrayList<>();
        //遍历赋值
        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record, dishDto);//复制
            Long categoryId = record.getCategoryId();//获得分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            //将分类对象的name分类名称赋值给dishDto
            if(category != null)
                dishDto.setCategoryName(category.getName());
            //绑定口味数据
            Long dishID = record.getId();//获得菜品id
            LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
            flavorQueryWrapper.eq(DishFlavor::getDishId, dishID);
            List<DishFlavor> dishFlavors = dishFlavorService.list(flavorQueryWrapper);
            dishDto.setFlavors(dishFlavors);
            //存入集合中
            dishDtos.add(dishDto);
        }

        return R.success(dishDtos);
    }
}
