package tango.sudao.com.im_module.interfaces;

import java.util.List;

import tango.sudao.com.im_module.model.Message;

/**
 * Created by pcdalao on 2018/1/16.
 */

public interface MessageListsRequest {

    void onSuccess(List<Message> list);
    void onError(List<Message> list);
}
