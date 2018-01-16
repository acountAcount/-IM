package tango.sudao.com.im_module.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMImageType;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;

import java.io.File;
import java.io.IOException;
import java.util.List;
import tango.sudao.com.im_module.R;
import tango.sudao.com.im_module.interfaces.MessageClickCallBack;
import tango.sudao.com.im_module.model.Message;
import tango.sudao.com.im_module.util.TimeUtil;

/**
 * 聊天界面adapter
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>  {

    private  Context context;
    private List<Message> list;
    private LayoutInflater layoutInflater;
    private MessageClickCallBack messageClickCallBack;
    public ChatAdapter(Context context, List<Message> list,MessageClickCallBack messageClickCallBack){
        this.context=context;
        this.list=list;
        this.layoutInflater=LayoutInflater.from(context);
        this.messageClickCallBack=messageClickCallBack;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=layoutInflater.inflate(R.layout.item_message,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message=list.get(position);
        holder.systemMessage.setVisibility(View.VISIBLE);
        holder.systemMessage.setText(TimeUtil.getChatTimeStr(message.getSenderOrreceiverTime(),context));
        if (message!=null)
            showMessage(holder,message);

    }

    /**
     * 在聊天界面显示消息
     *
     * @param viewHolder 界面样式
     * @param
     */
    public synchronized void showMessage(ChatAdapter.ViewHolder viewHolder, final Message message) {
        if(message.getMessageType()!=null) {
            switch (message.getMessageType()) {
                case TEXT:
                    TextView tv = new TextView(context);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    tv.setTextColor(context.getResources().getColor(message.isSelf() ? R.color.white : R.color.black));
                    tv.setText(message.getSenderOrreceiverMessage());
                    RelativeLayout relativeLayout = getBubbleView(viewHolder, message);
                    relativeLayout.removeAllViews();
                    relativeLayout.addView(tv);
                    break;
                case VOICE:
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setGravity(Gravity.CENTER);
                    ImageView voiceIcon = new ImageView(context);
                    voiceIcon.setBackgroundResource(message.isSelf() ? R.drawable.right_voice : R.drawable.left_voice);
                    final AnimationDrawable frameAnimatio = (AnimationDrawable) voiceIcon.getBackground();

                    TextView voicetv = new TextView(context);
                    voicetv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    voicetv.setTextColor(context.getResources().getColor(message.isSelf() ? R.color.white : R.color.black));
                    voicetv.setText(String.valueOf(message.getDuration() + "’"));
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
                    int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(width, height);
                    if (message.isSelf()) {
                        linearLayout.addView(voicetv);
                        imageLp.setMargins(10, 0, 0, 0);
                        voiceIcon.setLayoutParams(imageLp);
                        linearLayout.addView(voiceIcon);
                    } else {
                        voiceIcon.setLayoutParams(imageLp);
                        linearLayout.addView(voiceIcon);
                        lp.setMargins(10, 0, 0, 0);
                        voicetv.setLayoutParams(lp);
                        linearLayout.addView(voicetv);
                    }
                    RelativeLayout voiceRelativeLayout = getBubbleView(viewHolder, message);
                    voiceRelativeLayout.removeAllViews();
                    voiceRelativeLayout.addView(linearLayout);
                    voiceRelativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            messageClickCallBack.play(message, frameAnimatio);

                        }
                    });
                    break;
                case IMAGE:
                    ImageView imageView = new ImageView(context);
                    RelativeLayout imagerelativeLayout = getBubbleView(viewHolder, message);
                    if (null==message.getSenderOrreceiverMessage()){
                        messageClickCallBack.imageLoad(message,imageView,imagerelativeLayout);
                    }
                    else {
                        imageView.setImageBitmap(getThumb(message.getSenderOrreceiverMessage().toString()));
                        imagerelativeLayout.removeAllViews();
                        imagerelativeLayout.addView(imageView);
                    }
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            messageClickCallBack.imageClick(message);
                        }
                    });
                    break;
            }
            showStatus(viewHolder, message);
        }
    }


    /**
     * 生成缩略图
     * 缩略图是将原图等比压缩，压缩后宽、高中较小的一个等于198像素
     * 详细信息参见文档
     */
    public Bitmap getThumb(String path){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int reqWidth, reqHeight, width=options.outWidth, height=options.outHeight;
        if (width > height){
            reqWidth = 198;
            reqHeight = (reqWidth * height)/width;
        }else{
            reqHeight = 198;
            reqWidth = (width * reqHeight)/height;
        }
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        try{
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            Matrix mat = new Matrix();
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            ExifInterface ei =  new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mat.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mat.postRotate(180);
                    break;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        }catch (IOException e){
            return null;
        }
    }
    /**
     * 获取显示气泡
     *
     * @param viewHolder 界面样式
     */
    public RelativeLayout getBubbleView(ChatAdapter.ViewHolder viewHolder,Message message){
        viewHolder.systemMessage.setVisibility( View.VISIBLE);
        viewHolder.systemMessage.setText(TimeUtil.getChatTimeStr(message.getSenderOrreceiverTime(),context));
        if (message.isSelf()){
            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.VISIBLE);
            return viewHolder.rightMessage;
        }else{
            viewHolder.leftPanel.setVisibility(View.VISIBLE);
            viewHolder.rightPanel.setVisibility(View.GONE);
            //viewHolder.sender.setVisibility(View.GONE);
            return viewHolder.leftMessage;
        }

    }

    /**
     * 显示消息状态
     *
     * @param viewHolder 界面样式
     */
    public void showStatus(ChatAdapter.ViewHolder viewHolder,Message message){
        switch (message.messageStatus){
            case Sending:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.VISIBLE);
                break;
            case SendSucc:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.GONE);
                break;
            case SendFail:
                viewHolder.error.setVisibility(View.VISIBLE);
                viewHolder.sending.setVisibility(View.GONE);
                viewHolder.leftPanel.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        Log.d("zhouyoukun","count"+list.size());
        return list.size();
    }

    public static  class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout leftMessage;
        public RelativeLayout rightMessage;
        public RelativeLayout leftPanel;
        public RelativeLayout rightPanel;
        public ProgressBar sending;
        public ImageView error;
        public TextView sender;
        public TextView systemMessage;
        public TextView rightDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            leftMessage=itemView.findViewById(R.id.leftMessage);
            rightMessage=itemView.findViewById(R.id.rightMessage);
            leftPanel=itemView.findViewById(R.id.leftPanel);
            rightPanel=itemView.findViewById(R.id.rightPanel);
            sending=itemView.findViewById(R.id.sending);
            error=itemView.findViewById(R.id.sendError);
            sender=itemView.findViewById(R.id.sender);
            systemMessage=itemView.findViewById(R.id.systemMessage);
            rightDesc=itemView.findViewById(R.id.rightDesc);
        }
    }
}
