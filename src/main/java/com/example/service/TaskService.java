package com.example.service;

import java.util.List;

import com.example.model.Task;
// import java.util.List;
import com.example.repository.TaskMapper;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor//final fieldのコンストラクタを自動的に生成してくれる
public class TaskService {

    //@RequiredArgsConstructorによりコンストラクタインジェクション
    private final TaskMapper mapper;


    //select全件
    public List<Task> selectAllTasks() {
        return mapper.selectAllTasks();
    }

    //select undoneのみ 予定日順　
    public List<Task> selectUndoneTasks(String userID) {
        return mapper.selectUndoneTasks(userID);
    }

    //select undone 優先度順
    public List<Task> selectUndoneTasksByPriority(String userID) {
        return mapper.selectUndoneTasksByPriority(userID);
    }

    //select Doneのみ
    public List<Task> selectDoneTasks(String userID) {
        return mapper.selectDoneTasks(userID);
    }

    //select todayのみ
    public List<Task> selectTodayTask(String userID) {
        return mapper.selectTodayTask(userID);
    }

    //select1件
    public Task selectOne(int taskID) {
    	return mapper.selectOne(taskID);
    }
    
    //新規タスク登録
    public void insertOneTask(Task task) {
    	mapper.insertOneTask(task);
    }

    //タスク編集
    public void updateOneTask(Task task) {
        mapper.updateOneTask(task);
    }

    //タスク完了状態へdone
    public void done(int taskID) {
        mapper.done(taskID);
    }

    //タスク未完了状態へundone
    public void undone(int taskID) {
        mapper.undone(taskID);
    }

    //delete処理
    public void deleteTask(int taskID) {
        mapper.deleteTask(taskID);
    }

    //DeleteALl処理
    public void deleteAll(String userID) {
        mapper.deleteAll(userID);
    }

}



