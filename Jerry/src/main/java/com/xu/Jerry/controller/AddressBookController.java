package com.xu.Jerry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xu.Jerry.common.BaseContext;
import com.xu.Jerry.common.R;
import com.xu.Jerry.entity.AddressBook;
import com.xu.Jerry.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;


    /**
     * 保存地址
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        //获取当前用户登录的id
        Long id = BaseContext.getCurrentId();
        addressBook.setUserId(id);
        log.info("addressBook：{}",addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址default
     */

    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        log.info("addressBook：{}",addressBook);
        //1,先把该用户所有的地址先设置成非默认状态 is_default = 0
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(updateWrapper);
        //2,把用户刚刚传过来的地址AddressBook设置为默认

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R getOneAddress(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if (null != addressBook){
            return R.success(addressBook);
        }else {
            return R.error("没有找到该地址");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        //查询当前账号的默认地址
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        Long currentId = BaseContext.getCurrentId();
        queryWrapper.eq(AddressBook::getIsDefault,1);
        queryWrapper.eq(AddressBook::getUserId,currentId);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (addressBook != null) {
            return R.success(addressBook);
        }else {
            return R.error("未找到默认地址");
        }
    }
    /**
     * 查询用户所有地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        Long currentId = BaseContext.getCurrentId();
        addressBook.setUserId(currentId);
        log.info("查询id：{}所有地址",currentId);
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(),AddressBook::getUserId,currentId);
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        //select * from addressBook where userId = ? order by updateTime desc
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }
}
