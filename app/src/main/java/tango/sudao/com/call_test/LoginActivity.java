package tango.sudao.com.call_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import tango.sudao.com.im_module.activity.ChatActivity;
import tango.sudao.com.im_module.util.SPHelper;


/**
 * Created by pcdalao on 2018/1/8.
 */

public class LoginActivity extends Activity implements View.OnClickListener{

    private Button reg,login;

    private EditText uaserName,passWord;

    private boolean bTLSAccount =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        if(!SPHelper.getString("userSig",LoginActivity.this).equals("")){
            Intent intent =new Intent(LoginActivity.this,SplashActivity.class);
            startActivity(intent);
            finish();
        }
        initView();
    }
    public void initView(){
        uaserName=findViewById(R.id.userName);
        passWord=findViewById(R.id.passWord);
        reg=(Button)findViewById(R.id.reg);
        login=(Button)findViewById(R.id.login);
        reg.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reg:
                regist();
                break;
            case R.id.login:
                login();
                break;
        }
    }
    public void regist(){
        String mUserName=   uaserName.getText().toString();
        String mPassword=   passWord.getText().toString();
        if (bTLSAccount){
            ILiveLoginManager.getInstance().tlsRegister(mUserName, mPassword, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(getApplicationContext(), "regist success!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(getApplicationContext(), "regist failed:" + module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            //架设到自己服务器后做相应的处理
//            mAccountMgr.regist(mUserName, mPassword, new AccountMgr.RequestCallBack() {
//                @Override
//                public void onResult(int error, String response) {
//                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }
    public void login(){
        final String mUserName=   uaserName.getText().toString();
        String mPassword=   passWord.getText().toString();


        if (bTLSAccount){
            ILiveLoginManager.getInstance().tlsLogin(mUserName, mPassword, new ILiveCallBack<String>() {
                @Override
                public void onSuccess(String data) {

                    //loginSDK(mUserName, data);

                    SPHelper.putString("userSig",data,LoginActivity.this);
                    SPHelper.putString("userId",mUserName,LoginActivity.this);

                    Intent intent =new Intent(LoginActivity.this,ChatActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(getApplicationContext(), "login failed:" + module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }else {

            //架设到自己服务器后做相应的实现
//            mAccountMgr.login(id, password, new AccountMgr.RequestCallBack() {
//                @Override
//                public void onResult(int error, String response) {
//                    if (0 == error) {
//                        loginSDK(id, response);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "login failed:" + response, Toast.LENGTH_SHORT).show();
//                        loginView.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
        }

    }


}
