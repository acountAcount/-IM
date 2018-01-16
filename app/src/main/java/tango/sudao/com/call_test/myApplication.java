package tango.sudao.com.call_test;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMOfflinePushListener;
import com.tencent.TIMOfflinePushNotification;
import com.tencent.TIMUser;
import com.tencent.callsdk.ILVCallConfig;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.qalsdk.sdk.MsfSdkUtils;

import tango.sudao.com.im_module.presterent.Foreground;
import tango.sudao.com.im_module.util.SPHelper;

/**
 * Created by pcdalao on 2018/1/8.
 */

public class myApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Foreground.init(this);
        initConfig();
    }
    public void initConfig(){
        //TODO 初始化随心播
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1400028285, 11818);
        ILVCallManager.getInstance().init(new ILVCallConfig()
                .setAutoBusy(true));

        // 只能在主进程进行离线推送监听器注册
        if(MsfSdkUtils.isMainProcess(this)) {
            Log.d("MyApplication", "main process");

            // 设置离线推送监听器
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    Log.d("MyApplication", "recv offline push");

                    // 这里的doNotify是ImSDK内置的通知栏提醒，应用也可以选择自己利用回调参数notification来构造自己的通知栏提醒
                    notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher);
                }
            });
        }
    }
}
