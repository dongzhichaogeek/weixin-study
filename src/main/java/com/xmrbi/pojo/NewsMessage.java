package com.xmrbi.pojo;

import java.util.List;

/**
 * 图文消息实体
 *
 * @author Eric Dong
 * @date 19-10-12 16-23-20
 */

public class NewsMessage extends BaseMessage {
    private int ArticleCount;
    private List<News> Articles;

    public int getArticleCount() {
        return ArticleCount;
    }

    public void setArticleCount(int articleCount) {
        ArticleCount = articleCount;
    }

    public List<News> getArticles() {
        return Articles;
    }

    public void setArticles(List<News> articles) {
        Articles = articles;
    }
}
