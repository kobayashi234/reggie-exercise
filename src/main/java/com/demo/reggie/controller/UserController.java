package com.demo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.reggie.common.R;
import com.demo.reggie.pojo.User;
import com.demo.reggie.service.UserService;
import com.demo.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@Controller
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 移动端用户发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        //手机号不为空
        if(StringUtils.isNotEmpty(phone)){
            //生成随机的4位数验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);
            //将验证码保存到Session中
            session.setAttribute(phone, code);
            return R.success("验证码已发送");
        }

        return R.error("短信发送失败");
    }

    /**
     *移动端登录
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session){
        log.info(map.toString());
        //获取手机号和验证码
        String phone = map.get("phone");
        String code = map.get("code");
        //从session中获得和手机号对应的验证码
        String codeInSession = (String) session.getAttribute(phone);
        //进行验证码对比，页面提交的和session中存储的验证码
        if(codeInSession != null && codeInSession.equals(code)){
            //登录成功后判断是否为新用户，如果是新用户自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            //新用户自动注册
            if(user == null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
