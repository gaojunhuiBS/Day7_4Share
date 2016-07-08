package com.gaojunhui.day7_4share;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.bt_share)
    Button btShare;
    @InjectView(R.id.bt_shouquan)
    Button btShouquan;
    @InjectView(R.id.bt_login)
    Button btLogin;
    private UMShareAPI mShareAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        openSettings();//6.0权限

        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        PushAgent.getInstance(this).onAppStart();
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom + "dianji", Toast.LENGTH_LONG).show();
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        //开启推送并设置注册的回调处理
        mPushAgent.enable(new IUmengRegisterCallback() {

            @Override
            public void onRegistered(final String registrationId) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        //onRegistered方法的参数registrationId即是device_token
                        Log.d("device_token", registrationId);
                    }
                });
            }
        });
        String device_token = UmengRegistrar.getRegistrationId(this);
        Log.d("-------", "token-------" + device_token);
        mShareAPI = UMShareAPI.get(this);
    }
    @OnClick({R.id.bt_share, R.id.bt_shouquan, R.id.bt_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_share:
                initShare();
                break;
            case R.id.bt_shouquan:
                authorize();
                break;
            case R.id.bt_login:
                getInfo();
                break;
        }

    }
    private void getInfo() {
        mShareAPI.getPlatformInfo(MainActivity.this, SHARE_MEDIA.QQ, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                Iterator<String> keys = data.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Log.d("------", "key==" + key + "    value==" + data.get(key));
                }
                Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 授权
     */
    private void authorize() {
        SHARE_MEDIA platform = SHARE_MEDIA.QQ;
        mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                Iterator<String> keys = data.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Log.d("------", "key==" + key + "    value==" + data.get(key));
                }
                Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 分享
     */
    private void initShare() {
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                {
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
                        SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.DOUBAN
                };
        new ShareAction(this).setDisplayList(displaylist)
                .withText("呵呵")
                .withTitle("title")
                .withTargetUrl("http://www.baidu.com")
                .withMedia(new UMImage(MainActivity.this, "http://img3.imgtn.bdimg.com/it/u=1924893621,661118346&fm=21&gp=0.jpg"))
                .setListenerList(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        Log.i("----", "成功");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Log.i("----", "失败");
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                })
                .open();
    }

    /**
     * 6.0权限
     */
    public void openSettings() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("必须同意打开权限")
                .setNegativeButton("打开设置界面", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent2 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent2.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent2);
                    }
                }).create().show();
    }
}
