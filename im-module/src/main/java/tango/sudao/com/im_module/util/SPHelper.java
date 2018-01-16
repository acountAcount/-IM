package tango.sudao.com.im_module.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhuyijun on 2017/6/7.
 * SharedPreferences管理类
 */

public class SPHelper {

    //SharedPreferences全局存储的名字
    public static final String FILE_NAME = "call_login_sp_data";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存String类型
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean putString(String key, String value,Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        return edit.commit();
    }

    /**
     * 获取String类型
     *
     * @param key
     * @param
     * @return
     */
    public static String getString(String key,Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }



}
