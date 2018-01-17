package tango.sudao.com.im_module.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.callsdk.ILVCallConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tango.sudao.com.im_module.R;
import tango.sudao.com.im_module.adapter.ChatAdapter;
import tango.sudao.com.im_module.interfaces.MessageClickCallBack;
import tango.sudao.com.im_module.interfaces.MessageListsRequest;
import tango.sudao.com.im_module.interfaces.MessgeCallBack;
import tango.sudao.com.im_module.interfaces.RequestStatusCallBack;
import tango.sudao.com.im_module.model.Message;
import tango.sudao.com.im_module.model.MessageStatus;
import tango.sudao.com.im_module.interfaces.SendMessage;
import tango.sudao.com.im_module.presterent.FileUtil;
import tango.sudao.com.im_module.presterent.PushUtil;
import tango.sudao.com.im_module.presterent.TentChatHold;
import tango.sudao.com.im_module.presterent.UriChangePathUtil;
import tango.sudao.com.im_module.util.RecorderUtil;
import tango.sudao.com.im_module.view.ChatInput;
import tango.sudao.com.im_module.view.VoiceSendingView;

public class ChatActivity extends TencentCloudActivity implements SendMessage,MessgeCallBack,MessageClickCallBack{
    private ChatInput input;
    private VoiceSendingView voiceSendingView;
    private RecorderUtil recorder;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private  TentChatHold tentChatHold;
    private Uri fileUri;
    private String senderId;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int IMAGE_STORE = 200;
    private static final int IMAGE_PREVIEW = 400;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        input = (ChatInput) findViewById(R.id.input_panel);
        voiceSendingView=findViewById(R.id.voice_sending);
        input.setSendMessage(this);// chatview初始化
        recorder = new RecorderUtil(this);
        recyclerView = findViewById(R.id.list);

        tentChatHold= new TentChatHold(this,this);//必须实例化，里面做sdk的消息的接收处理等

        adapter = new ChatAdapter(this, messageList,this);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        //linearLayoutManager.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        //linearLayoutManager.setReverseLayout(true);//列表翻转
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        senderId="851866901z";
        PushUtil.getInstance();  //离线推送
//        ILVCallManager.getInstance().addIncomingListener(this);
//        ILVCallManager.getInstance().addCallListener(this);
        initConfig();

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        input.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });

        tentChatHold.messageList(senderId, new MessageListsRequest() {
            @Override
            public void onSuccess(List<Message> list) {
                messageList.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(List<Message> list) {
                Toast.makeText(getApplicationContext(),"获取不到聊天记录",Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void sendImage() {
        Toast.makeText(getApplicationContext(),"发送图片",Toast.LENGTH_LONG).show();
        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image/*");
        startActivityForResult(intent_album, IMAGE_PREVIEW);
    }

    @Override
    public void sendPhoto() {
        Toast.makeText(getApplicationContext(),"发送照片",Toast.LENGTH_LONG).show();
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = new FileUtil(getApplicationContext()).getTempFile(FileUtil.FileType.IMG);
            if (tempFile != null) {
                fileUri = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void sendText() {
        Toast.makeText(getApplicationContext(),"发送文字",Toast.LENGTH_LONG).show();
        final Message message=tentChatHold.buildTextMessage(senderId, input.getText());

        messageList.add(message);
        adapter.notifyItemInserted(messageList.size()-1);
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
        input.setText("");

        tentChatHold.sendText(message,new RequestStatusCallBack() {

            @Override
            public void onSuccess(Message msg) {
                Log.d("test", "onSuccess: "+messageList.size());
                int index=getIndex(msg);
                messageList.get(index).setMessageStatus(MessageStatus.SendSucc);
                adapter.notifyItemChanged(index);
                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Message msg) {
                int index=getIndex(msg);
                messageList.get(index).setMessageStatus(MessageStatus.SendFail);
                adapter.notifyItemChanged(index);
                Toast.makeText(getApplicationContext(),"发送失败",Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void sendFile() {
        Toast.makeText(getApplicationContext(),"发送文件",Toast.LENGTH_LONG).show();
    }

    @Override
    public void startSendVoice() {
        Toast.makeText(getApplicationContext(),"发送语音开始",Toast.LENGTH_LONG).show();
        voiceSendingView.setVisibility(View.VISIBLE);
        voiceSendingView.showRecording();
        recorder.startRecording();
    }

    @Override
    public void endSendVoice() {

        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        if (recorder.getTimeInterval() < 1) {
            Toast.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
        } else {
            Message message=tentChatHold.buildVoiceMessage(senderId, recorder.getTimeInterval(), recorder.getFilePath());

            messageList.add(message);
            adapter.notifyItemInserted(messageList.size()-1);
            Log.d("zhouyoukun","ac"+messageList.size());
            recyclerView.scrollToPosition(adapter.getItemCount()-1);

            tentChatHold.sendVoice(message,new RequestStatusCallBack() {
                @Override
                public void onSuccess(Message msg) {
                    Toast.makeText(getApplicationContext(),"发送语音结束",Toast.LENGTH_LONG).show();
                    int index=getIndex(msg);
                    messageList.get(index).setMessageStatus(MessageStatus.SendSucc);
                    adapter.notifyItemChanged(index);
                }

                @Override
                public void onError(Message msg) {
                    int index=getIndex(msg);
                    messageList.get(index).setMessageStatus(MessageStatus.SendFail);
                    adapter.notifyItemChanged(index);
                    Toast.makeText(getApplicationContext(),"发送失败",Toast.LENGTH_LONG).show();

                }
            });

        }
    }

    @Override
    public void sendVideo(String fileName) {
        Toast.makeText(getApplicationContext(),"发送小视频",Toast.LENGTH_LONG).show();
    }

    @Override
    public void cancelSendVoice() {
        Toast.makeText(getApplicationContext(),"取消发送语音",Toast.LENGTH_LONG).show();
    }

    @Override
    public void sending() {
        // Toast.makeText(getApplicationContext(),"消息编辑中",Toast.LENGTH_LONG).show();
        Log.d("test","消息编辑中");
    }

    @Override
    public void sendVideoChat() {

        ArrayList<String> list=new ArrayList<String>();
        list.add(senderId);
        makeCall(ILVCallConstants.CALL_TYPE_VIDEO, list);
    }

    @Override
    public void sendVoip() {
        ArrayList<String> list=new ArrayList<String>();
        list.add(senderId);
        makeCall(ILVCallConstants.CALL_TYPE_AUDIO, list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PREVIEW){
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                String path=null;
                UriChangePathUtil uriChangePathUtil=new UriChangePathUtil(this);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    path = uriChangePathUtil.getPath(this, uri);
                    Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
                } else {//4.4以下下系统调用方法
                    path =  uriChangePathUtil.getRealPathFromURI(uri);
                    Toast.makeText(this, path+"222222", Toast.LENGTH_SHORT).show();
                }

                File file = new File(path);
                if (file.exists() && file.length() > 0){
                    if (file.length() > 1024 * 1024 * 10){
                        Toast.makeText(this, getString(R.string.chat_file_too_large),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "发送",Toast.LENGTH_SHORT).show();
                        Message message=tentChatHold.buildImageMessage(senderId,path);

                        messageList.add(message);
                        adapter.notifyItemInserted(messageList.size()-1);
                        Log.d("zhouyoukun","ac"+messageList.size());
                        recyclerView.scrollToPosition(adapter.getItemCount()-1);

                        tentChatHold.sendImage(message, new RequestStatusCallBack() {
                            @Override
                            public void onSuccess(Message message) {
                                int index=getIndex(message);
                                messageList.get(index).setMessageStatus(MessageStatus.SendSucc);
                                adapter.notifyItemChanged(index);
                            }

                            @Override
                            public void onError(Message message) {
                                int index=getIndex(message);
                                messageList.get(index).setMessageStatus(MessageStatus.SendFail);
                                adapter.notifyItemChanged(index);
                            }
                        });
                    }
                }else{
                    Toast.makeText(this, getString(R.string.chat_file_not_exist),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 获取需要刷新状态的message
     * @param message
     * @return
     */
    public int getIndex(Message message) {
        int index = 0;
        for (int i = messageList.size() - 1; i >= 0; i--) {
            if (messageList.get(i).getMessageId().equals(message.getMessageId())) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            index = 0;
        }
        return index;
    }

    /**
     * 新消息监听
     * @param message
     */
    @Override
    public void newMessage(Message message) {
        messageList.add(message);
        adapter.notifyItemInserted(messageList.size()-1);


        Log.d("zhouyoukun","ac"+messageList.size());
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
    }

    /**
     * 点击播放语音消息
     * @param message
     */
    @Override
    public void play(Message message, AnimationDrawable animationDrawable) {
        tentChatHold.playVoice(message,animationDrawable);
    }

    /**
     *
     * @param message
     */
    @Override
    public void imageLoad(Message message, final ImageView imageView, final RelativeLayout relativeLayout) {
        tentChatHold.imageLoad(message, new RequestStatusCallBack() {
            @Override
            public void onSuccess(Message message) {
                imageView.setImageBitmap(adapter.getThumb(message.getSenderOrreceiverMessage().toString()));
                relativeLayout.removeAllViews();
                relativeLayout.addView(imageView);
            }

            @Override
            public void onError(Message message) {

            }
        });
    }

    @Override
    public void imageClick(Message message) {
        tentChatHold.imageClick(message);
    }
}
