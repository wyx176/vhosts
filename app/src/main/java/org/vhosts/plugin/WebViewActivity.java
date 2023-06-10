package org.vhosts.plugin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.webview.QMUIWebView;
import com.qmuiteam.qmui.widget.webview.QMUIWebViewClient;
import com.qmuiteam.qmui.widget.webview.QMUIWebViewContainer;

import org.vhosts.plugin.domain.OpenAction;
import org.vhosts.plugin.domain.VersionInfo;

import java.nio.charset.StandardCharsets;

public class WebViewActivity extends Activity {
    private QMUITopBarLayout topBar;
    private QMUIWebViewContainer qmuiWebcon;

    QDWebView mWebView;

    public static final  String HOST_DATA = "HOST_DATA";

    public static final  String SET_DNS = "http://open.action/";

    private VersionInfo info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_explorer);
        //接收数据
        info = JSONObject.parseObject(getIntent().getStringExtra(HOST_DATA),VersionInfo.class);
        //初始化bar
        initTopBar();
        initWebView();
    }

    protected void initTopBar(){
        topBar= (QMUITopBarLayout) findViewById(R.id.topbar);
        //开启沉浸式
        QMUIStatusBarHelper.translucent(this);
        topBar.setTitle(info.getWebViewConfig().getTitle()).setTextColor(ContextCompat.getColor(this,R.color.qmui_config_color_white));
        topBar.setBackgroundColor(ContextCompat.getColor(this,R.color.primary));
        topBar.setTitleGravity(Gravity.LEFT);
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接关闭当前页面
                finish();
            }
        });;

    }

    protected void initWebView(){
        qmuiWebcon= (QMUIWebViewContainer) findViewById(R.id.webview_container);
        mWebView = new QDWebView(this);
        boolean needDispatchSafeAreaInset =false;
        qmuiWebcon.addWebView(mWebView, needDispatchSafeAreaInset);
        //禁止调到系统浏览器
        mWebView.setWebViewClient(new QMUIWebViewClient(needDispatchSafeAreaInset,true){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if(request.getUrl()!=null && !TextUtils.isEmpty(request.getUrl().getScheme())){
                    //String scheme = request.getUrl().getScheme();
                    //String scheme2 = request.getUrl().getSchemeSpecificPart();
                    /**
                     * 例子如果有存在的mqqopensdkapi、weixin请求头则允许打开
                     *mqqopensdkapi://bizAgent/qm/qr?url=https%3A%2F%2Fqm.qq.c
                     *weixin://dl/business/?t=PoRiw1Alv5q
                     * intent类型的无法打开
                     */
                    boolean has = info.getWebViewConfig().getAllowOpenUrls().contains(request.getUrl().getScheme());
                    if(has){
                        if(!"intent".equals(request.getUrl().getScheme())){
                            Intent intent = new Intent(Intent.ACTION_VIEW,request.getUrl()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);
                        }
                        return true;
                    }
                    else{
                        //在地址中包含有http://open.action/的会打开活动
                       if(request.getUrl().toString().contains("open.action")){
                           String url = request.getUrl().getQueryParameter("url");
                           if(url!=null && url!=""){
                                url = url.replace(SET_DNS,"");
                                url = new String(Base64.decode(url.getBytes(StandardCharsets.UTF_8),Base64.DEFAULT));
                               OpenAction setDns = JSONObject.parseObject(url, OpenAction.class);
                               if(setDns!=null){
                                   Intent intent = new Intent();
                                   intent.setAction(setDns.getAction());
                                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                   intent.setComponent(new ComponentName(setDns.getPackages(),setDns.getActivity()));
                                   getApplicationContext().startActivity(intent);
                                   return true;
                               }
                           }
                       }
                    }
                }
                return false;
            }
        });
        mWebView.loadUrl(info.getWebViewConfig().getUrl());
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

            }
        });
        qmuiWebcon.setCustomOnScrollChangeListener(new QMUIWebView.OnScrollChangeListener(){
            @Override
            public void onScrollChange(WebView webView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }
        });
    }

}
