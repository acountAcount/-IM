package tango.sudao.com.im_module.model;

/**
 * Created by pcdalao on 2018/1/10.
 * 发送状态，发送成功失败，发送中等
 */

public enum  MessageStatus{
    Invalid,
    Sending,
    SendSucc,
    SendFail,
    HasDeleted,
    ISRECEIVE
}