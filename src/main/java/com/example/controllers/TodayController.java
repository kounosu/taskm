package com.example.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.model.Task;
import com.example.service.TaskService;
import com.example.service.UserService;


@Controller
public class TodayController{
	TaskService taskService;
	UserService userService;
@GetMapping(path ="/today")
	public  String index(Model model, @AuthenticationPrincipal OAuth2User oauth2User) {

		//ログイン中のユーザーのメールアドレスをuserIDとして取得
		String userID = oauth2User.getAttribute("email");

		//todayタスクのみ表示
		List<Task> task = taskService.selectTodayTask(userID);
		model.addAttribute("task", task);

		//GoogleCalendar表示用ß
		model.addAttribute("userID", userID);

		return "/today/index.html";
	}
}
