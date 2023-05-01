package org.vhosts.plugin.domain;

import java.io.Serializable;
import java.util.List;

public class WebViewConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String url;

    /**
     * 允许webview调起其它应用
     */
    private List<String> allowOpenUrls;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getAllowOpenUrls() {
        return allowOpenUrls;
    }

    public void setAllowOpenUrls(List<String> allowOpenUrls) {
        this.allowOpenUrls = allowOpenUrls;
    }
}
