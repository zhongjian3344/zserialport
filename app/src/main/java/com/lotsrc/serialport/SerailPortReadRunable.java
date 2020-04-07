package com.lotsrc.serialport;

import android.os.SystemClock;

import com.lotsrc.common.SPCoreConvertUtils;
import com.lotsrc.common.SPCoreLogUtil;

/**
 * Created by zj on 2017/8/24.
 */
class SerailPortReadRunable implements Runnable
{
    private SerialPortOperator serialPortOperator=null;
    private byte[] bytesReceived = new byte[512];

    public SerailPortReadRunable(SerialPortOperator serialPortOperator)
    {
        this.serialPortOperator=serialPortOperator;
    }

    @Override
    public void run()
    {
        SPCoreLogUtil.println(serialPortOperator.spoName+"->ThreadRead线程启动");
        int readSize=0;
        //接收到流的数量
        int receiveCount = 0;
        try
        {
            while(!Thread.currentThread().isInterrupted())
            {
                readSize=serialPortOperator.serialPort.getInputStream().available();

                if(readSize>0)
                {
                    SPCoreLogUtil.println(serialPortOperator.spoName+"->------------准备读取数据------------");
                    receiveCount=(serialPortOperator.serialPort.getInputStream().read(bytesReceived));
                    SPCoreLogUtil.println(serialPortOperator.spoName+"->收到数据大小 readSize: "+readSize+"  receiveCount: "+receiveCount);
                    SPCoreLogUtil.println(serialPortOperator.spoName+"->收到原始数据:"+ SPCoreConvertUtils.bytesToHexString(bytesReceived,receiveCount));

                    //提交数据
                    reportData(bytesReceived,receiveCount);
                }
                SystemClock.sleep(200);
            }
            SPCoreLogUtil.println(serialPortOperator.spoName+"->ThreadRead run() 线程正常退出");
        }
        catch (Exception e)
        {
            SPCoreLogUtil.println(serialPortOperator.spoName+"->ThreadRead线程(catch):"+e.toString());
            //提交错误
            reportError(e.toString());
        }
        finally
        {
            SPCoreLogUtil.println(serialPortOperator.spoName+"->ThreadRead 线程(finally)");
            //释放资源
            serialPortOperator.dispose();
        }
    }

    //收到数据上报上层
    private void reportData(byte[] bytesReceived,int receiveCount)
    {
        if(serialPortOperator.iSerialPortListener!=null)
        {
            serialPortOperator.iSerialPortListener.onReceiveData(bytesReceived,receiveCount);
        }
    }

    //收到错误上报上层
    private void reportError(String errorInfo)
    {
        if(serialPortOperator.iSerialPortListener!=null)
        {
            serialPortOperator.iSerialPortListener.onReceiveError(errorInfo);
        }
    }

}
