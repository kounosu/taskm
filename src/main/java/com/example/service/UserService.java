package com.example.service;

import org.springframework.stereotype.Service;

import com.example.repository.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    //UserMapperをコンストラクタインジェクション
    UserMapper mapper;
    
    //top.html表示時にuserIDとsortを登録する処理
    //IGNOREにより、初回のみ実行する
    public void insertOneUser(String userID) {
        mapper.insertOneUser(userID);
    }

    //ログイン中のユーザーのsort情報を取得
    public String selectSort(String userID) {
        return mapper.selectSort(userID);
    }

    //ログイン中のユーザーのsortをdateかpriorityに更新
    public void updateUserSort(String sort, String userID) {
        mapper.updateUserSort(sort, userID);
    }

    //DeleteAll ログイン中のユーザー情報を全削除
    public void deleteAll(String userID) {
        mapper.deleteAll(userID);
    }




    
}
