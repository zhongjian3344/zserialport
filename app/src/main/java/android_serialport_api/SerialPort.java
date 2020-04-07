package android_serialport_api;

import com.lotsrc.common.SPCoreLogUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort
{
    public static final String TAG = SerialPort.class.getName();

    private File device;
    private int baudRate;
    private int flags;


	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(){}

	//初始化串口
	public SerialPort(File device, int baudRate, int flags)
	{
		if (!device.canRead() || !device.canWrite())
		{
			try
			{
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
				SPCoreLogUtil.println("SerialPort:初始化串口:"+device+" /system/bin/su");

				//su = Runtime.getRuntime().exec("/system/xbin/su");
				//String cmd = "chmod 777 " + device.getAbsolutePath() + "\n" + "exit\n";

				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite())
				{
					SPCoreLogUtil.println(device+"SerialPort:初始化串口 出错 ");
				}
			}
			catch (Exception e)
			{
				SPCoreLogUtil.println(device+" SerialPort:初始化串口 出错:"+e.toString());
			}
		}

        this.device = device;
        this.baudRate = baudRate;
        this.flags = flags;
	}

	//获取输入流
	public InputStream getInputStream()
	{
		return mFileInputStream;
	}

	//获取输出流
	public OutputStream getOutputStream()
	{
		return mFileOutputStream;
	}

	//打开串口
    public boolean open()
	{
		try
		{
			mFd = open(device.getAbsolutePath(), baudRate, flags);
			if (mFd == null)
			{
				SPCoreLogUtil.println("SerialPort:open() is null");
				return false;
			}
			mFileInputStream = new FileInputStream(mFd);
			mFileOutputStream = new FileOutputStream(mFd);
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
    }

    //关闭流
	public boolean closeStream()
	{
		boolean backResult=true;
		if(mFileInputStream!=null)
		{
			try
			{
				mFileInputStream.close();
				mFileInputStream=null;
			}
			catch (IOException e)
			{
				SPCoreLogUtil.println("SerialPort:mFileInputStream closeStream()"+e.toString());
				backResult=false;
			}
		}
		if(mFileOutputStream!=null)
		{
			try
			{
				mFileOutputStream.close();
				mFileOutputStream=null;
			}
			catch (IOException e)
			{
				SPCoreLogUtil.println("SerialPort:mFileOutputStream closeStream()"+e.toString());
				backResult=false;
			}
		}
		return backResult;
	}

	/**
	 * 关闭串口
	 */
	public void closeSP()
	{
		close();
		mFd = null;
	}

	public String getStringFormJNI()
	{
		return stringFromJNI();
	}

	// JNI
	private native static FileDescriptor open(String path, int baudRate, int flags);
	//JNI
	public native void close();
	//JNI
	public native String stringFromJNI();

	static
	{
		SPCoreLogUtil.println("before ylspcore  serial_port load");
		System.loadLibrary("serial_port");
		SPCoreLogUtil.println("after ylspcore serial_port load");
	}
}
