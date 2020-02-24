package com.xmrbi.test;

import com.xmrbi.pojo.AccessToken;
import com.xmrbi.translate.TransApi;
import com.xmrbi.util.TranslatUtil;
import com.xmrbi.util.WeixinUtil;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class WeixinTest {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        //获取token
        AccessToken token = WeixinUtil.getAccessToken();
        System.out.println("票据:   " + token.getAccessToken());
        System.out.println("有效时间:  " + token.getExpiresIn());
//----------------------------------------------------------------------------
//        for (int i = 0; i < 1000000; i++) {
//            System.out.print("");
//        }
//        System.out.println("");
//        AccessToken token1 = WeixinUtil.getAccessToken();
//        System.out.println("票据:   "+token1.getAccessToken());
//        System.out.println("有效时间:  "+token1.getExpiresIn());

        //图片文件上传
//        String path = "D:/test.jpg";
//        String mediaId = WeixinUtil.upload(path,token.getAccessToken(),"image");
//        System.out.println(mediaId);
        //缩略图文件上传
//        String path = "D:/cat.jpg";
//        String mediaId = WeixinUtil.upload(path,token.getAccessToken(),"thumb");
//        System.out.println(mediaId);

//        String menu = JSON.toJSONString(WeixinUtil.initMenu());
//        String menu = net.sf.json.JSONObject.fromObject(WeixinUtil.initMenu()).toString();

        //两种方法的区别  net.sf.json.JSONObject的方法，将对象为空的属性也会转化出来
//        {"buttons":[{"key":"11","name":"click菜单","type":"click"},{"name":"view菜单","type":"view","url":"http://www.imooc.com"},{"name":"菜单","sub_button":[{"key":"31","name":"扫描事件","type":"scancode_push"},{"key":"32","name":"地理位置","type":"location_select"}]}]}
//        {"buttons":[{"key":"11","name":"click菜单","sub_button":[],"type":"click"},{"name":"view菜单","sub_button":[],"type":"view","url":"http://www.imooc.com"},{"name":"菜单","sub_button":[{"key":"31","name":"扫描事件","sub_button":[],"type":"scancode_push"},{"key":"32","name":"地理位置","sub_button":[],"type":"location_select"}],"type":""}]}
//        System.out.println(menu);
//        创建自定义菜单
//        int result = WeixinUtil.createMenu(token.getAccessToken(), menu);
//        if (result == 0) {
//            System.out.println("菜单创建成功");
//        } else {
//            System.out.println("菜单创建失败，状态码：" + result);
//        }
        //查询自定义菜单
//        JSONObject jsonObject = WeixinUtil.queryMenu(token.getAccessToken());
//        System.out.println(jsonObject);

//        //删除自定义菜单
//        int errcode = WeixinUtil.deleteMenu(token.getAccessToken());
//        if (errcode == 0) {
//            System.out.println("菜单删除成功");
//        } else {
//            System.out.println("菜单删除失败+" + errcode);
//        }
        //调用百度翻译
//        String query = "beach";
////        String result = WeixinUtil.translate(query);
////        System.out.println(result);
//        //调用百度翻译
//        String result = TranslatUtil.getBaiduTranslate(query, "auto", "auto");
//        System.out.println(result);
    }
}
