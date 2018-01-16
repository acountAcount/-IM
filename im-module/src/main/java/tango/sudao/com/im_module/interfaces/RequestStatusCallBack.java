package tango.sudao.com.im_module.interfaces;

import tango.sudao.com.im_module.model.Message;

/**
 * Created by pcdalao on 2018/1/11.
 */

public interface RequestStatusCallBack {

     void onSuccess(Message message);
     void onError(Message message);
}
