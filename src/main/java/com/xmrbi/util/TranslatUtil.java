package com.xmrbi.util;

import com.xmrbi.translate.MD5;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class TranslatUtil {

    private static final String APP_ID = "20191017000342153";
    private static final String SECURITY_KEY = "Ji_7MoEl7lpPzHb5iD22";
    private static final String BAIDU_TRANSLATE_URL = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    /**
     * get 请求 ：get方式是通过接口地址从微信后台获取需要的信息
     *
     * @param url
     * @return
     */
    public static JSONObject doGetStr(String url) {

        //DefaultHttpClient已经被取消，可以用如下方式替代
        HttpClient httpClient = HttpClientBuilder.create().build();
        //get请求
        HttpGet httpGet = new HttpGet(url);
        //最终要返回的JSONObject对象
        JSONObject jsonObject = null;

        try {
            //通过httpClient执行get请求
            HttpResponse response = httpClient.execute(httpGet);
            //从响应中获取响应体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity, "UTF-8");
                jsonObject = JSONObject.fromObject(result);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 获取百度翻译结果
     * @param query
     * @param from
     * @param to
     * @return
     */
    public static String getBaiduTranslate(String query, String from, String to) {
        //参数准备
        Map<String, String> params = buildParams(query, from, to);
        //请求接口url拼接
        String url = getUrlWithQueryString(BAIDU_TRANSLATE_URL, params);
        //get请求
        JSONObject jsonObject = doGetStr(url);
        //查询结果数据处理
        JSONObject transResult = JSONObject.fromObject(jsonObject.getString("trans_result").replace("[", "").replace("]", "").trim());
        String dst = transResult.getString("dst");
        return dst;
    }

    /**
     * 调用百度翻译接口的 必备参数准备
     *
     * @param query
     * @param from  从什么语言
     * @param to    转换为什么语言
     * @return
     */
    private static Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", APP_ID);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = APP_ID + query + salt + SECURITY_KEY; // 加密前的原文
        params.put("sign", MD5.md5(src));

        return params;
    }

    /**
     * 百度接口的请求url拼接
     *
     * @param url
     * @param params
     * @return
     */
    private static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }

    /**
     * 对输入的字符串进行URL编码, 即转换为%20这种形式
     *
     * @param input 原文
     * @return URL编码. 如果编码失败, 则返回原文
     */
    private static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }
}
