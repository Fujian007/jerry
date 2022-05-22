package com.xu.Jerry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xu.Jerry.common.BaseContext;
import com.xu.Jerry.common.R;
import com.xu.Jerry.entity.ShoppingCart;
import com.xu.Jerry.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 菜品或者套餐加入购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> save(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shopOne = selectOne(shoppingCart,userId);
        if (null != shopOne){
            //说明该菜品或者套餐已存在,我们获取其数量
            Integer number = shopOne.getNumber();
            //我们调用updateById方法，把该菜品的number属性加一即可
            shopOne.setNumber(number + 1);
            shoppingCartService.updateById(shopOne);
        }else {
            //第一次添加到购物车的商品只需要设置userId即可
            shoppingCart.setUserId(userId);
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }
        return R.success(shoppingCart);
    }

    /**http://localhost:8080/shoppingCart/sub
     * 对购物车的菜品进行减少
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shopOne = selectOne(shoppingCart, userId);
        Integer number = shopOne.getNumber();
        //判断数量减一是否为0，如果为0就删除
        if ((number - 1) <= 0){
            shoppingCartService.removeById(shopOne);
        }else {
            //不为0就修改数量
            shopOne.setNumber(number - 1);
            //调用update方法
            shoppingCartService.updateById(shopOne);
        }
        return R.success("已修改数量");
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("clean")
    public R<String> clean(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        //delete from shopping_Cart where user_id = ?
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }

    /**
     * 查询购物车
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        //排序，按照创建时间升序
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }


    /**
     * 查询单个ShoppingCart对象
     * 菜品或者套餐
     * @param shoppingCart
     * @return
     */
    public ShoppingCart selectOne(ShoppingCart shoppingCart,Long userId){
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        //1,先判断该用户的添加的菜品是否和购物车中的相同
        //如果相同直接修改该菜品的number即可
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        //再判断要保存的是菜品还是套餐
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        //返回查询该菜品或者套餐，可能为空
        return shoppingCartService.getOne(queryWrapper);
    }


}



