package org.vhosts.plugin.domain;

import java.io.Serializable;

public class Version implements Serializable {
    private static final long serialVersionUID = 1L;
    private String v;

    private String title;

    private String confirm;

    private String msg;

    private String url;

    public String getV() {
        return v;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
