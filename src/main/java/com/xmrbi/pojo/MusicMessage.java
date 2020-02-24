package com.xmrbi.pojo;

/**
 * 音乐消息对象
 */
public class MusicMessage extends BaseMessage {
    private Music Music;

    public com.xmrbi.pojo.Music getMusic() {
        return Music;
    }

    public void setMusic(com.xmrbi.pojo.Music music) {
        Music = music;
    }
}
