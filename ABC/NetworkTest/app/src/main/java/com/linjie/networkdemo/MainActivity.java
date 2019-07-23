package com.linjie.networkdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView responseText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendRequest = (Button)findViewById(R.id.send_request);
        responseText = (TextView)findViewById(R.id.response_text);
        sendRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.send_request){
            Log.e("aaa","clicked is run");
//            sendRequestWithHttpURLConnection();
            sendRequestWithOkHttp();
        }

    }

    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://localhost:8081/get_data.json")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //parseXMLWithPull(responseData);
                    parseJSONWithJSONObject(responseData);
                 //   showResponse(responseData);
                }catch (Exception e){
                e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String responseData) {
        try{
            JSONArray jsonArray = new JSONArray(responseData);
            for(int i = 0;i < jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String version = jsonObject.getString("version");
                Log.d("MainActivity", "id is "+id);
                Log.d("MainActivity", "name is "+name);
                Log.d("MainActivity", "version is "+version);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseXMLWithPull(String responseData) {
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(responseData));
            int eventType = xmlPullParser.getEventType();
            String id="";
            String name="";
            String version="";
            while ((eventType != XmlPullParser.END_DOCUMENT)){
                String nodeName =xmlPullParser.getName();
                switch (eventType){
                    //开始解析某个节点
                    case XmlPullParser.START_TAG:{
                        if("id".equals(nodeName)){
                            id =  xmlPullParser.nextText();
                        }else if ("name".equals(nodeName)){
                            name = xmlPullParser.nextText();
                        }else if("version".equals(nodeName)){
                            version = xmlPullParser.nextText();
                        }
                        break;
                    }
                    //完成解析某个节点
                        case XmlPullParser.END_TAG:{
                            if("app".equals(nodeName)){
                                Log.d("MainActivity", "id is "+id);
                                Log.d("MainActivity", "name is "+name);
                                Log.d("MainActivity", "version is "+version);
                            }
                            break;
                        }
                        default:
                            break;
                }
                eventType = xmlPullParser.next();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendRequestWithHttpURLConnection() {

        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    Log.e("aaa","sendrequest is run");
                    URL url = new URL("https://www.baidu.com/");
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();

                    //对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    showResponse(response.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (reader != null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //UI操作
                Log.e("aaa","show is run");
                responseText.setText(response);
            }
        });
    }
}
