package com.xu.Jerry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xu.Jerry.Utils.SMSUtils;
import com.xu.Jerry.Utils.ValidateCodeUtils;
import com.xu.Jerry.common.R;
import com.xu.Jerry.entity.User;
import com.xu.Jerry.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;


    /**
     * 发送验证码
     */
    @PostMapping("sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        log.info("手机号：{}", user.getPhone());
        //1,获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //如果手机号不为空
            //2，生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码：= {}", code);
            //3,调用阿里云sms发送短信
            //SMSUtils.sendMessage("某某外卖"," ",phone,code);
            //4,把生成的验证码根据手机号储存到session中
            session.setAttribute(phone, code);
            return R.success("验证码发送成功");
        }

        return R.error("验证码发送失败");
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public R<User> login(HttpSession session, @RequestBody Map map) {
        //这里页面发送的时json数据，我们可以自己在写一个UserDto实体类去继承User
        //也可以使用Map接收
        //1，首先我们先获取页面发送的参数，手机号，code
        log.info("手机号：={}", map.get("phone"));
        log.info("填写验证码：{}", map.get("code"));
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //2,获取session中的code
        Object codeInSession = session.getAttribute(phone);
        //3,判断这两者验证码是否相同
        if (codeInSession != null && codeInSession.equals(code)) {
            //4,说明验证码比对成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                //5，去数据库查询该用户是否是新用户,如果是新用户，就自动注册
                user = new User();
                user.setPhone(phone);//设置手机号
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
    /**
     * 退出登录
     */
    //退出登录
    @PostMapping("/loginout")
    public R<String> logOut(HttpSession session){
        //清除session中用户id
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
