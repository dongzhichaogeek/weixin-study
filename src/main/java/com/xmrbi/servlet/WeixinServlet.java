package com.xmrbi.servlet;

import com.xmrbi.pojo.TextMessage;
import com.xmrbi.util.CheckUtil;
import com.xmrbi.util.MessageUtil;
import com.xmrbi.util.TranslatUtil;
import org.dom4j.DocumentException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

public class WeixinServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //微信加密签名
        String signature = req.getParameter("signature");
        //时间戳
        String timestamp = req.getParameter("timestamp");
        //随机数
        String nonce = req.getParameter("nonce");
        //随机字符串
        String echostr = req.getParameter("echostr");

        //通过输出流响应
        PrintWriter out = resp.getWriter();
        //校验后返回字echostr符串
        if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
            out.print(echostr);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //对于请求和响应 要设置统一编码格式
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        //响应的  输出流
        PrintWriter out = resp.getWriter();

        //当普通微信用户向公众账号发消息时，微信服务器将POST消息的XML数据包到开发者填写的URL上。
        //因此接受消息时要将xml类型转换为集合类型
        //发送消息时也要将消息转化为xml类型
        try {
            //将xml类型转换为集合类型
            Map<String, String> map = MessageUtil.xmlToMap(req);
            System.out.println("微信用户端请求服务器的map:\n" + map);
            String toUserName = map.get("ToUserName");
            String fromUserName = map.get("FromUserName");
            String msgType = map.get("MsgType");
            String content = map.get("Content");

            String xmlMessage = null;
            //【1】文本消息
            if (MessageUtil.MESSAGE_TEXT.equals(msgType)) {
                //文本消息的 关键字回复
                if ("1".equals(content)) {
                    xmlMessage = MessageUtil.initText(toUserName, fromUserName, MessageUtil.firstMenu());
                } else if ("2".equals(content)) {
                    /* xmlMessage = MessageUtil.initText(toUserName, fromUserName, MessageUtil.secondMenu());*/
                    xmlMessage = MessageUtil.initNewsMessage(toUserName, fromUserName);
                } else if ("3".equals(content)) {
                    xmlMessage = MessageUtil.initImageMessage(toUserName, fromUserName);
                } else if ("4".equals(content)) {
                    xmlMessage = MessageUtil.initmusicMessage(toUserName, fromUserName);
                } else if ("5".equals(content)) {
                    xmlMessage = MessageUtil.initText(toUserName, fromUserName, MessageUtil.fiveMenu());
                } else if (content.contains("翻译")) {
                    String translationWord = content.replaceFirst("翻译", "").trim();//将content中的内容，开头为翻译的内容替换为空
                    if ("".equals(translationWord)) {
                        //如果去掉“翻译”后为空，返回翻译指示菜单
                        xmlMessage = MessageUtil.initText(toUserName, fromUserName, MessageUtil.fiveMenu());
                    } else {
                    xmlMessage = MessageUtil.initText(toUserName, fromUserName, TranslatUtil.getBaiduTranslate(translationWord,"auto","en"));
                    }
                } else if ("?".equals(content) || "？".equals(content)) {
                    xmlMessage = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
                } else {
                    xmlMessage = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
                }
                //【2】事件推送的逻辑
            } else if (MessageUtil.MESSAGE_EVENT.equals(msgType)) {
                //消息推送有三种类型 关注 取消关注 菜单点击
                String eventType = map.get("Event");
                System.out.println("event:    " + eventType);
                //事件推送的-关注事件的逻辑 ：关注后直接发送主菜单内容
                if (MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)) {
                    xmlMessage = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
                    //事件推送的-菜单点击的逻辑 ：点击不同的菜单，回复不同的消息
                } else if (MessageUtil.MESSAGE_CLICK.equals(eventType)) {
                    xmlMessage = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
                } else if (MessageUtil.MESSAGE_VIEW.equals(eventType)) { //当事件类型event是view时，EventKey是菜单跳转链接url
                    String url = map.get("EventKey");
                    xmlMessage = MessageUtil.initText(toUserName, fromUserName, url);
                } else if (MessageUtil.MESSAGE_SCANCODE_PUSH.equals(eventType)) {
                    String key = map.get("EventKey");
                    xmlMessage = MessageUtil.initText(toUserName, fromUserName, key);
                }
            } else if (MessageUtil.MESSAGE_LOCATION.equals(msgType)) {
                String label = map.get("Label");
                String location_x = map.get("Location_X");
                String location_y = map.get("Location_Y");
                StringBuilder sb = new StringBuilder();
                String location = sb.append("地理位置:" + label).append("\r\n").append("Location_X: " + location_x).append("\r\n").append("Location_Y: " + location_y).toString();
                xmlMessage = MessageUtil.initText(toUserName, fromUserName, location);
            }
            System.out.println("最终xmlMessage ：");
            System.out.println(xmlMessage);
            //响应消息 MESSAGE_LOCATION
            out.print(xmlMessage);
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            //关闭输出流
            out.close();
        }
    }
}
