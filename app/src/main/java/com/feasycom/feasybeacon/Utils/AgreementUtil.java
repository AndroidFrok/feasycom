package com.feasycom.feasybeacon.Utils;

import android.util.Log;

import com.feasycom.feasybeacon.Activity.AgreementActivity;
import com.feasycom.feasybeacon.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

interface CALLBACK{
    interface AgreementComplete{
        void Complete(Map re);
    }
}

public class AgreementUtil implements CALLBACK {
    public static void requestAgreement(int fbType,
                                  final AgreementComplete complete){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //需要在子线程中处理的逻辑
                try {
                    // 网络请求
                    URL url = new URL("https://api.feasycom.com/agreement");
                    //2. HttpURLConnection
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    //3. POST
                    conn.setRequestMethod("POST");
                    //4. Content-Type,这里是固定写法，发送内容的类型
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("local", "en");
                    //5. output，这里要记得开启输出流，将自己要添加的参数用这个输出流写进去，传给服务端，这是socket的基本结构
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    // 发送请求
                    os.write(params(fbType));
                    os.flush();
                    os.close();
                    int resultCode = conn.getResponseCode();            //获得服务器的响应码
                    if(HttpURLConnection.HTTP_OK==resultCode){
                        StringBuffer sb=new StringBuffer();
                        String readLine= "";
                        BufferedReader responseReader=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                        while((readLine=responseReader.readLine())!=null){
                            sb.append(readLine).append("\n");
                        }
                        responseReader.close();
                        complete.Complete(jsonToMap(sb.toString()));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 请求参数
     * @param fbType 协议类型
     * */
    private static byte[] params(int fbType){
        JSONObject map = new JSONObject();
        try {
            map.put("app", "beacon");
            map.put("type", fbType);
            return map.toString().getBytes();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "".getBytes();
    }

    /**
     * String 转 Map
     * @param string String
     * 返回值:Map
     */
    public static Map<String, Object> jsonToMap(String string) {
        string = string.trim();
        Map<String, Object> result = new HashMap<>();
        try {
            if (string.charAt(0) == '[') {
                JSONArray jsonArray = new JSONArray(string);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object value = jsonArray.get(i);
                    if (value instanceof JSONArray || value instanceof JSONObject) {
                        result.put(i + "", jsonToMap(value.toString().trim()));
                    } else {
                        result.put(i + "", jsonArray.getString(i));
                    }
                }
            } else if (string.charAt(0) == '{'){
                JSONObject jsonObject = new JSONObject(string);
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = jsonObject.get(key);
                    if (value instanceof JSONArray || value instanceof JSONObject) {
                        result.put(key, jsonToMap(value.toString().trim()));
                    } else {
                        result.put(key, value.toString().trim());
                    }
                }
            }else {
                Log.e("异常", "json2Map: 字符串格式错误");
            }
        } catch (JSONException e) {
            Log.e("异常", "json2Map: ", e);
            result = null;
        }
        return result;
    }
}
