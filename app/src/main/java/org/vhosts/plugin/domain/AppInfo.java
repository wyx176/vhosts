package org.vhosts.plugin.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class AppInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @JSONField
    private String appName;

    @JSONField(name = "helpBtnName")
    private String helpBtnName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHelpBtnName() {
        return helpBtnName;
    }

    public void setHelpBtnName(String helpBtnName) {
        this.helpBtnName = helpBtnName;
    }
}
