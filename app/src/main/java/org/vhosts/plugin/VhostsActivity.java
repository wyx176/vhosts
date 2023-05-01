/*
 **Copyright (C) 2017  xfalcon
 **
 **This program is free software: you can redistribute it and/or modify
 **it under the terms of the GNU General Public License as published by
 **the Free Software Foundation, either version 3 of the License, or
 **(at your option) any later version.
 **
 **This program is distributed in the hope that it will be useful,
 **but WITHOUT ANY WARRANTY; without even the implied warranty of
 **MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 **GNU General Public License for more details.
 **
 **You should have received a copy of the GNU General Public License
 **along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **
 */

package org.vhosts.plugin;

import static org.vhosts.plugin.util.LogUtils.context;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSONObject;
import com.baidu.mobstat.StatService;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.suke.widget.SwitchButton;

import org.vhosts.plugin.domain.VersionInfo;
import org.vhosts.plugin.util.HttpUtils;
import org.vhosts.plugin.util.LogUtils;
import org.vhosts.plugin.vservice.VhostsService;

import java.nio.charset.StandardCharsets;

public class VhostsActivity extends Activity {

    private static final String TAG = VhostsActivity.class.getSimpleName();
    private static final int VPN_REQUEST_CODE = 0x0F;

    public static final String PREFS_NAME = VhostsActivity.class.getName();

    public static final  String HOST_DATA = "HOST_DATA";

    private VersionInfo info;

    private boolean waitingForVPNStart;

    private String connect;

    private String codeName;

    private QMUIRoundButton helpButton;

    private QMUITopBarLayout topBar;

    private BroadcastReceiver vpnStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (VhostsService.BROADCAST_VPN_STATE.equals(intent.getAction())) {
                if (intent.getBooleanExtra("running", false))
                    waitingForVPNStart = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vhosts);
        context = getApplicationContext();
        codeName = getCodedName();
        connect = new String(Base64.decode("aHR0cDovL2hvc3RzLjkxNmIuY24vYXBpL2hvc3Rz".getBytes(StandardCharsets.UTF_8),Base64.DEFAULT))+"?codeName="+codeName;
        //初始化bar
        initTopBar();
        init();
        launch();
        StatService.autoTrace(this, true, false);

        final SwitchButton vpnButton = findViewById(R.id.button_start_vpn);

        //帮助按钮
        helpButton = findViewById(R.id.button_help);

        vpnButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    startVPN();
                } else {
                    shutdownVPN();
                }
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                startActivity(new Intent(getApplicationContext(), WebViewActivity.class).putExtra(HOST_DATA,JSONObject.toJSONString(info)));
            }
        });


        LocalBroadcastManager.getInstance(this).registerReceiver(vpnStateReceiver,
                new IntentFilter(VhostsService.BROADCAST_VPN_STATE));
    }

    private void launch() {
        Uri uri = getIntent().getData();
        if (uri == null) return;
        String data_str = uri.toString();
        if ("on".equals(data_str)) {
            if (!VhostsService.isRunning())
                VhostsService.startVService(this,1);
            finish();
        } else if ("off".equals(data_str)) {
            VhostsService.stopVService(this);
            finish();
        }
    }

    private void startVPN() {
        waitingForVPNStart = false;
        Intent vpnIntent = VhostsService.prepare(this);
        if (vpnIntent != null){
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        }
        else {
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
        }
    }

    private void shutdownVPN() {
        if (VhostsService.isRunning()) {
            startService(new Intent(this, VhostsService.class).setAction(VhostsService.ACTION_DISCONNECT));
        }
        setButton(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        waitingForVPNStart = true;
        startService(new Intent(this, VhostsService.class).setAction(VhostsService.ACTION_CONNECT).putExtra(HOST_DATA,JSONObject.toJSONString(info)));
        setButton(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setButton(!waitingForVPNStart && !VhostsService.isRunning());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setButton(boolean enable) {
        final SwitchButton vpnButton = (SwitchButton) findViewById(R.id.button_start_vpn);
        if (enable) {
            vpnButton.setChecked(false);

        } else {
            vpnButton.setChecked(true);
        }
    }

    private void init(){
        if(!checkNetwork()){
            Toast.makeText(getApplication(), getString(R.string.error), Toast.LENGTH_LONG).show();
            finish();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    String result = HttpUtils.get(connect);
                    LogUtils.e(TAG, result);

                    info = JSONObject.parseObject(result,VersionInfo.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //设置帮助按钮名称
                            helpButton.setText(info.getAppInfo().getHelpBtnName());
                            //设置状态栏名称
                            topBar.setTitle(info.getAppInfo().getAppName()).setTextColor(ContextCompat.getColor(context,R.color.qmui_config_color_white));
                        }
                    });
                    //检测版本号
                    if(!codeName.equals(info.getVersion().getV())){
                        new QMUIDialog.MessageDialogBuilder(VhostsActivity.this)
                                .setTitle(info.getVersion().getTitle())
                                .setMessage(info.getVersion().getMsg())
                                .addAction(info.getVersion().getConfirm(), new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        Intent intent = new Intent();
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                                        intent.setData(Uri.parse(info.getVersion().getUrl()));//Url 要打开的网址
                                        intent.setAction(Intent.ACTION_VIEW);
                                        getApplication().startActivity(intent); //启动浏览器
                                    }
                                })
                                .setCancelable(false)
                                .setCanceledOnTouchOutside(false)
                                .show();
                    }
                    else if(info.getNotice().getShow()){
                        new QMUIDialog.MessageDialogBuilder(VhostsActivity.this)
                                .setTitle(info.getNotice().getTitle())
                                .setMessage(info.getNotice().getMsg())
                                .addAction(info.getNotice().getConfirm(), new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .setCanceledOnTouchOutside(false)
                                .show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplication(), getString(R.string.error), Toast.LENGTH_LONG).show();
                    LogUtils.e(TAG, e.getMessage(), e);
                    finish();
                }
                Looper.loop();

            }
        }).start();
    }

    protected void initTopBar(){
        topBar= (QMUITopBarLayout) findViewById(R.id.topbar);
        //开启沉浸式
        QMUIStatusBarHelper.translucent(this);
        topBar.setBackgroundColor(ContextCompat.getColor(this,R.color.primary));
        topBar.setTitleGravity(Gravity.LEFT);
    }

    /**
     * 获取版本号
     * @return
     */
    private String getCodedName(){
        PackageManager manager = context.getPackageManager();
        String codeName = "0" ;
        try {
            PackageInfo packageInfo =  manager.getPackageInfo( context.getPackageName(),0);
            codeName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "init: "+e.getMessage() ,e);
        }
        return codeName;
    }

    private boolean checkNetwork() {
        boolean pass = true;
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getNetworkInfo(ConnectivityManager.TYPE_VPN).isConnectedOrConnecting()){
            return false;
        }
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");    //获取代理主机
            String portStr = System.getProperty("http.proxyPort");  //获取代理端口
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }

        return !((!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1));
    }
}
