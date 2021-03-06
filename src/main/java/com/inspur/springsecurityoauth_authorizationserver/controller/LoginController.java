package com.inspur.springsecurityoauth_authorizationserver.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 登录模块
 * @Author Malcolm
 */
@RestController
public class LoginController {

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login-tj");
    }

    @GetMapping("/login-error")
    public ModelAndView loginError( Model model) {
        model.addAttribute("loginError", true);
        model.addAttribute("errorMsg", "登陆失败，账号或者密码错误！");
        return new ModelAndView("login-tj", "userModel", model);
    }

}
