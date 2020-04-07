package com.lotsrc.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.lotsrc.common.SPCoreConvertUtils;
import com.lotsrc.common.SPCoreLogUtil;
import com.lotsrc.serialport.ISerialPortListener;
import com.lotsrc.serialport.SerialPortOperator;
import com.lotsrc.zserialport.R;


public class MainActivity extends Activity {

    SerialPortOperator serialPortOperator=new SerialPortOperator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //是否是调试模式
        SPCoreLogUtil.isDebug=true;

        findViewById(R.id.btnInit).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                serialPortOperator.initSerialPort("/dev/ttyO2",9600,0);
                serialPortOperator.setListener(new OnSerialPortListener());
            }
        });

        findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                serialPortOperator.openSerialPort();
            }
        });

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialPortOperator.closeSerialPort();
            }
        });

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] testByts={1,2,3,4,5};
                serialPortOperator.sendData(testByts);
            }
        });

        findViewById(R.id.btnCloseTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    //接收到的数据
    private class OnSerialPortListener implements ISerialPortListener
    {
        @Override
        public void onReceiveData(byte[] bytesData, int receiveCount)
        {
            SPCoreLogUtil.println("接收到的数据:"+ SPCoreConvertUtils.bytesToHexString(bytesData,receiveCount));
        }

        @Override
        public void onSendData(byte[] bytesData)
        {
            SPCoreLogUtil.println("MainActivity 发送的数据:"+ SPCoreConvertUtils.bytesToHexString(bytesData,bytesData.length));
        }

        @Override
        public void onReceiveError(String errorInfo)
        {
            SPCoreLogUtil.println("MainActivity 接收到的错误:"+errorInfo);
        }
    }

}
