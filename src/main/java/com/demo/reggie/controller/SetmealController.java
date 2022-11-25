package com.demo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.reggie.common.R;
import com.demo.reggie.pojo.Category;
import com.demo.reggie.pojo.Setmeal;
import com.demo.reggie.dto.SetmealDto;
import com.demo.reggie.service.CategoryService;
import com.demo.reggie.service.SetmealDishService;
import com.demo.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", key = "#setmealDto.categoryId + '_' +#setmealDto.status")
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }

    /**
     * 分页显示套餐列表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //根据name模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        //根据修改时间降序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        //复制对象
        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");

        List<Setmeal> records = pageInfo.getRecords();
        ArrayList<SetmealDto> setmealDtos = new ArrayList<>();
        for (Setmeal record : records) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record, setmealDto);
            //套餐分类id
            Long categoryId = record.getCategoryId();
            //根据套餐id查询分类对象
            Category category = categoryService.getById(categoryId);
            //将分类名称赋值给dto
            setmealDto.setCategoryName(category.getName());
            setmealDtos.add(setmealDto);
        }
        setmealDtoPage.setRecords(setmealDtos);

        return R.success(setmealDtoPage);
    }

    /**
     * 根据id删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        log.info(ids.toString());
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 起售停售的修改
     * @param ids
     * @param type
     * @return
     */
    @PostMapping("/status/{type}")
    public R<String> status(@RequestParam List<Long> ids, @PathVariable("type") int type){
        log.info(ids.toString(), type);
        setmealService.status(ids, type);
        return R.success("修改成功");
    }

    /**
     * 修改套餐前的回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealDto(@PathVariable("id") Long id){
        SetmealDto setmealDto = setmealService.getSetmealDto(id);
        return R.success(setmealDto);
    }

    /**
     * 修改，先删除再添加
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "setmealCache", key = "#setmealDto.categoryId + '_' +#setmealDto.status")
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateSetmealWithDish(setmealDto);
        return R.success("套餐修改成功");
    }

    /**
     * 根据条件查询套餐数据列表
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "SetmealCache", key = "#setmeal.categoryId + '_' +#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
  
        return R.success(setmealList);
    }
}
