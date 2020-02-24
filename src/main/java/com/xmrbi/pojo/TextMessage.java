package com.xmrbi.pojo;

/**
 * 消息
 *
 * @author Eric Dong
 * @date 19-10-11 15-58-04
 */

public class TextMessage extends BaseMessage {

    //文本消息内容
    private String Content;
    //消息id
    private String MsgId;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }
}
