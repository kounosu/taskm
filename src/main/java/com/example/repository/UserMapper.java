package com.example.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.repository.query.Param;

@Mapper
public interface UserMapper {

    //top.html表示時にuserIDとsortを登録する処理
    //IGNOREにより、初回のみ実行する
    @Insert("INSERT IGNORE INTO GoogleUser (userID, sort) VALUES (#{userID}, 'date')")
    public void insertOneUser(@Param("userID") String userID);

    //ログイン中のユーザーのsortを取得
    @Select("SELECT sort FROM GoogleUser WHERE userID = #{userID}")
    public String selectSort(@Param("userID") String userID);

    //ログイン中のユーザーのsortをdateかpriorityに更新
    @Update("UPDATE GoogleUser SET sort = #{sort} WHERE userID = #{userID}")
    public void updateUserSort(@Param("sort") String sort, @Param("userID") String userID);

    /**
     * DeleteAll処理②
     * ログイン中のユーザーのGoogleUserテーブルの情報を削除
     * @param userID
     */
    @Delete("DELETE from GoogleUser WHERE userID = #{userID}")
    public void deleteAll(@Param("userID") String userID);
    
}
