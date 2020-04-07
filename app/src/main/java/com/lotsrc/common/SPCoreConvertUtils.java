package com.lotsrc.common;

import java.util.Locale;

/**
 * Created by zj on 2017/8/25.
 */

public class SPCoreConvertUtils
{
    /**
     * byte数组转换为十六进制字符串
     *
     * @param b
     * @return
     */
    public static String bytesToHexString(byte[] b,int count)
    {
        if (count == 0)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < count; i++)
        {
            int value = b[i] & 0xFF;
            String hv = Integer.toHexString(value);
            if (hv.length() < 2)
            {
                sb.append(0);
            }
            sb.append(hv);
        }
        return sb.toString().toUpperCase(Locale.US);
    }

}
