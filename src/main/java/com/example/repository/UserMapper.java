package com.example.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.repository.query.Param;

@Mapper
public interface UserMapper {

    //top.html表示時にuseridとsortを登録する処理
    //IGNOREにより、初回のみ実行する
    //@Insert("INSERT IGNORE INTO GoogleUser (userid, sort) VALUES (#{userid}, 'date')")
	@Insert("INSERT INTO GoogleUser (userid, sort) VALUES (#{userID}, 'date') ON CONFLICT DO NOTHING")
	//@Insert("INSERT INTO GoogleUser (userid, sort) VALUES ('matsuda.mutsumi61@gmail.com', 'date') ON CONFLICT DO NOTHING")
    void insertOneUser(@Param("userID") String userID);

    //ログイン中のユーザーのsortを取得
    @Select("SELECT sort FROM GoogleUser WHERE userid = #{userid}")
    String selectSort(@Param("userid") String userid);

    //ログイン中のユーザーのsortをdateかpriorityに更新
    @Update("UPDATE GoogleUser SET sort = #{sort} WHERE userid = #{userid}")
    void updateUserSort(@Param("sort") String sort, @Param("userid") String userid);

    /**
     * DeleteAll処理②
     * ログイン中のユーザーのGoogleUserテーブルの情報を削除
     * @param userid
     */
    @Delete("DELETE from GoogleUser WHERE userid = #{userid}")
    void deleteAll(@Param("userid") String userid);
    
}
