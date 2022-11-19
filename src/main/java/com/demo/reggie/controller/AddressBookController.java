package com.demo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.demo.reggie.common.R;
import com.demo.reggie.pojo.AddressBook;
import com.demo.reggie.service.AddressBookService;
import com.demo.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 添加收获地址
     * @param addressBook
     * @return
     */
    @Transactional
    @PostMapping
    public R<String> add(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("添加地址成功");
    }

    /**
     * 查询收货地址列表
     * @return
     */
    @RequestMapping("/list")
    public R<List<AddressBook>> list(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 根据id获取单个地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable("id") Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 删除地址
     * @param ids
     * @return
     */
    @Transactional
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        addressBookService.removeByIds(ids);
        return R.success("删除成功");
    }

    /**
     * 默认收货地址设置
     * @param addressBook
     * @return
     */
    @Transactional
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        //先将所有地址设为非默认
        updateWrapper.set(AddressBook::getIsDefault, 0);
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        addressBookService.update(updateWrapper);
        //设置当前地址为默认
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("默认地址修改成功");
    }

    /**
     * 前往订单页面前获得默认收货地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if(addressBook == null){
            return R.error("请先添加默认地址");
        }else{
            return R.success(addressBook);
        }

    }
}
