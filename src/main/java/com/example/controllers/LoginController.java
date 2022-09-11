package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * ログインページ表示
     * @return
     */
    @GetMapping(path = "/")
    public String login() {
        return "login";
    }



}