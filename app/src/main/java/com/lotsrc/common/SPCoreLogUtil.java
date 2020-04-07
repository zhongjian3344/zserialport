package com.lotsrc.common;

import android.util.Log;

/**
 * Created by zj on 2017/8/25.
 */

public class SPCoreLogUtil
{
    //是否调试
    public static boolean isDebug=true;

    public static String tag="ylspcore";

    /**
     * 输出日志
     * @param logStr
     */
    public static void println(String logStr)
    {
        if (isDebug)
        {
            Log.v(tag,logStr);
        }
    }
}
