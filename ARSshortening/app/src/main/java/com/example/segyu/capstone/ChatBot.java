package com.example.segyu.capstone;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.GsonFactory;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIEvent;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;

public class ChatBot extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = ChatBot.class.getName();
    private Gson gson = GsonFactory.getGson();
    private AIDataService aiDataService;
    private ChatView chatView;
    private User myAccount;
    private User SOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot_layout);

        initChatView();

        //Language, Dialogflow Client access token
        final LanguageConfig config = new LanguageConfig("ko", "344ccce21ed94319b837eec664d3916c");
        initService(config);
    }

    @Override
    public void onClick(View v) {
        //new message
        final Message message = new Message.Builder()
                .setUser(myAccount)
                .setRightMessage(true)
                .setMessageText(chatView.getInputText())
                .hideIcon(true)
                .build();
        //Set to chat view
        chatView.send(message);
        sendRequest(chatView.getInputText());
        //Reset edit text
        chatView.setInputText("");
    }

    /*
     * AIRequest should have query OR event
     */
    private void sendRequest(String text) {
        Log.d(TAG, text);
        final String queryString = String.valueOf(text);
        final String eventString = null;
        final String contextString = null;

        if (TextUtils.isEmpty(queryString) && TextUtils.isEmpty(eventString)) {
            onError(new AIError(getString(R.string.non_empty_query)));
            return;
        }

        //AI response 객체
        new AiTask().execute(queryString, eventString, contextString);
    }

    //AI response 틀
    //여기서 case조정으로 다른 액티비티를 부를 수 있으면 ok
    public class AiTask extends AsyncTask<String, Void, AIResponse> {
        private AIError aiError;

        @Override
        protected AIResponse doInBackground(final String... params) {
            final AIRequest request = new AIRequest();
            String query = params[0];
            String event = params[1];
            String context = params[2];

            if (!TextUtils.isEmpty(query)) {
                request.setQuery(query);
            }

            if (!TextUtils.isEmpty(event)) {
                request.setEvent(new AIEvent(event));
            }

            RequestExtras requestExtras = null;
            if (!TextUtils.isEmpty(context)) {
                final List<AIContext> contexts = Collections.singletonList(new AIContext(context));
                requestExtras = new RequestExtras(contexts, null);
            }

            try {
                return aiDataService.request(request, requestExtras);
            } catch (final AIServiceException e) {
                aiError = new AIError(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final AIResponse response) {
            if (response != null) {
                onResult(response);
            } else {
                onError(aiError);
            }
        }
    }


    private void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Variables
                gson.toJson(response);
                final Status status = response.getStatus();
                final Result result = response.getResult();
                final String speech = result.getFulfillment().getSpeech();
                final Metadata metadata = result.getMetadata();
                final HashMap<String, JsonElement> params = result.getParameters();

                // Logging
                Log.d(TAG, "onResult");
                Log.i(TAG, "Received success response");
                Log.i(TAG, "Status code: " + status.getCode());
                Log.i(TAG, "Status type: " + status.getErrorType());
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());
                Log.i(TAG, "Action: " + result.getAction());
                Log.i(TAG, "Speech: " + speech);

                if (metadata != null) {
                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
                }

                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, "Parameters: ");
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        Log.i(TAG, String.format("%s: %s",
                                entry.getKey(), entry.getValue().toString()));
                    }
                }

                //Update view to bot says
                final Message receivedMessage = new Message.Builder()
                        .setUser(SOS)
                        .setRightMessage(false)
                        .setMessageText(speech)
                        .build();
                /*final Message receivedMessage1 = new Message.Builder()
                        .setUser(SOS)
                        .setRightMessage(false)
                        .setMessageText(speech)
                        .build();
*/

                String userBirthday;
                //카드 해지 전화번호 띄우기
                if (speech.equals("(개인)신용카드/체크카드 분실관련 ARS에 연결해드리겠습니다.")) {
                    //    연동확인 : 같은 내용이 2번 뜨는가? : 연동확인 완료
                                        /*
                                 final Message receivedMessage1 = new Message.Builder()
                                                                    .setUser(SOS)
                                                                    .setRightMessage(false)
                                                                    .setMessageText(speech)
                                                                        .build();
                                                                     chatView.receive(receivedMessage1);
                                        */

                    /////////////////////////  통화연결  ////////////
                    if (ContextCompat.checkSelfPermission(ChatBot.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ChatBot.this, new String[]{Manifest.permission.CALL_PHONE
                        }, 1);
                    } else {
                        call();
                    }
                }




                //지도 띄우기 - 성범이형꺼 코드 붙이기
                if (speech.equals("카드해지 관련 ARS에 연결해드리겠습니다.")) {
                }







                chatView.receive(receivedMessage);
                //chatView.receive(receivedMessage1);


            }
        });
    }

    private void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, error.toString());

            }
        });
    }

    private void initChatView() {
        int myId = 0;
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.sunwooo);

        String myName = "강한새우";
        myAccount = new User(myId, myName, icon);

        int botId = 1;
        String botName = "SOS";
        SOS = new User(botId, botName, icon);

        chatView = findViewById(R.id.chat_view);
        chatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.colorYellow));//나의 메세지 색깔
        chatView.setLeftBubbleColor(R.color.colorsky);
        chatView.setBackground(ContextCompat.getDrawable(this, R.drawable.mainback3));
        chatView.setSendButtonColor(ContextCompat.getColor(this, R.color.colorBlue));
        chatView.setSendIcon(R.drawable.ic_action_send);
        chatView.setRightMessageTextColor(Color.WHITE);
        chatView.setLeftMessageTextColor(Color.WHITE);
        chatView.setUsernameTextColor(Color.WHITE);
        chatView.setSendTimeTextColor(Color.WHITE);
        chatView.setDateSeparatorColor(Color.WHITE);
        chatView.setInputTextHint("내용을 입력해주세요.");
        chatView.setMessageMarginTop(5);
        chatView.setMessageMarginBottom(5);
        chatView.setOnClickSendButtonListener(this);
    }

    private void initService(final LanguageConfig languageConfig) {
        final AIConfiguration.SupportedLanguages lang =
                AIConfiguration.SupportedLanguages.fromLanguageTag(languageConfig.getLanguageCode());
        final AIConfiguration config = new AIConfiguration(languageConfig.getAccessToken(),
                lang,
                AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(this, config);
    }


    private void call() {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);

            //일단은 신용카드/체크카드 분실 서비스만
            //이 작업 시, 다음차례 작업: 생년월일 입력 --> 카드번호 입력
            intent.setData(Uri.parse("tel:" + "15889999,*,1,,,,*"));

            startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();

        }
    }


    //필요한가?

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call();
                } else {
                    Toast.makeText(this, "asdf", Toast.LENGTH_SHORT).show();
                }
        break;
        default:
    }
            }
            }