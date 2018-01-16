package tango.sudao.com.call_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import tango.sudao.com.im_module.activity.ChatActivity;
import tango.sudao.com.im_module.util.SPHelper;

/**
 * Created by pcdalao on 2018/1/16.
 */

public class SplashActivity  extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        String id = SPHelper.getString("userId", getApplicationContext());
        String userSig = SPHelper.getString("userSig", getApplicationContext());
        Toast.makeText(getApplicationContext(), "Login :", Toast.LENGTH_SHORT).show();
        loginSDK(id,userSig);
    }

    /**
     * 使用userSig登录iLiveSDK(独立模式下获有userSig直接调用登录)
     */
    private void loginSDK(final String id, final String userSig) {

        ILiveLoginManager.getInstance().iLiveLogin(id, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {

                Log.d("succ","login");
                    Intent intent =new Intent(SplashActivity.this,ChatActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(getApplicationContext(), "Login failed:" + module + "|" + errCode + "|" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
