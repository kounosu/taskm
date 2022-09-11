package com.example.model;

import lombok.Data;

@Data
public class GoogleUser {

    //GメールアドレスをuserIDとして格納
    private String userID;

    //タスクをソートする方法を格納
    //日付順(date)か優先度順(priority)
    private String sort;

}
