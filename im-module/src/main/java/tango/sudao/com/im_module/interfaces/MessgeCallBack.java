package tango.sudao.com.im_module.interfaces;

import tango.sudao.com.im_module.model.Message;

/**
 * Created by pcdalao on 2018/1/11.
 */

public interface MessgeCallBack {
    /**
     * 接收新来的消息
     * @param message
     */
    void newMessage(Message message);
}
