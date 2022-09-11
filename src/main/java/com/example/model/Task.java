package com.example.model;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


import lombok.Data;

@Data
public class Task {
	
	//User情報と紐付けするためのフィールド
	private String userID;
	
	//task識別番号 primary key
	private int taskID;

	//タスク名 重複可能
	@NotBlank(message = "タスク名を入力してください")
	@Size(min = 1, max = 15, message = "1〜15文字で入力してください")
	private String taskName;
	
	//タスクの見積所要時間
	@Digits(integer = 1, fraction = 1, message="整数部1桁、小数部1桁で入力してください")
	private double estimatedTime;
	
	//タスクの実行予定日
	//htmlでもバリデーション
	@NotBlank(message = "予定日を入力してください")
	private String scheduledDate;
	
	//タスクの開始予定時刻
	//htmlでもバリデーション
	@NotBlank(message = "開始時刻を入力してください")
	private String startTime;
	
	//タスクの累計実行時間
	//未実装
	private String accumlatedTime;

	//タスクが完了状態か否か表す
	private boolean done;
	
	//タスク完了日
	private String completionDate;

	//タスクの優先度(A~C or Null)
	//htmlでバリデーション
	private String priority;

}
