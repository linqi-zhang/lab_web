package com.benearyou.tab_web.controller;

import com.benearyou.tab_web.common.dto.LoginDto;
import com.benearyou.tab_web.common.lang.Result;
import com.benearyou.tab_web.entity.Setting;
import com.benearyou.tab_web.service.SettingService;
import com.benearyou.tab_web.util.JwtUtils;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@RestController
public class AccountController {

    @Autowired
    SettingService settingService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/SettingServiceLogin")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response) {
        Setting setting = settingService.getOne(new QueryWrapper<Setting>().eq("username", loginDto.getUsername()));
        Assert.notNull(setting, "用户不存在");
//        System.out.println("用户存在");
//        System.out.println(SecureUtil.md5(loginDto.getPassword()));

        //  明文密码
        if(!setting.getPassword().equals(loginDto.getPassword())){
            return Result.fail("密码不正确");
        }

        String jwt = jwtUtils.generateToken(setting.getId());

        response.setHeader("Authorization", jwt);
        response.setHeader("Access-control-Expose-Headers", "Authorization");

        return Result.succ(MapUtil.builder()
                .put("id", setting.getId())
                .put("username", setting.getUsername())
                .put("lastlogin", setting.getLastLogin())
                .map()
        );
    }

    @RequiresAuthentication
    @GetMapping("/SettingServiceLogout")
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.succ(null);
    }

}
