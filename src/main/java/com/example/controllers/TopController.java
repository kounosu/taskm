package com.example.controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.scheduling.config.Task;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.service.TaskService;
import com.example.service.UserService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/")
public class TopController {
	
	TaskService taskService;
	UserService userService;
	private final OAuth2AuthorizedClientService authorizedClientService;
	
	public TopController(OAuth2AuthorizedClientService authorizedClientService) {
		  this.authorizedClientService = authorizedClientService;
		}
	
	private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
		return this.authorizedClientService.loadAuthorizedClient(
				authentication.getAuthorizedClientRegistrationId(), 
				authentication.getName()
		);
	}
	
    
    	
    	@GetMapping("/")
		public String index(@AuthenticationPrincipal OAuth2User oauth2User, OAuth2AuthenticationToken authentication, Model model) {
			
			//ログイン中のユーザーのメールアドレスをuserIDとして取得
			String userID = oauth2User.getAttribute("email");

			//初回ログイン時のみ、GoogleUserテーブルにUser追加
			userService.insertOneUser(userID);
			
			//ログイン中のユーザーのsort情報取得
			String sort = userService.selectSort(userID);

			//タスクリスト初期化宣言
			//下記if文でタスクリストを入れるため
			List<Task> task = null;
			
			//ログイン中のユーザーのsortがdateならば日付順、priorityなら優先度順でタスク表示
			if(sort.equals("date")) {
				task = taskService.selectUndoneTasks(userID);
			}
			if(sort.equals("priority")) {
				task = taskService.selectUndoneTasksByPriority(userID);
			}
			
			//sort情報表示用
			model.addAttribute("sort", sort);
			
			//task情報表示用
			model.addAttribute("task", task);
			
			//GoogleCalendar表示用
			model.addAttribute("userID", userID);
			
			/**
			 * 週間見積時間表示処理
			 */
			LocalDate today = LocalDate.now();
			LocalDate tomorrow  = today.plusDays(1);

			LocalDate mon = today.with(DayOfWeek.MONDAY);
			LocalDate tue = today.with(DayOfWeek.TUESDAY);
			LocalDate wed = today.with(DayOfWeek.WEDNESDAY);
			LocalDate thu = today.with(DayOfWeek.THURSDAY);
			LocalDate fri = today.with(DayOfWeek.FRIDAY);
			LocalDate sat = today.with(DayOfWeek.SATURDAY);
			LocalDate sun = today.with(DayOfWeek.SUNDAY);
			
			double monEstimatedTime = 0;
			double tueEstimatedTime = 0;
			double wedEstimatedTime = 0;
			double thuEstimatedTime = 0;
			double friEstimatedTime = 0;
			double satEstimatedTime = 0;
			double sunEstimatedTime = 0;
			for(int i = 0; i < task.size(); i++) {
				if(mon.toString().equals(task.get(i).getScheduledDate())) {monEstimatedTime += task.get(i).getEstimatedTime();}
				if(tue.toString().equals(task.get(i).getScheduledDate())) {tueEstimatedTime += task.get(i).getEstimatedTime();}
				if(wed.toString().equals(task.get(i).getScheduledDate())) {wedEstimatedTime += task.get(i).getEstimatedTime();}
				if(thu.toString().equals(task.get(i).getScheduledDate())) {thuEstimatedTime += task.get(i).getEstimatedTime();}
				if(fri.toString().equals(task.get(i).getScheduledDate())) {friEstimatedTime += task.get(i).getEstimatedTime();}
				if(sat.toString().equals(task.get(i).getScheduledDate())) {satEstimatedTime += task.get(i).getEstimatedTime();}
				if(sun.toString().equals(task.get(i).getScheduledDate())) {sunEstimatedTime += task.get(i).getEstimatedTime();}
			}

			//LocalDate型mon~sunをListに格納し、modelに渡す
			DateTimeFormatter md = DateTimeFormatter.ofPattern("MM/dd(E)");
			List<String> week = Arrays.asList(
					mon.format(md), 
					tue.format(md), 
					wed.format(md), 
					thu.format(md), 
					fri.format(md), 
					sat.format(md), 
					sun.format(md));
			model.addAttribute("week", week);

			//今週のタスク状況欄 「今日」赤字表示 「明日」太字表示用
			String todayofweek = today.format(md);
			model.addAttribute("todayofweek", todayofweek);
			String tomorrowofweek = tomorrow.format(md);
			model.addAttribute("tomorrowofweek", tomorrowofweek);


			//double型mon~sunEstimatedTimeをListに格納し、modelに渡す
			List<Double> weekEstimatedTime = Arrays.asList(monEstimatedTime, tueEstimatedTime, wedEstimatedTime, thuEstimatedTime, friEstimatedTime, satEstimatedTime, sunEstimatedTime);
			model.addAttribute("weekEstimatedTime", weekEstimatedTime);

			//タスク一覧 日付欄 今日、明日表示用
			DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			model.addAttribute("today", today.format(ymd));
			model.addAttribute("tomorrow", tomorrow.format(ymd));

			/**
			 * 週間見積表示処理終わり
			 */


        return "top/index";
    }

}