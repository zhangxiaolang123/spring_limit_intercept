package com.zxl.interceptorlimit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class URLController {
    //http://localhost:8200/urltest访问
    @RequestMapping("/urltest")
    @ResponseBody
    public String test(HttpServletRequest request, ModelMap modelMap) {
        return "aaa";
    }
}
