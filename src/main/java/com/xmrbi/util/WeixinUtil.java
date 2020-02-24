package com.xmrbi.util;

import com.xmrbi.pojo.AccessToken;
import com.xmrbi.pojo.menu.Button;
import com.xmrbi.pojo.menu.ClickButton;
import com.xmrbi.pojo.menu.Menu;
import com.xmrbi.pojo.menu.ViewButton;
import com.xmrbi.translate.TransApi;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;

/*
 *通过http请求对接口进行访问
 */
public class WeixinUtil {

    //微信测试账号的信息
    //公众号id 和 公众号秘钥
    private static final String APPID = "wx483aa7348a06923c";
    private static final String APPSECRET = "0a6b07d850797a8694b9859e68b45dde";

    //ACCESS_TOKEN获取 的接口地址
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    //上传文件接口地址 到公众号可以新增临时素材（即上传临时多媒体文件）。
    //返回{"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789} 主要使用media_id做图片消息回复
    private static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

    //自定义菜单的创建
    private static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    //自定义菜单的查询
    private static final String QUERY_MENU_url = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=ACCESS_TOKEN";
    //自定义菜单的删除
    private static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
    private static String TOKEN = "";
    private static long tokenInitTime = 0L;

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
     * post 请求  post请求是通过接口地址，将参数提交到微信后台
     *
     * @param url    请求接口地址
     * @param outStr 请求参数
     * @return
     */
    public static JSONObject doPostStr(String url, String outStr) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        JSONObject jsonObject = null;
        try {
            httpPost.setEntity(new StringEntity(outStr, "UTF-8"));
            //请求执行  响应结果response
            HttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            jsonObject = JSONObject.fromObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 获取Access_token
     *
     * @return
     */
    public static AccessToken getAccessToken() {

        AccessToken token = new AccessToken();
        String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
        //通过拼接获取accesstoken的接口url appid appsecret，进行请求，响应结果为jsonobject
        JSONObject jsonObject = doGetStr(url);
        if (jsonObject != null) {
            token.setAccessToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getInt("expires_in"));
        }
        return token;
    }

    /**
     * 获取Access_token  自己测试 2小时内的请求中使用同一个token
     * <p>
     * 通过比较初始token的时间和当前时间的差 和7200秒进行比较
     *
     * @return
     */
    public static AccessToken getAccessToken1() {

        AccessToken token = new AccessToken();
        long subTime = new Date().getTime() - tokenInitTime;//差值时间 现在时间和token初始化时间
        if (tokenInitTime == 0 || subTime >= 7200 * 1000) {
            String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
            //通过拼接获取accesstoken的接口url appid appsecret，进行请求，响应结果为jsonobject
            JSONObject jsonObject = doGetStr(url);
            if (jsonObject != null) {
                token.setAccessToken(jsonObject.getString("access_token"));
                TOKEN = jsonObject.getString("access_token");
                token.setExpiresIn(jsonObject.getInt("expires_in"));
                tokenInitTime = new Date().getTime();
            }
        } else {
            token.setAccessToken(TOKEN);
            token.setExpiresIn((int) ((7200 * 1000 - subTime) / 1000));
        }
        return token;
    }

    /**
     * 文件上传
     *
     * @param filePath
     * @param accessToken
     * @param type
     * @return
     * @throws IOException
     */
    public static String upload(String filePath, String accessToken, String type) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }
        //对接口中参数进行赋值
        String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);
        URL urlObj = new URL(url);

        //http连接  http请求方式：POST/FORM
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);//post方式要忽略缓存

        //设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");

        //设置边界
        String BOUNDARY = "-------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

        StringBuilder sb = new StringBuilder();
        sb.append("--");
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");
        byte[] head = sb.toString().getBytes("utf-8");

        //获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        //输出表头
        out.write(head);

        //文件正文部分
        //把文件以流的方式推入到url中
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) { //按1024字节数组读取
            out.write(bufferOut, 0, bytes);
        }
        in.close();

        //结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//定义最后数据分隔线

        out.write(foot);
        out.flush();
        out.close();

        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        String result = null;

        try {
            //定义BufferReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) { //按行读取
                buffer.append(line);
            }
            if (result == null) {
                result = buffer.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        JSONObject jsonObj = JSONObject.fromObject(result);
        System.out.println(jsonObj);
        String typeName = "media_id";
        if (!"image".equals(type)) {
            typeName = type + "_media_id";
        }
        String mediaId = jsonObj.getString(typeName);

        return mediaId;
    }

    /**
     * 文件上传 视频方法
     *
     * @param filePath
     * @param accessToken
     * @param type
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws KeyManagementException
     */
    public static String upload1(String filePath, String accessToken, String type) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }

        String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);

        URL urlObj = new URL(url);
        //连接
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        //设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");

        //设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        StringBuilder sb = new StringBuilder();
        sb.append("--");
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");

        byte[] head = sb.toString().getBytes("utf-8");

        //获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        //输出表头
        out.write(head);

        //文件正文部分
        //把文件已流文件的方式 推入到url中
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();

        //结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//定义最后数据分隔线

        out.write(foot);

        out.flush();
        out.close();

        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        String result = null;
        try {
            //定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (result == null) {
                result = buffer.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        JSONObject jsonObj = JSONObject.fromObject(result);
        System.out.println(jsonObj);
        String typeName = "media_id";
        if (!"image".equals(type)) {
            typeName = type + "_media_id";
        }
        String mediaId = jsonObj.getString(typeName);
        return mediaId;
    }

    /**
     * 自定义菜单初始化
     *
     * @return
     */
    public static Menu initMenu() {
        Menu menu = new Menu();
        //每个菜单按钮
        ClickButton button11 = new ClickButton();
        button11.setName("click菜单");
        button11.setType("click");
        button11.setKey("11");

        ViewButton button21 = new ViewButton();
        button21.setName("view菜单");
        button21.setType("view");
        button21.setUrl("http://www.imooc.com");

        ClickButton button31 = new ClickButton();
        button31.setName("扫描事件");
        button31.setType("scancode_push");
        button31.setKey("31");

        ClickButton button32 = new ClickButton();
        button32.setName("地理位置");
        button32.setType("location_select");
        button32.setKey("32");

        //二级菜单31/32的组装
        Button button = new Button();
        button.setName("菜单");
        button.setSub_button(new Button[]{button31, button32});
        //主菜单的组装
        menu.setButton(new Button[]{button11, button21, button});
        return menu;
    }

    /**
     * 创建自定义菜单
     *
     * @param token
     * @param menu
     * @return
     */
    public static int createMenu(String token, String menu) {
        int result = 0;
        String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", token);
        JSONObject jsonObject = doPostStr(url, menu);
        if (jsonObject != null) {
            result = jsonObject.getInt("errcode");
        }
        return result;
    }

    /**
     * 查询自定义菜单
     *
     * @param token
     * @return
     */
    public static JSONObject queryMenu(String token) {
        String url = QUERY_MENU_url.replace("ACCESS_TOKEN", token);
        JSONObject jsonObject = doGetStr(url);
        return jsonObject;
    }

    public static int deleteMenu(String token) {
        String url = DELETE_MENU_URL.replace("ACCESS_TOKEN", token);
        JSONObject jsonObject = doGetStr(url);
        int result = 0;
        if (jsonObject != null) {
            result = jsonObject.getInt("errcode");
        }
        return result;
    }

    /**
     * 调用百度翻译接口
     *
     * @param query
     * @return
     */
// 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20191017000342153";
    private static final String SECURITY_KEY = "Ji_7MoEl7lpPzHb5iD22";

    public static String translate(String query) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);

        JSONObject result = api.getTransResult(query, "auto", "en");
        JSONObject transResult = JSONObject.fromObject(result.getString("trans_result").replace("[","").replace("]","").trim());
        String dst = transResult.getString("dst");
        return dst;
    }
}
