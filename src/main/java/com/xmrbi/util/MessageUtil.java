package com.xmrbi.util;

import com.thoughtworks.xstream.XStream;
import com.xmrbi.pojo.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * xml消息和集合的相互转化 工具类
 *
 * @author Eric Dong
 * @date 19-10-11 14-46-00
 */

/*文本消息 text
图片消息 image
语音消息 voice
视频消息 video
	小视频 shortvideo
链接消息 link
地理位置消息 location
事件推送 event
	关注 subscribe
	取消关注 unsubscribe
	菜单点击 CLICk VIEW
*/
public class MessageUtil {

    //内网穿透的映射url
    public static final String NATAPPURL = "http://ywh38x.natappfree.cc";

    public static final String MESSAGE_TEXT = "text";
    public static final String MESSAGE_NEWS = "news";
    public static final String MESSAGE_IMAGE = "image";
    public static final String MESSAGE_VOICE = "voice";
    public static final String MESSAGE_VIDEO = "video";
    public static final String MESSAGE_MUSIC = "music";
    public static final String MESSAGE_SHORTVIDEO = "shortvideo";
    public static final String MESSAGE_LOCATION = "location";
    public static final String MESSAGE_LINK = "link";
    public static final String MESSAGE_EVENT = "event";
    public static final String MESSAGE_SUBSCRIBE = "subscribe";
    public static final String MESSAGE_UNSUBSCRIBE = "unsubscribe";
    public static final String MESSAGE_CLICK = "CLICK";
    public static final String MESSAGE_VIEW = "VIEW";
    public static final String MESSAGE_SCANCODE_PUSH = "scancode_push";
    public static final String MESSAGE_LOCATION_SELECT = "location_select";

    /**
     * xml转化为map集合  dom4j
     *
     * @param request
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static Map<String, String> xmlToMap(HttpServletRequest request) throws IOException, DocumentException {

        HashMap<String, String> map = new HashMap<String, String>();

        //dom4j的输入流读取对象SAXReader
        SAXReader reader = new SAXReader();
        //从请求中获取输入流
        InputStream ins = request.getInputStream();
        //读取输入流中的数据Document
        Document doc = reader.read(ins);

        //获取document的根元素,并将根元素放到集合中
        Element rootElement = doc.getRootElement();
        List<Element> list = rootElement.elements();

        //遍历集合将标签的名称name和标签的内容Text分别放到Map的key和value中
        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        ins.close();

        return map;
    }

    /**
     * @Description: 将文本消息对象转化为xml
     * @Param: [textMessage]
     * @return: java.lang.String
     */
    public static String textMessageToXml(TextMessage textMessage) {
        XStream xStream = new XStream();
        //注意：要将跟标签转化为<xml>
        xStream.alias("xml", textMessage.getClass());
        return xStream.toXML(textMessage);
    }

    /**
     * 初始化消息 方法  (消息的交互都是通过xml的方式)
     * <p>
     * 目的：将content的内容  最终转化xml类型的string
     *
     * @param toUserName
     * @param fromUserName
     * @param content
     * @return
     */
    public static String initText(String toUserName, String fromUserName, String content) {
        TextMessage textMessage = new TextMessage();
        //因为是相应的消息 所以：toUserName和fromUserName位置互换
        textMessage.setFromUserName(toUserName);
        textMessage.setToUserName(fromUserName);
        textMessage.setMsgType(MESSAGE_TEXT);
        textMessage.setCreateTime(String.valueOf(new Date().getTime()));
        textMessage.setContent(content);
        //将消息转化为xml类型
        return MessageUtil.textMessageToXml(textMessage);
    }

    /**
     * 定义主菜单
     *
     * @return
     */
    public static String menuText() {
        StringBuffer sb = new StringBuffer();
        sb.append("欢迎您的关注，请按照菜单提示进行操作：\n\n");
        sb.append("1、课程介绍\n");
        sb.append("2、图文回复\n");
        sb.append("3、图片回复\n");
        sb.append("4、音乐回复\n");
        sb.append("5、百度翻译\n\n");
        sb.append("回复? 显示主菜单。");
        return sb.toString();
    }

    /**
     * 自动回复“1”的 菜单内容
     *
     * @return
     */
    public static String firstMenu() {
        StringBuffer sb = new StringBuffer();
        sb.append("您回复了“1” :本课程介绍微信公众号开发");
        return sb.toString();
    }

    /**
     * 自动回复“2”的 菜单内容
     *
     * @return
     */
    public static String secondMenu() {
        StringBuffer sb = new StringBuffer();
        sb.append("您回复了“2” ：慕课网......");
        return sb.toString();
    }

    /**
     * 自动回复“5”的 菜单内容
     *
     * @return
     */
    public static String fiveMenu() {
        StringBuffer sb = new StringBuffer();
        sb.append("翻译：\r\n");
        sb.append("使用示例\r\n");
        sb.append("翻译足球\r\n");
        sb.append("翻译football\r\n\n");
        sb.append("回复? 显示主菜单。");
        return sb.toString();
    }

    /**
     * 将图文消息转换为xml
     *
     * @param newsMessage
     * @return
     */
    public static String newsMessageToXml(NewsMessage newsMessage) {
        XStream xStream = new XStream();
        //注意：要将跟标签转化为<xml>
        xStream.alias("xml", newsMessage.getClass());
        xStream.alias("item", News.class);
        return xStream.toXML(newsMessage);
    }

    /**
     * @Description: 图文消息的组装
     * @Param: [toUserName, fromUserName]
     * @return: java.lang.String
     */
    public static String initNewsMessage(String toUserName, String fromUserName) {
        String message = null;
        List<News> newsList = new ArrayList<>();
        NewsMessage newsMessage = new NewsMessage();

        //消息体的设置
        News news = new News();
        news.setTitle("慕课网介绍");
        news.setDescription("这是慕课网的介绍.....");
        news.setPicUrl(NATAPPURL + "/weixin-study/img/超人.jpg");
        news.setUrl("www.imooc.com");
        newsList.add(news);

        //图文消息的设置
        newsMessage.setToUserName(fromUserName);
        newsMessage.setFromUserName(toUserName);
        newsMessage.setCreateTime(String.valueOf(new Date().getTime()));
        newsMessage.setMsgType(MESSAGE_NEWS);
        newsMessage.setArticles(newsList);
        newsMessage.setArticleCount(newsList.size());

        //将图文消息转换为xml
        message = newsMessageToXml(newsMessage);
        return message;
    }

    /**
     * 将图片消息转换为xml
     *
     * @param imageMessage
     * @return
     */
    public static String imageMessageToXml(ImageMessage imageMessage) {
        XStream xStream = new XStream();
        //注意：要将跟标签转化为<xml>
        xStream.alias("xml", imageMessage.getClass());
        return xStream.toXML(imageMessage);
    }

    /**
     * 图片消息的组装
     *
     * @param toUserName
     * @param fromUserName
     * @return
     */
    public static String initImageMessage(String toUserName, String fromUserName) {
        String message = null;
        Image image = new Image();
        image.setMediaId("vBGvZYmLP8RFPmKFtke2BglnWE6B_Y8YMVAdrSepBcKm9Lu_eFEy1o9cJgwV-rWN");
        ImageMessage imageMessage = new ImageMessage();
        imageMessage.setFromUserName(toUserName);
        imageMessage.setToUserName(fromUserName);
        imageMessage.setMsgType(MESSAGE_IMAGE);
        imageMessage.setCreateTime(String.valueOf(new Date().getTime()));
        imageMessage.setImage(image);

        message = imageMessageToXml(imageMessage);
        return message;
    }

    /**
     * 将音乐消息转换为xml
     *
     * @param musicMessage
     * @return
     */
    public static String musicMessageToXml(MusicMessage musicMessage) {
        XStream xStream = new XStream();
        //注意：要将跟标签转化为<xml>
        xStream.alias("xml", musicMessage.getClass());
        return xStream.toXML(musicMessage);
    }

    /**
     * 图片消息的组装
     *
     * @param toUserName
     * @param fromUserName
     * @return
     */
    public static String initmusicMessage(String toUserName, String fromUserName) {
        String message = null;
        Music music = new Music();
        music.setTitle("see you again");
        music.setDescription("see you again,音乐回复");
        music.setMusicUrl(NATAPPURL + "/weixin-study/music/See You Again.mp3");
        music.setThumbMediaId("5Zhi5tzUjsrc6mcf1TR8foNqPOAkMquTq5dYR_x6SQhju_Pe2eNIZJ7YvAGCN3tl");
        music.setHQMusicUrl(NATAPPURL + "/weixin-study/music/See You Again.mp3");

        MusicMessage musicMessage = new MusicMessage();
        musicMessage.setFromUserName(toUserName);
        musicMessage.setToUserName(fromUserName);
        musicMessage.setMsgType(MESSAGE_MUSIC);
        musicMessage.setCreateTime(String.valueOf(new Date().getTime()));
        musicMessage.setMusic(music);

        message = musicMessageToXml(musicMessage);
        return message;
    }

}
