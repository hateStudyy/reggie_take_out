package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author coldwind
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code= {}",code);

            //调用阿里云的短信服务api发送短信
//            SMSUtils.sendMessage("reggie", "SMS_460835180",phone, code);
            //需要将生成的验证码保存Session
//            session.setAttribute(phone, code);

            //将生成的验证码缓存到Redis中，并设置有效期5分钟
            redisTemplate.opsForValue().set(phone, code,5, TimeUnit.MINUTES);
            return R.success("手机验证码发送成功！");
        }


        return R.error("发送失败...");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获得保存的验证码
//        Object codeInSession = session.getAttribute(phone);

        //从redis中获取缓存验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        //进行比对(页面提交的验证码 和 session中保存的验证码)
        if(codeInSession !=null && codeInSession.equals(code)) {//如果比对成功说明登成功
            LambdaQueryWrapper<User> q = new LambdaQueryWrapper<>();
            q.eq(User::getPhone,phone);

            User user = userService.getOne(q);
            if(user == null){
                //判断当前手机号对应的用户是否是新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
                //完成注册了
            }
            session.setAttribute("user", user.getId());

            //如果用户登录成功，删除redis缓存的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }

        return R.error("登录失败...");
    }
}
