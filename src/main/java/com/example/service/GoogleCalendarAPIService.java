package com.example.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import com.example.exception.AccessTokenNullException;
import com.example.model.Task;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleCalendarAPIService {
    
    //@RequiredArgsConstructorによりコンストラクタインジェクション
    private final TaskService taskService;
    // 認可済みのクライアント情報は OAuth2AuthorizedClientService経由で取得できる
    private final OAuth2AuthorizedClientService authorizedClientService;

    private static final String APPLICATION_NAME = "TaskTime";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    
    /**
     * ログインしているUserのOAuth2AccessTokenから、
     * GoogleCredentialsを取得する処理
     * 後述のaddEventメソッドで用いる
     * @param authenticationToken
     * @return
     * @throws AccessTokenNullException
     */
    private HttpRequestInitializer getCredentials(OAuth2AuthenticationToken authenticationToken) throws AccessTokenNullException {
    	OAuth2AuthorizedClient client =
    			authorizedClientService.loadAuthorizedClient(
    					authenticationToken.getAuthorizedClientRegistrationId(),
    					authenticationToken.getName());
        
        if(client == null) {
            String message = "OAuth2AuthorizedClientがnullです。";
            throw new AccessTokenNullException(message);
        }

        //accessToken取得処理
        //null時には再ログインを求めるページへ遷移させる例外処理                
        OAuth2AccessToken oauth2AccessToken = client.getAccessToken();

        if(oauth2AccessToken == null) {
            String message = "AccessTokenがnullです。";
            throw new AccessTokenNullException(message);
        }

        AccessToken accessToken = new AccessToken(oauth2AccessToken.getTokenValue(), Date.from(oauth2AccessToken.getExpiresAt()));
        GoogleCredentials credentials = GoogleCredentials.create(accessToken);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        return requestInitializer;
    }

    
    /**
     * GoogleCalendarにイベント追加
     * 上述のgetCredentialsメソッドで得たrequestInitializerを引数に、Calendar型のserviceを生成
     * @param authenticationToken
     * @param taskID
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws AccessTokenNullException
     */
    public String addEvent(OAuth2AuthenticationToken authenticationToken, int taskID) throws GeneralSecurityException, IOException, AccessTokenNullException{

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        
        Calendar service = new Calendar
        .Builder(HTTP_TRANSPORT, 
        JSON_FACTORY, 
        getCredentials(authenticationToken))
        .setApplicationName(APPLICATION_NAME)
        .build();
        
        DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter hm = DateTimeFormatter.ofPattern("HH:mm");
        
        //選択中のtask情報を取得
        Task task = taskService.selectOne(taskID);

        //String「yyyy-MM-dd」をLocalDateへ
        LocalDate startDate = LocalDate.parse(task.getScheduledDate(), ymd);
        //String「HH:mm」をLocalDateTImeへ
        LocalTime startTime = LocalTime.parse(task.getStartTime(), hm);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        //double esitimatedTimeをHoursとMinutesに分解
        double estimated = task.getEstimatedTime();
        int estimatedMins = (int)((estimated % 1) * 60);
        int estimatedHours = (int)Math.floor(estimated);
        
        LocalDateTime endDateTime = startDateTime.plusHours(estimatedHours).plusMinutes(estimatedMins);
        
        //DateTime型変換部分において改善の余地あり
        EventDateTime startEventDateTime = new EventDateTime().setDateTime(new DateTime(startDateTime.toString() + ":00+09:00")); // イベント開始日時
        EventDateTime endEventDateTime = new EventDateTime().setDateTime(new DateTime(endDateTime.toString() + ":00+09:00")); // イベント終了日時
        
        //イベントのサマリーをタスク名に設定
        String summary = task.getTaskName();
        
        //上記サマリー、開始時刻、終了時刻をセットしたイベントを生成
        Event event = new Event()
        .setSummary(summary)
        .setStart(startEventDateTime)
        .setEnd(endEventDateTime);
        
        //Calendarのinsertメソッドの引数gcal_idを設定
        //primaryでメインカレンダーとなる
        String gcal_id = "primary";
        
        //イベント追加処理
        event = service.events().insert(gcal_id, event).execute();
        
        return event.getId();

    }

    //GoogleCalendar　イベント追加test
    // public String addEventTest(OAuth2AuthenticationToken authenticationToken) throws GeneralSecurityException, IOException {
    
    //     final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    
    //     Calendar service = new Calendar
    //         .Builder(HTTP_TRANSPORT, 
    //                     JSON_FACTORY, 
    //                     getCredentials(authenticationToken))
    //         .setApplicationName(APPLICATION_NAME)
    //         .build();
    
    //     EventDateTime startEventDateTime = new EventDateTime().setDateTime(new DateTime("2021-07-19T17:00:00+09:00")); // イベント開始日時
    //     EventDateTime endEventDateTime = new EventDateTime().setDateTime(new DateTime("2021-07-19T18:00:00+09:00")); // イベント終了日時
    
    //     String summary = "テスト";
    //     String description = "テスト";
    
    //     Event event = new Event()
    //         .setSummary(summary)
    //         .setDescription(description)
    //         .setColorId("2") // green
    //         .setStart(startEventDateTime)
    //         .setEnd(endEventDateTime);
    
    //     String gcal_id = "primary";
    
    //     event = service.events().insert(gcal_id, event).execute();
    
    //     return event.getId();
    // }
    
}