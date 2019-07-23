package com.planetwalk.ponion.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.planetwalk.ponion.PonionApplication;
import com.planetwalk.ponion.Utils.CommonUtils;
import com.planetwalk.ponion.Utils.ResultCode;
import com.planetwalk.ponion.db.converter.DateConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessageService extends IntentService {

    public MessageService() {
        super("messageservice");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        int petId = intent.getIntExtra(MainActivity.EXTRA_PET_ID, 0);

        return;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PonionApplication application = (PonionApplication) getApplication();
//        application.getAppExecutors().networkIO().execute(() -> {
//            try {
//                Response response = requestMessage(this);
//                final String res = response.body().string();
//                JSONObject jsonObject = new JSONObject(res);
//                int code = jsonObject.getInt("code");
//                if (code == ResultCode.SUCCESS.code()) {
//                    JSONArray msgJSONArray = jsonObject.getJSONObject("data").getJSONArray("list");
////                    "fromId":24,
////                            "gmtCreate":1557704742229,
////                            "gmtModified":1557704742229,
////                            "id":12,
////                            "msg":"爱的发声！！！",
////                            "petId":11
//                    int size = msgJSONArray.length();
//                    List<MessageEntity> messageEntityList = new ArrayList<>(size);
//                    long lastmsgId = 0;
//                    for (int i = size - 1; i >= 0; i--) {
//                        JSONObject msgJSONObject = (JSONObject) msgJSONArray.get(i);
//                        MessageEntity msgEntity = new MessageEntity();
//                        msgEntity.fromId = msgJSONObject.optLong("fromId", 0);
//                        msgEntity.gmtCreate = DateConverter.toDate(msgJSONObject.getLong("gmtModified"));
//                        msgEntity.serverId = msgJSONObject.getLong("id");
//                        if (msgEntity.serverId > lastmsgId) {
//                            lastmsgId = msgEntity.serverId;
//                        }
//                        msgEntity.message = msgJSONObject.getString("msg");
//                        msgEntity.petId = msgJSONObject.getLong("petId");
//                        messageEntityList.add(msgEntity);
//                    }
//
//                    if (lastmsgId > 0) {
//                        application.getDatabase().messageDao().insertAll(messageEntityList);
//                        CommonUtils.writeLastMsgId(getApplicationContext(), lastmsgId);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        });

        return START_STICKY;
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, MessageService.class);
        context.startService(intent);
    }

//    public static void sendMessage(Context context, int petId, String msg){
//        Intent intent = new Intent(context, MessageService.class);
//        intent.putExtra(MainActivity.EXTRA_PET_ID, petId);
//    }

    public static Response requestMessage(Context context) throws IOException {
        String url = "http://39.105.222.203:8085/pet/message/msgs";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("token", CommonUtils.getToken(context.getApplicationContext()));
        formBuilder.add("lastMsgId", String.valueOf(CommonUtils.getLastMsgId(context.getApplicationContext())));
        formBuilder.add("page", String.valueOf(0));
        formBuilder.add("size", String.valueOf(20));

        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response;
    }

    public static Response sendMessage(Context context, int petId, String msg) throws IOException {
        String url = "http://39.105.222.203:8085/pet/message/add";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("token", CommonUtils.getToken(context.getApplicationContext()));
        formBuilder.add("petId", String.valueOf(petId));
        formBuilder.add("msg", String.valueOf(msg));

        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response;
    }

    public static void sendMessageAndSyncFromServer(Context context, int petId, String msg) {
        try {
            Response response = MessageService.sendMessage(context, petId, msg);
            final String res = response.body().string();
            JSONObject jsonObject = new JSONObject(res);
            int code = jsonObject.getInt("code");
            if (code == ResultCode.SUCCESS.code()) {
                MessageService.startService(context);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
