package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.model.Task;

@Mapper
public interface TaskMapper {

	    
	/**
	 * select全件
	 * @return
	 */
	@Select("SELECT * FROM Task")
	public List<Task> selectAllTasks();
	
	
	/**
	 * select undoneのみ scheduledDate順　
	 * @param userID
	 * @return
	 */
	@Select("SELECT * FROM Task WHERE done = false AND userID = #{userID} ORDER BY " +
	    "CASE " +
	        "WHEN scheduledDate IS NULL THEN'2' " +//null最後
	        "WHEN scheduledDate = '' THEN '1' " +//空文字最後
	        "ELSE '0' " +
	    "END, scheduledDate ASC, startTime ASC")
	public List<Task> selectUndoneTasks(@Param("userID") String userID);
	
	
	/**
	 * select　undoneのみ　priority順
	 * @param userID
	 * @return
	 */
	@Select("SELECT * FROM Task WHERE done = false AND userID = #{userID} ORDER BY " +
	    "CASE " +
	        "WHEN priority IS NULL THEN '2' " +//null最後
	        "WHEN priority = '' THEN '1' " +//空文字最後
	        "ELSE '0' " +
	    "END, priority ASC, scheduledDate ASC")
	public List<Task> selectUndoneTasksByPriority(@Param("userID") String userID);
	
	
	/**
	 * select undone,todayのみ startTime順
	 * @param userID
	 * @return
	 */
	@Select("SELECT * FROM Task WHERE scheduledDate = to_char(CURRENT_DATE, 'YYYY-MM-DD') AND done = false AND userID = #{userID} ORDER BY " +
	    "CASE " +
	        "WHEN startTime IS NULL THEN '2' " +//null最後
	        "WHEN startTime = '' THEN '1' " +//空文字最後
	        "ELSE '0' " +
	    "END, startTime ASC")
	public List<Task> selectTodayTask(@Param("userID") String userID);
	
	
	/**
	 * select doneのみ
	 * @param userID
	 * @return
	 */
	@Select("SELECT * FROM Task WHERE done = true AND userID = #{userID} ORDER BY completionDate ASC")
	public List<Task> selectDoneTasks(@Param("userID") String userID);
	
	
	/**
	 * select1件
	 * @param taskID
	 * @return
	 */
	@Select("SELECT * FROM Task WHERE taskID = #{taskID}")
	public Task selectOne(int taskID);
	
	
	/**
	 * insert1件
	 * @param task
	 */
	@Insert("INSERT INTO Task (userID, taskName, estimatedTime, scheduledDate, startTime) VALUES (#{userID}, #{taskName}, #{estimatedTime}, #{scheduledDate}, #{startTime})")
	public void insertOneTask(Task task);
	
	
	/**
	 * edit.htmlのタスク編集処理
	 * @param task
	 */
	@Update("UPDATE Task SET "+
	    "taskName = #{taskName}, "+
	    "estimatedTime = #{estimatedTime}, "+
	    "scheduledDate = #{scheduledDate}, "+
	    "startTime = #{startTime}, "+
	    "priority = #{priority} "+
	    "WHERE taskID = #{taskID}")
	public void updateOneTask(Task task);
	
	
	/**
	 * done処理
	 * done→trueへ
	 * completionDate→CURRENT_DATE
	 * @param taskID
	 */
	@Update("UPDATE Task SET done = true, completionDate = CURRENT_DATE WHERE taskID = #{taskID}")
	public void done(int taskID);
	
	
	/**
	 * undone処理
	 * done→falseへ
	 * comletionDate→null
	 * @param taskID
	 */
	@Update("UPDATE Task SET done = false, completionDate = null WHERE taskID = #{taskID}")
	public void undone(int taskID);
	
	
	/**
	 * DeleteTask処理
	 * @param taskID
	 */
	@Delete("DELETE from Task WHERE taskID = #{taskID}")
	public void deleteTask(int taskID);


	/**
	 * DeleteAll処理①
	 * ②はUserMapper.javaに記載
	 * ログイン中のユーザーのTaskテーブルの情報を削除
	 * @param userID
	 */
	@Delete("DELETE from Task WHERE userID = #{userID}")
	public void deleteAll(@Param("userID") String userID);
		    


}