package com.demo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.reggie.common.R;
import com.demo.reggie.pojo.Category;
import com.demo.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category, HttpSession session){
        log.info("category:{}", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页显示分类列表
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Category :: getSort);
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id删除菜品分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.delete(ids);
        return R.success("删除成功");
    }

    /**
     * 修改分类信息
     * @param category
     * @param session
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * 新增套餐前获得套餐分类信息
     * @param type
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> getCategoryList(Integer type){
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null, Category::getType, type);
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(wrapper);
        return R.success(list);
    }
}
