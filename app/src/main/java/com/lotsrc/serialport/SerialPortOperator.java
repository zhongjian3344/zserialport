/**
 * @Description:主要功能: 类型转换类
 * @Prject: ylspcore
 * @Package: com.levending.ylspcore.serialport
 * @author: zhongjian
 * @date: 2017年08月24日 15:26
 * @Copyright: 个人版权所有
 * @Company:
 * @version: 1.0.0
 */

package com.lotsrc.serialport;

import android.os.SystemClock;


import com.lotsrc.common.SPCoreConvertUtils;
import com.lotsrc.common.SPCoreLogUtil;

import java.io.File;
import java.io.IOException;

import android_serialport_api.SerialPort;


public class SerialPortOperator
{
    //串口
    SerialPort serialPort=null;
    //数据读取线程
    Thread threadReadData=null;
    //串口名字
    public String spoName="spoName";
    //串口打开状态
    boolean openState=false;

    //接口
    ISerialPortListener iSerialPortListener=null;

    /**
     * 初始化串口
     * @param devicePathName
     * @param baudRate
     * @param flags
     */
    public void initSerialPort(String devicePathName, int baudRate, int flags)
    {
        spoName=devicePathName+"--"+baudRate;
        if (serialPort == null)
        {
            serialPort = new SerialPort(new File(devicePathName), baudRate, flags);
            SPCoreLogUtil.println(spoName + "initSerialPort() 初始化串口");
        }
        else
        {
            SPCoreLogUtil.println(spoName + "->initSerialPort() 串口已经被初始化");
        }
    }

    //注册事件
    public void setListener(ISerialPortListener tempListener)
    {
        iSerialPortListener=tempListener;
    }
    //取消事件
    public  void removeListener()
    {
        iSerialPortListener=null;
    }


    /**
     * 打开串口
     */
    public boolean openSerialPort()
    {
        if (openState)
        {
            SPCoreLogUtil.println(spoName+"->SerialPortOperator openSerialPort() 此串口已经被打开");
            return openState;
        }

        if (serialPort==null)
        {
            SPCoreLogUtil.println(spoName+"->SerialPortOperator openSerialPort() 请先初始化串口");
            return false;
        }

        if (serialPort!=null)
        {
            openState=serialPort.open();
            SPCoreLogUtil.println(spoName+"->SerialPortOperator openSerialPort() 打开串口:"+openState);
            //启动读取线程
            if(threadReadData==null && openState==true)
            {
                //启动读数据线程
                threadReadData=new Thread(new SerailPortReadRunable(this));
                threadReadData.setName(spoName+"_SerailPortReadRunable");
                threadReadData.start();
            }
        }

        return openState;
    }


    /**
     * 退出读取线程
     */
    public void exitReadThread()
    {
        SPCoreLogUtil.println(spoName+"->SerialPortOperator closeSerialPort() 开始终止线程");
        if (threadReadData!=null)
        {
            //关闭读数据线程
            threadReadData.interrupt();
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort()
    {
        SPCoreLogUtil.println(spoName+"->SerialPortOperator closeSerialPort() 关闭串口");
        //退出线程
        exitReadThread();
    }

    /**
     * 释放资源
     */
    public void dispose()
    {
        if(threadReadData!=null)
        {
            threadReadData=null;
        }
        if(serialPort!=null)
        {
            boolean backResult=serialPort.closeStream();
            SPCoreLogUtil.println(spoName+"->SerialPortOperator dispose() 关闭流结果:"+backResult);
            openState=false;
            serialPort.closeSP();
            serialPort=null;
            SPCoreLogUtil.println(spoName+"->SerialPortOperator dispose() openState:"+openState);
        }
    }


    /**
     * 发送数据
     * @param data
     */
    public boolean sendData(byte[] data)
    {
        boolean isSendResult=false;
        if(serialPort!=null && data!=null)
        {
            try
            {
                SystemClock.sleep(10);
                serialPort.getOutputStream().write(data);
                serialPort.getOutputStream().flush();
                isSendResult=true;
                SPCoreLogUtil.println(spoName+"->SerialPortOperator sendData("+ SPCoreConvertUtils.bytesToHexString(data,data.length)+") 长度: "+data.length+" 发送结果:"+isSendResult);
                if (iSerialPortListener!=null)
                {
                    iSerialPortListener.onSendData(data);
                }
            }
            catch (Exception e)
            {
                isSendResult=false;
                if (iSerialPortListener!=null)
                {
                    iSerialPortListener.onReceiveError(e.toString());
                }
            }
        }
        return isSendResult;
    }

}
