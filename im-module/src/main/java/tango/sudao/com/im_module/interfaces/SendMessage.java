package tango.sudao.com.im_module.interfaces;


/**
 * 聊天界面的接口
 */
public interface SendMessage {

    /**
     * 发送图片消息
     *
     */
    void sendImage();


    /**
     * 发送照片消息
     *
     */
    void sendPhoto();


    /**
     * 发送文字消息
     *
     */
    void sendText();

    /**
     * 发送文件
     *
     */
    void sendFile();


    /**
     * 开始发送语音消息
     *
     */
    void startSendVoice();


    /**
     * 结束发送语音消息
     *
     */
    void endSendVoice();


    /**
     * 发送小视频消息
     *
     * @param fileName 文件名
     */
    void sendVideo(String fileName);


    /**
     * 结束发送语音消息
     *
     */
    void cancelSendVoice();

    /**
     * 正在发送
     *
     */
    void sending();

    /**
     * 视频通话
     */
    void sendVideoChat();

    /**
     * 音频通话
     */
    void sendVoip();

}
