package com.example.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.service.TaskService;
import com.example.service.UserService;

public class DeleteController{
	TaskService taskService;
	UserService userService;
@PostMapping("/delete/{userID}")
	public String deleteAll(@AuthenticationPrincipal OAuth2User oauth2User) {

		//ログイン中のユーザーのメールアドレスをuserIDとして取得
		String userID = oauth2User.getAttribute("email");

		//ログイン中のユーザーのタスク情報とユーザー情報を削除
		taskService.deleteAll(userID);
		userService.deleteAll(userID);

		return "/delete/index.html";
	}
}
