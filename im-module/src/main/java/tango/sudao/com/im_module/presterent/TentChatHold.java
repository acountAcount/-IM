package tango.sudao.com.im_module.presterent;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMFaceElem;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMImageType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMSoundElem;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import tango.sudao.com.im_module.activity.ImageViewActivity;
import tango.sudao.com.im_module.interfaces.MessageListsRequest;
import tango.sudao.com.im_module.interfaces.MessgeCallBack;
import tango.sudao.com.im_module.interfaces.RequestStatusCallBack;
import tango.sudao.com.im_module.model.Message;
import tango.sudao.com.im_module.model.MessageStatus;
import tango.sudao.com.im_module.model.MessageType;
import tango.sudao.com.im_module.util.EmoticonUtil;
import tango.sudao.com.im_module.util.SPHelper;

/**
 * Created by pcdalao on 2018/1/11.
 */

public class TentChatHold implements Observer {
    private String tag = "teng log===============>";
    private Context context;
    private MessgeCallBack messgeCallBack;

    public TentChatHold(Context context, MessgeCallBack messgeCallBack) {
        this.context = context;
        this.messgeCallBack = messgeCallBack;
        initConfig();

    }

    /**
     * 初始化
     */
    public void initConfig() {
        String id = SPHelper.getString("userId", context);
        String userSig = SPHelper.getString("userSig", context);

       // loginSDK(id, userSig);

        // 设置消息回调
        MessageEvent.getInstance().addObserver(this);
    }

    /**
     * 使用userSig登录iLiveSDK(独立模式下获有userSig直接调用登录)
     */
    private void loginSDK(final String id, final String userSig) {

        ILiveLoginManager.getInstance().iLiveLogin(id, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {


            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(context, "Login failed:" + module + "|" + errCode + "|" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 构建文字消息
     *
     * @param userId
     * @param msgText
     * @return
     */
    public Message buildTextMessage(String userId, final Editable msgText) {
        if (msgText.equals("")) {
            Toast.makeText(context, "输入的是空消息", Toast.LENGTH_LONG).show();
            return null;
        }

        final TIMMessage timMessage = textMessage(msgText);
        Message message = new Message();
        long a = new Date().getTime() / 1000;

        SpannableStringBuilder stringBuilder = (SpannableStringBuilder) msgText;
        message.setSenderOrreceiverMessage(stringBuilder);
        message.setMessageId(timMessage.getMsgId());
        message.setSenderOrreceiverTime(a);
        message.setMessageStatus(MessageStatus.Sending);
        message.setMessageType(MessageType.TEXT);
        message.setCustomObject(timMessage);
        message.setSenderId(userId);
        message.setSelf(true);
        return message;
    }

    /**
     * 发送文字表情
     *  @param message            消息内容
     * @param sendStatusCallBack
     */

    public void sendText(Message message, final RequestStatusCallBack sendStatusCallBack) {
        //获取单聊会话
        TIMConversation conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                message.getSenderId());
        final TIMMessage timMessage = (TIMMessage) message.getCustomObject();
        //发送消息
        conversation.sendMessage(timMessage, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                Log.d(tag, "send message failed. code: " + code + " errmsg: " + desc);
                Message message = new Message();
                message.setMessageId(timMessage.getMsgId());
                sendStatusCallBack.onError(message);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e(tag, "SendMsg ok");
                Message message = new Message();
                message.setMessageId(msg.getMsgId());
                sendStatusCallBack.onSuccess(message);
            }
        });
    }

    public Message buildImageMessage(String userId,  String path) {
        if (path.equals("")&&null==path) {
            Toast.makeText(context, "文件不存在", Toast.LENGTH_LONG).show();
            return null;
        }
        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //添加图片
        TIMImageElem elem = new TIMImageElem();
        elem.setPath(path);
        //将elem添加到消息
        if(msg.addElement(elem) != 0) {
            Log.d(tag, "addElement failed");
            return null;
        }


        Message message = new Message();
        long a = new Date().getTime() / 1000;

        message.setSenderOrreceiverMessage(new SpannableStringBuilder(path));
        message.setMessageId(msg.getMsgId());
        message.setSenderOrreceiverTime(a);
        message.setMessageStatus(MessageStatus.Sending);
        message.setMessageType(MessageType.IMAGE);
        message.setCustomObject(msg);
        message.setSenderId(userId);
        message.setSelf(true);
        return message;
    }
    public void sendImage(Message message,final RequestStatusCallBack sendStatusCallBack){

        //获取单聊会话
        TIMConversation conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                message.getSenderId());

        final TIMMessage timMessage = (TIMMessage) message.getCustomObject();
        //发送消息
        conversation.sendMessage(timMessage, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                Log.d(tag, "send message failed. code: " + code + " errmsg: " + desc);
                Message message = new Message();
                message.setMessageId(timMessage.getMsgId());
                sendStatusCallBack.onError(message);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e(tag, "SendMsg ok");
                Message message = new Message();
                message.setMessageId(msg.getMsgId());
                sendStatusCallBack.onSuccess(message);
            }
        });
    }

    public void imageLoad(final Message message, final RequestStatusCallBack sendStatusCallBack){
        TIMMessage timMessage=(TIMMessage)message.getCustomObject();
        TIMImageElem e = (TIMImageElem) timMessage.getElement(0);
        final FileUtil fileUtil=new FileUtil(context);
        for(final TIMImage image : e.getImageList()) {
            if (image.getType() == TIMImageType.Thumb){
                final String uuid = image.getUuid();
                if (fileUtil.isCacheFileExist(uuid)){
                    //showThumb(viewHolder,uuid);
                    message.setSenderOrreceiverMessage(new SpannableStringBuilder(fileUtil.getCacheFilePath(uuid)));
                    sendStatusCallBack.onSuccess(message);
                }else{
                    image.getImage(new TIMValueCallBack<byte[]>() {
                        Message tempMessage=message;
                        @Override
                        public void onError(int code, String desc) {//获取图片失败
                            //错误码code和错误描述desc，可用于定位请求失败原因
                            //错误码code含义请参见错误码表
                            Log.e("test", "getImage failed. code: " + code + " errmsg: " + desc);
                        }

                        @Override
                        public void onSuccess(byte[] data) {//成功，参数为图片数据
                            fileUtil.createFile(data, uuid);
                            // showThumb(viewHolder,uuid);
                            tempMessage.setSenderOrreceiverMessage(new SpannableStringBuilder(fileUtil.getCacheFilePath(uuid)));
                            sendStatusCallBack.onSuccess(message);
                        }
                    });
                }
            }
        }
    }

    public void imageClick(final Message message){
        TIMMessage timMessage=(TIMMessage)message.getCustomObject();
        TIMImageElem e = (TIMImageElem) timMessage.getElement(0);
        FileUtil fileUtil=new FileUtil(context);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(message.getSenderOrreceiverMessage().toString(), options);


        if (fileUtil.isFileExist(message.getSenderOrreceiverMessage().toString()) && options.outWidth>400){

            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putExtra("filename", message.getSenderOrreceiverMessage().toString());
            context.startActivity(intent);
        }
         else {
            for (final TIMImage image : e.getImageList()) {
                if (image.getType() == TIMImageType.Large) {

                    image.getImage(message.getSenderOrreceiverMessage().toString(), new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            //错误码code和错误描述desc，可用于定位请求失败原因
                            //错误码code含义请参见错误码表
                            Log.e("test", "getImage failed. code: " + i + " errmsg: " + s);
                            Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess() {
                            Intent intent = new Intent(context, ImageViewActivity.class);
                            intent.putExtra("filename", message.getSenderOrreceiverMessage().toString());
                            context.startActivity(intent);
                        }
                    });
                }
            }
        }

}

    /**
     * 构建语音消息
     *
     * @param userId
     * @param time
     * @param filePath
     * @return
     */
    public Message buildVoiceMessage(String userId, long time, String filePath) {

        //构造一条消息
        final TIMMessage msg = new TIMMessage();
        //添加语音
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(filePath); //填写语音文件路径
        elem.setDuration(time);  //填写语音时长

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            Log.d(tag, "addElement failed");
            return null;
        }
        Message message = new Message();
        message.setCustomObject(msg);
        message.setMessageId(msg.getMsgId());
        message.setDuration(time);
        message.setMessageType(MessageType.VOICE);
        long a = new Date().getTime() / 1000;
        message.setSenderOrreceiverTime(a);
        message.setMessageStatus(MessageStatus.Sending);
        message.setMessageType(MessageType.VOICE);
        message.setSenderId(userId);
        message.setSelf(true);
        return message;
    }

    /**
     * 发送语音消息
     *  @param message
     * @param sendStatusCallBack
     */
    public void sendVoice(Message message, final RequestStatusCallBack sendStatusCallBack) {
        //获取单聊会话
        TIMConversation conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                message.getSenderId());
        final TIMMessage timMessage = (TIMMessage) message.getCustomObject();
        //发送消息
        conversation.sendMessage(timMessage, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                Log.d(tag, "send message failed. code: " + code + " errmsg: " + desc);
                Message message = new Message();
                message.setMessageId(timMessage.getMsgId());
                sendStatusCallBack.onError(message);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e(tag, "SendMsg ok");
                Message message = new Message();
                message.setMessageId(msg.getMsgId());
                sendStatusCallBack.onSuccess(message);
            }
        });
    }

    /**
     * 富文本编辑（文本消息构建）
     *
     * @param s
     * @return
     */
    public TIMMessage textMessage(Editable s) {
        TIMMessage message = new TIMMessage();
        ImageSpan[] spans = s.getSpans(0, s.length(), ImageSpan.class);
        List<ImageSpan> listSpans = sortByIndex(s, spans);
        int currentIndex = 0;
        for (ImageSpan span : listSpans) {
            int startIndex = s.getSpanStart(span);
            int endIndex = s.getSpanEnd(span);
            if (currentIndex < startIndex) {
                TIMTextElem textElem = new TIMTextElem();
                textElem.setText(s.subSequence(currentIndex, startIndex).toString());
                message.addElement(textElem);
            }
            TIMFaceElem faceElem = new TIMFaceElem();
            int index = Integer.parseInt(s.subSequence(startIndex, endIndex).toString());
            faceElem.setIndex(index);
            if (index < EmoticonUtil.emoticonData.length) {
                faceElem.setData(EmoticonUtil.emoticonData[index].getBytes(Charset.forName("UTF-8")));
            }
            message.addElement(faceElem);
            currentIndex = endIndex;
        }
        if (currentIndex < s.length()) {
            TIMTextElem textElem = new TIMTextElem();
            textElem.setText(s.subSequence(currentIndex, s.length()).toString());
            message.addElement(textElem);
        }
        return message;
    }

    private List<ImageSpan> sortByIndex(final Editable editInput, ImageSpan[] array) {
        ArrayList<ImageSpan> sortList = new ArrayList<>();
        for (ImageSpan span : array) {
            sortList.add(span);
        }
        Collections.sort(sortList, new Comparator<ImageSpan>() {
            @Override
            public int compare(ImageSpan lhs, ImageSpan rhs) {
                return editInput.getSpanStart(lhs) - editInput.getSpanStart(rhs);
            }
        });

        return sortList;
    }

    /**
     * 检测到消息的接收
     *
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {

        if (observable instanceof MessageEvent) {
            TIMMessage msg = (TIMMessage) o;
            if (msg != null) {
                    msg.getConversation().setReadMessage();

                Message message=getTIMMessageFactory(msg);
                if(message!=null)
                    messgeCallBack.newMessage(message);
            }
        }
    }

    public void playVoice(Message message, final AnimationDrawable animationDrawable) {
        TIMMessage timMessage = (TIMMessage) message.getCustomObject();
        final TIMSoundElem soundElem = (TIMSoundElem) timMessage.getElement(0);
        final FileUtil fileUtil = new FileUtil(context);
        fileUtil.getTempFile(FileUtil.FileType.AUDIO);

        soundElem.getSound(new TIMValueCallBack<byte[]>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    File tempAudio = new File(fileUtil.cacheDir + "/" + soundElem.getUuid());
                    FileOutputStream fos = new FileOutputStream(tempAudio);
                    fos.write(bytes);
                    fos.close();
                    FileInputStream fis = new FileInputStream(tempAudio);
                    MediaPlayer player = new MediaPlayer();
                    player.reset();
                    player.setDataSource(fis.getFD());
                    player.prepare();
                    player.start();
                    animationDrawable.start();
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            animationDrawable.stop();
                            animationDrawable.selectDrawable(0);
                        }
                    });
                } catch (Exception e) {

                }
            }
        });
    }

    public void messageList(final String id, final MessageListsRequest messageListsRequest) {

        //获取会话扩展实例
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, id);
        //获取此会话的消息
        conversation.getMessage(20, //获取此会话最近的10条消息
                null, //不指定从哪条消息开始获取 - 等同于从最新的消息开始往前
                new TIMValueCallBack<List<TIMMessage>>() {//回调接口
                    @Override
                    public void onError(int code, String desc) {//获取消息失败
                        //接口返回了错误码code和错误描述desc，可用于定位请求失败原因
                        //错误码code含义请参见错误码表
                        Log.d(tag, "get message failed. code: " + code + " errmsg: " + desc);
                        messageListsRequest.onError(null);
                    }

                    @Override
                    public void onSuccess(List<TIMMessage> msgs) {//获取消息成功
                        List<Message> list = new ArrayList<Message>();
                        //遍历取得的消息
                            for (int i=msgs.size()-1;i>=0;i--) {
                                //可以
                                // 通过timestamp()获得消息的时间戳, isSelf()是否为自己发送的消息
                                Log.e(tag, "get msg: " + msgs.get(i).timestamp() + " self: " + msgs.get(i).isSelf() + " seq: " + msgs.get(i).msg.seq());
                                Message tempMessage = getTIMMessageFactory(msgs.get(i));
                                if (tempMessage != null)
                                    list.add(tempMessage);
                            }

                        messageListsRequest.onSuccess(list);
                        Log.d("test","tset"+list);
                    }
                });
    }

    public Message getTIMMessageFactory(TIMMessage msg) {
        Message message =null;
        final FileUtil fileUtil = new FileUtil(context);
        switch (msg.getElement(0).getType()) {
            case Text:
            case Face:
                message=new Message();
                SpannableStringBuilder result = new SpannableStringBuilder();
                for (int i = 0; i < msg.getElementCount(); ++i) {
                    switch (msg.getElement(i).getType()) {
                        case Face:
                            TIMFaceElem faceElem = (TIMFaceElem) msg.getElement(i);
                            int startIndex = result.length();
                            try {
                                AssetManager am = context.getAssets();
                                InputStream is = am.open(String.format("emoticon/%d.gif", faceElem.getIndex()));
                                if (is == null) continue;
                                Bitmap bitmap = BitmapFactory.decodeStream(is);
                                Matrix matrix = new Matrix();
                                int width = bitmap.getWidth();
                                int height = bitmap.getHeight();
                                matrix.postScale(2, 2);
                                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                        width, height, matrix, true);
                                ImageSpan span = new ImageSpan(context, resizedBitmap, ImageSpan.ALIGN_BASELINE);
                                result.append(String.valueOf(faceElem.getIndex()));
                                result.setSpan(span, startIndex, startIndex + String.valueOf(faceElem.getIndex()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                is.close();
                            } catch (IOException e) {

                            }
                            break;
                        case Text:
                            TIMTextElem textElem = (TIMTextElem) msg.getElement(i);
                            result.append(textElem.getText());
                            break;

                    }

                }
                message.setMessageType(MessageType.TEXT);
                message.setSenderName(msg.getSender());
                message.setSenderOrreceiverMessage(result);
                message.setSelf(msg.isSelf());
                message.setMessageId(msg.getMsgId());
                message.setRoomType(msg.getConversation().getType().name());

                message.setMessageStatus( MessageStatus.valueOf(msg.status().name()));
                break;
            case Sound:
                message=new Message();
                final TIMSoundElem soundElem = (TIMSoundElem) msg.getElement(0);

                message.setMessageType(MessageType.VOICE);
                message.setSenderName(msg.getSender());
                message.setCustomObject(msg);
                // message.setVoiceFile(fileUtil.cacheDir+"/"+soundElem.getUuid());
                message.setDuration(soundElem.getDuration());
                message.setSelf(msg.isSelf());
                message.setRoomType(msg.getConversation().getType().name());
                message.setMessageStatus( MessageStatus.valueOf(msg.status().name()));
                break;
            case Image:
                message=new Message();

                message.setSenderOrreceiverMessage(null);
                message.setSenderName(msg.getSender());
                message.setCustomObject(msg);
                message.setMessageId(msg.getMsgId());
                message.setMessageType(MessageType.IMAGE);
                message.setCustomObject(msg);
                message.setSelf(msg.isSelf());
                message.setRoomType(msg.getConversation().getType().name());
                message.setMessageStatus( MessageStatus.valueOf(msg.status().name()));


                break;
        }
        return message;
    }


}
