package com.example.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.exception.AccessTokenNullException;
import com.example.model.Task;
import com.example.service.GoogleCalendarAPIService;
import com.example.service.TaskService;
import com.example.service.UserService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

@Data
@Controller
@RequiredArgsConstructor
@RequestMapping("/app")
public class TaskController {
	
	//コンストラクタインジェクション
	//@RequiredArgsConstructorによりコンストラクタ記述省略
	private final TaskService taskService;
	private final UserService userService;
	private final GoogleCalendarAPIService gcapiService;
	
	
	// 認可済みのクライアント情報は OAuth2AuthorizedClientService経由で取得できる
	private final OAuth2AuthorizedClientService authorizedClientService;
	

	
	/**
	 * 
	 * @param authentication
	 * @return OAuth2AuthorizedClient
	 * 
	 * https://spring.pleiades.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/client/OAuth2AuthorizedClientService.html
	 * OAuth2AuthorizedClientServiceの
	 * メソッドloadAuthorizedClient(clientRegistrationId, principalName)を、
	 * 認証されたTokenを使って書き換えている
	 * 	clientRegistrationId - クライアントの登録の識別子
	 * 	principalName - エンドユーザー Principal の名前 (リソース所有者)
	 */
	private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
		return this.authorizedClientService.loadAuthorizedClient(
				authentication.getAuthorizedClientRegistrationId(), 
				authentication.getName()
		);
	}
		
		
		/**
		 * top.html表示
		 * @param oauth2User
		 * @param authentication
		 * @param model
		 * @return
		 */
		@GetMapping("/top")
		public String displayTop(@AuthenticationPrincipal OAuth2User oauth2User, OAuth2AuthenticationToken authentication, Model model) {
			
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


			return "/app/top.html";
	}


	/**
	 * top.html表示 優先度順ボタン押下
	 * @param oauth2User
	 * @param model
	 * @return
	 */
	@PostMapping("/top/priority")
	public String sortByPriority(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {

		//ログイン中のユーザーのメールアドレスをuserIDとして取得
		String userID = oauth2User.getAttribute("email");

		//ログイン中のユーザーのsortをpriorityに変換
		String sort = "priority";
		userService.updateUserSort(sort, userID);

		return "redirect:/app/top";
	}


	/**
	 * top.html表示 予定日順ボタン押下
	 * @param oauth2User
	 * @param model
	 * @return
	 */
	@PostMapping("/top/date")
	public String sortByDate(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {

		//ログイン中のユーザーのメールアドレスをuserIDとして取得
		String userID = oauth2User.getAttribute("email");

		//ログイン中のユーザーのsortをdateに変換
		String sort = "date";
		userService.updateUserSort(sort, userID);

		return "redirect:/app/top";
	}

	/**
	 * GoogleCalendarAPI　テスト用
	 * @param authenticationToken
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	// @PostMapping("/test")
	// public String test(OAuth2AuthenticationToken authenticationToken) throws GeneralSecurityException, IOException {
	// 	gcapiService.addEventTest(authenticationToken);
	// 	return "redirect:/app/top";
	// }

	/**
	 * GoogleCalendarAPI　イベント追加
	 * @param authenticationToken
	 * @param taskID
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	@PostMapping("/event/{taskID}")
	public String addEvent(Model model, OAuth2AuthenticationToken authenticationToken, @PathVariable("taskID") int taskID) throws GeneralSecurityException, IOException {
		
		try {
			gcapiService.addEvent(authenticationToken, taskID);
			
		} catch (AccessTokenNullException e) {
			model.addAttribute("message", e);
			return "app/accesserror.html"; 
		}
		
		return "redirect:/app/top";
	}

	
	/**
	 * 新規タスク登録用new.html表示
	 * @param model
	 * @param task
	 * @param oauth2User
	 * @return
	 */
	@GetMapping("/new")
	public String displayNew(Model model, @ModelAttribute Task task, @AuthenticationPrincipal OAuth2User oauth2User) {
		model.addAttribute("email", oauth2User.getAttribute("email"));
		task.setScheduledDate(LocalDate.now().toString());
		task.setStartTime("12:00");
		return "/app/new.html";
	}
	/**
	 * 新規タスク登録
	 * postで受け取ったあとの処理
	 * @param model
	 * @param oauth2User
	 * @param task
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/new")
	public String register(Model model, @AuthenticationPrincipal OAuth2User oauth2User, @Validated @ModelAttribute Task task, BindingResult bindingResult) {
		
		//バリデーション処理
		if(bindingResult.hasErrors()) {
			System.err.println("↓↓　エラー発生！BindingResult内容　↓↓");
			System.err.println(bindingResult);
			System.err.println("↑↑　エラー発生！BindingResult内容　↑↑");

			model.addAttribute("email", oauth2User.getAttribute("email"));
			task.setScheduledDate(LocalDate.now().toString());
			task.setStartTime("12:00");
			return "/app/new.html";
		}

		//新規タスクinsert
		taskService.insertOneTask(task);

		return "redirect:/app/top";
	}


	/**
	 * タスク編集処理用のedit.hmlt表示
	 * @param model
	 * @param taskID
	 * @param task
	 * @return
	 */
	@GetMapping("/edit/{taskID}")
	public String displayEdit(Model model, @PathVariable("taskID") int taskID, @ModelAttribute Task task) {
		model.addAttribute("task", taskService.selectOne(taskID));
		return "/app/edit.html";
	}
	/**
	 * タスク編集
	 * postで受け取ったあとの処理
	 */
	@PostMapping("/edit/{taskID}")
	public String edit(Model model, @Validated @ModelAttribute Task task, BindingResult bindingResult) {

		//バリデーション処理
		if(bindingResult.hasErrors()) {
			System.err.println("↓↓　エラー発生！BindingResult内容　↓↓");
			System.err.println(bindingResult);
			System.err.println("↑↑　エラー発生！BindingResult内容　↑↑");

			model.addAttribute("task", task);
			return "/app/edit.html";
		}

		taskService.updateOneTask(task);
		return "redirect:/app/top";
	}
	

	/**
	 * タスク完了状態にする処理
	 * top.html用
	 * @param taskID
	 * @return
	 */
	@PostMapping("/doneTop/{taskID}")
	public String doneTop(@PathVariable("taskID") int taskID) {
		taskService.done(taskID);
		return "redirect:/app/top";
	}
	/**
	 * タスク完了状態にする処理
	 * today.html用
	 * @param taskID
	 * @return
	 */
	@PostMapping("/doneToday/{taskID}")
	public String doneToday(@PathVariable("taskID") int taskID) {
		taskService.done(taskID);
		return "redirect:/app/today";
	}


	/**
	 * undone処理
	 * タスクを未完了状態に戻す
	 * @param taskID
	 * @return
	 */
	@PostMapping("/undone/{taskID}")
	public String undone(@PathVariable("taskID") int taskID) {
		taskService.undone(taskID);
		return "redirect:/app/done";
	}


	/**
	 * タスク削除処理
	 * top.html用
	 * @param taskID
	 * @return
	 */
	@PostMapping("/deleteTop/{taskID}")
	public String deleteTop(@PathVariable("taskID") int taskID) {
		taskService.deleteTask(taskID);
		return "redirect:/app/top";
	}
	/**
	 * タスク削除処理
	 * done.html用
	 * @param taskID
	 * @return
	 */
	@PostMapping("/deleteDone/{taskID}")
	public String deleteDone(@PathVariable("taskID") int taskID) {
		taskService.deleteTask(taskID);
		return "redirect:/app/done";
	}


	/**
	 * today.html表示
	 * @param model
	 * @param oauth2User
	 * @return
	 */
	@GetMapping("/today")
	public String displayToday(Model model, @AuthenticationPrincipal OAuth2User oauth2User) {

		//ログイン中のユーザーのメールアドレスをuserIDとして取得
		String userID = oauth2User.getAttribute("email");

		//todayタスクのみ表示
		List<Task> task = taskService.selectTodayTask(userID);
		model.addAttribute("task", task);

		//GoogleCalendar表示用
		model.addAttribute("userID", userID);

		return "/app/today.html";
	}


	/**
	 * done.html表示
	 * @param model
	 * @param oauth2User
	 * @return
	 */
	@GetMapping("/done")
	public String displayDone(Model model, @AuthenticationPrincipal OAuth2User oauth2User) {

		//ログイン中のユーザーのメールアドレスをuserIDとして取得
		String userID = oauth2User.getAttribute("email");

		//done状態のタスクのみselect
		List<Task> task = taskService.selectDoneTasks(userID);
		model.addAttribute("task", task);

		return "/app/done.html";
	}

	@PostMapping("/deleteAll/{userID}")
	public String deleteAll(@AuthenticationPrincipal OAuth2User oauth2User) {

		//ログイン中のユーザーのメールアドレスをuserIDとして取得
		String userID = oauth2User.getAttribute("email");

		//ログイン中のユーザーのタスク情報とユーザー情報を削除
		taskService.deleteAll(userID);
		userService.deleteAll(userID);

		return "/app/delete_all.html";
	}
	
	
	

}
