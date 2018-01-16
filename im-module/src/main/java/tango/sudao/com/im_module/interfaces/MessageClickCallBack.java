package tango.sudao.com.im_module.interfaces;

import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import tango.sudao.com.im_module.model.Message;

/**
 * Created by pcdalao on 2018/1/13.
 */

public interface MessageClickCallBack {
    void play(Message message, AnimationDrawable animationDrawable) ;

    void imageLoad(Message message, ImageView imageView, RelativeLayout relativeLayout);
    void imageClick(Message message);
}
