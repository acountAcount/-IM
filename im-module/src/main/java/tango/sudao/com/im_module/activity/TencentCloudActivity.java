package tango.sudao.com.im_module.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallNotification;
import com.tencent.callsdk.ILVCallNotificationListener;
import com.tencent.callsdk.ILVIncomingListener;
import com.tencent.callsdk.ILVIncomingNotification;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.util.ArrayList;

import tango.sudao.com.im_module.util.SPHelper;

/**
 * Created by pcdalao on 2018/1/8.
 */

public class TencentCloudActivity extends Activity implements ILVIncomingListener, ILVCallListener, ILVCallNotificationListener {
    AlertDialog mIncomingDlg=null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void initConfig(){

       // String id= SPHelper.getString("userId",this);
        //String userSig= SPHelper.getString("userSig",this);

        //loginSDK(id,userSig);

        // 设置通话回调
        ILVCallManager.getInstance().addIncomingListener(this);
        ILVCallManager.getInstance().addCallListener(this);
    }


    /**
     * 使用userSig登录iLiveSDK(独立模式下获有userSig直接调用登录)
     */
    private void loginSDK(final String id, final String userSig){

        ILiveLoginManager.getInstance().iLiveLogin(id, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {


            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(getApplicationContext(), "Login failed:"+module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 发起呼叫
     */
    public void makeCall(int callType, ArrayList<String> nums){
        Intent intent = new Intent();
        intent.setClass(this, CallActivity.class);
        intent.putExtra("HostId", ILiveLoginManager.getInstance().getMyUserId());
        intent.putExtra("CallId", 0);
        intent.putExtra("CallType", callType);
        intent.putStringArrayListExtra("CallNumbers", nums);
        startActivity(intent);
    }



    private void acceptCall(int callId, String hostId, int callType){
        Intent intent = new Intent();
        intent.setClass(this, CallActivity.class);
        intent.putExtra("HostId", hostId);
        intent.putExtra("CallId", callId);
        intent.putExtra("CallType", callType);
        startActivity(intent);
    }

    @Override
    public void onCallEstablish(int callId) {
        Toast.makeText(getApplicationContext(),"通话链接建立",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        Toast.makeText(getApplicationContext(),"通话结束",Toast.LENGTH_LONG).show();
        if(mIncomingDlg!=null){
            mIncomingDlg.dismiss();
        }
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {
        Toast.makeText(getApplicationContext(),"通话异常",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRecvNotification(int callid, ILVCallNotification notification) {

    }

    @Override
    public void onNewIncomingCall(int callId, int callType, ILVIncomingNotification notification) {

        if (null != mIncomingDlg){  // 关闭遗留来电对话框
            mIncomingDlg.dismiss();
        }
        final int  mCurIncomingId = callId;
        final int mCallType=callType;
        final ILVIncomingNotification mNotification=notification;
        mIncomingDlg = new AlertDialog.Builder(this)
                .setTitle("New Call From "+notification.getSender())
                .setMessage(notification.getNotifDesc())
                .setPositiveButton("Accept", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        acceptCall(mCurIncomingId, mNotification.getSponsorId(), mCallType);
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int ret = ILVCallManager.getInstance().rejectCall(mCurIncomingId);
                        dialog.dismiss();
                    }
                })
                .create();
        mIncomingDlg.setCanceledOnTouchOutside(false);
        mIncomingDlg.show();
    }

}
