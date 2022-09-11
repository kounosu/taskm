package com.example.controllers;

import java.util.List;

import org.springframework.scheduling.config.Task;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.service.TaskService;
import com.example.service.UserService;

import groovyjarjarpicocli.CommandLine.Model;

@Controller
public class DoneController{
	TaskService taskService;
	UserService userService;
	
@PostMapping("/deleteDone/{taskID}")
public String deleteDone(@PathVariable("taskID") int taskID) {
	taskService.deleteTask(taskID);
		return "redirect:/app/done";
	}

@GetMapping("/done")
public String displayDone(Model model, @AuthenticationPrincipal OAuth2User oauth2User) {

	//ログイン中のユーザーのメールアドレスをuserIDとして取得
	String userID = oauth2User.getAttribute("email");

	//done状態のタスクのみselect
	List<Task> task = taskService.selectDoneTasks(userID);
	model.addAttribute("task", task);

	return "/done/index.html";
}
}