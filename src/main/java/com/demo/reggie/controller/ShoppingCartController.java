package com.demo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.demo.reggie.common.R;
import com.demo.reggie.pojo.ShoppingCart;
import com.demo.reggie.service.ShoppingCartService;
import com.demo.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 将菜品或套餐添加到购物车中
     * @param shoppingCart
     * @return
     */
    @RequestMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //获得当前操作用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //查询当前菜品套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);//条件：用户名

        //如果菜品id不为null，说明添加的是菜品
        queryWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
        //如果菜品id为null，说明添加的是套餐
        queryWrapper.eq(dishId == null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);

        if(cart != null){
            //如果已经存在，数量+1
            Integer countNum = cart.getNumber();
            cart.setNumber(countNum + 1);
            shoppingCartService.updateById(cart);
        }else{
            //不存在，就添加到购物车，默认数量是1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }
        return R.success(shoppingCart);
    }

    /**
     * 修改购物车菜品或套餐数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);//条件：用户名
        //如果菜品id不为null，说明修改的是菜品
        queryWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
        //如果菜品id为null，说明修改的是套餐
        queryWrapper.eq(dishId == null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        //获得该菜品或套餐
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);

        //修改数量
        Integer countNum = cart.getNumber();
        LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShoppingCart::getUserId, userId);
        if(countNum > 1){
            //如果数量大于1，减一,
            countNum--;
            //理由同上
            updateWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
            updateWrapper.eq(dishId == null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
            updateWrapper.set(ShoppingCart::getNumber, countNum);
            shoppingCartService.update(updateWrapper);
        }else{
            //数量等于1，直接删除
            updateWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
            updateWrapper.eq(dishId == null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
            shoppingCartService.remove(updateWrapper);
        }

        return R.success("修改成功");
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);//按创建时间升序
        List<ShoppingCart> cartList = shoppingCartService.list(queryWrapper);
        return R.success(cartList);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long userId = BaseContext.getCurrentId();
        LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(updateWrapper);
        return R.success("清空购物车成功");
    }
}
