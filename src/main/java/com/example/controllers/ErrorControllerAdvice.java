package com.example.controllers;

import com.example.exception.AccessTokenNullException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorControllerAdvice {
    /**
     * AccessToken取得時にNullだった場合の例外処理
     * @param e
     * @return
     */
    @ExceptionHandler(AccessTokenNullException.class)
    public String accessTokenNullError(AccessTokenNullException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "/error/access_error.html";
    }

    /**
     * 上記ケース以外の場合の例外処理
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public String otherError(Exception e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "/error/basic_error.html";
    }
    
}
