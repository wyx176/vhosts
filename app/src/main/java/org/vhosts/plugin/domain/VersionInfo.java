package org.vhosts.plugin.domain;

import android.os.Build;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class VersionInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @JSONField(name = "appInfo")
    private AppInfo appInfo;

    private Version version;

    private Notice notice;

    private List<String> hosts;

    private String hostsInfo;

    private DNS dns;

    private WebViewConfig webViewConfig;

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public String getHostsInfo()  {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && hosts!=null && hosts.size()>0) {
            this.hostsInfo = hosts.stream().collect(Collectors.joining("\r\n"));
        }
        return hostsInfo;
    }

    /***********************get and set***********************/

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public DNS getDns() {
        return dns;
    }

    public void setDns(DNS dns) {
        this.dns = dns;
    }

    public WebViewConfig getWebViewConfig() {
        return webViewConfig;
    }

    public void setWebViewConfig(WebViewConfig webViewConfig) {
        this.webViewConfig = webViewConfig;
    }
}
