package tango.sudao.com.im_module.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.callsdk.ILVCallConstants;

import java.util.ArrayList;

import tango.sudao.com.im_module.R;


/**
 * Created by pcdalao on 2018/1/8.
 */

public class IMActivity extends TencentCloudActivity implements View.OnClickListener {
    private EditText user1,user2;
    private Button call_v,call_a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        initConfig();

        call_v=findViewById(R.id.call_im);
        call_a=findViewById(R.id.call_audio_im);
        user1=findViewById(R.id.user1);
        user2=findViewById(R.id.user2);
        call_v.setOnClickListener(this);
        call_a.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        ArrayList<String> list=new ArrayList<String>();
        if(view.getId()==R.id.call_audio_im){
            if(!user1.getText().toString().equals(""))
                list.add(user1.getText().toString());
            if(!user2.getText().toString().equals(""))
                list.add(user2.getText().toString());

            makeCall(ILVCallConstants.CALL_TYPE_AUDIO, list);
        }else if(view.getId()==R.id.call_im){
            if(!user1.getText().toString().equals(""))
                list.add(user1.getText().toString());
            if(!user2.getText().toString().equals(""))
                list.add(user2.getText().toString());

            makeCall(ILVCallConstants.CALL_TYPE_VIDEO, list);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
