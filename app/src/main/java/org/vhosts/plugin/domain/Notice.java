package org.vhosts.plugin.domain;

import java.io.Serializable;

public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private Boolean show;

    private String msg;

    private String confirm;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
}
