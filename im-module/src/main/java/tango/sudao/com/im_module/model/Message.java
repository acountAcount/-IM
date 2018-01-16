package tango.sudao.com.im_module.model;


import android.text.SpannableStringBuilder;

import tango.sudao.com.im_module.interfaces.ImageType;

/**
 * 消息数据基类
 */
public  class Message {
    public String messageId;  //消息id  毕传

    //必传
    public MessageType messageType;//消息的类型 ，如文字，图片，表情...

    //必传
    public MessageStatus  messageStatus; //发送状态，发送成功失败，发送中等

    public String senderId;//发送人Id或者组i

    public String senderName;//发送人姓名或者组名称

    public String receiverName;//接收人姓名

    public SpannableStringBuilder senderOrreceiverMessage;//发送的消息内容

    public long senderOrreceiverTime;//发送的时间或者接收的时间

    public boolean isSelf;//是否是自己发的

    public boolean isRead;//是否读取

    public boolean hasTime;  //是否需要显示时间获取

    public String roomType;//消息的类型 ，如群聊，单聊...

    public long duration;//消息时长，针对语音情况

    public String voiceFile;//语音文件路径

    public Object customObject;  //扩展对象，供自定义



    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public SpannableStringBuilder getSenderOrreceiverMessage() {
        return senderOrreceiverMessage;
    }

    public void setSenderOrreceiverMessage(SpannableStringBuilder senderOrreceiverMessage) {
        this.senderOrreceiverMessage = senderOrreceiverMessage;
    }

    public long getSenderOrreceiverTime() {
        return senderOrreceiverTime;
    }

    public void setSenderOrreceiverTime(long senderOrreceiverTime) {
        this.senderOrreceiverTime = senderOrreceiverTime;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public boolean isHasTime() {
        return hasTime;
    }

    public void setHasTime(boolean hasTime) {
        this.hasTime = hasTime;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getVoiceFile() {
        return voiceFile;
    }

    public void setVoiceFile(String voiceFile) {
        this.voiceFile = voiceFile;
    }

    public Object getCustomObject() {
        return customObject;
    }

    public void setCustomObject(Object customObject) {
        this.customObject = customObject;
    }

}
