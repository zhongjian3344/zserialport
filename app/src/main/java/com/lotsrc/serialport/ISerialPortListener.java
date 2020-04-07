package com.lotsrc.serialport;

/**
 * Created by zj on 2017/8/25.
 */

public interface ISerialPortListener
{
    void onReceiveData(byte[] bytesData, int receiveCount);
    void onSendData(byte[] bytesData);
    void onReceiveError(String errorInfo);
}
