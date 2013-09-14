package com.bn.reader;

public class AdThread  extends Thread
{
	boolean adFlag=true;//控制刷新的标志位
	ReaderView reader;//阅读界面的引用
	//创建即可得到引用
	public AdThread(ReaderView reader)//创建即可得到引用
	{
		this.reader=reader;//拿到ReaderView的引用
	}
	//线程运行
	public void run()//线程运行
	{ 
		while(adFlag)
		{				
		 try{
			 Constant.NUM=(Constant.NUM+1)%reader.ad.length;
			 if(reader.repaintAdFlag){				 
				 reader.repaint();//重绘界面
			 }
			 Thread.sleep(1000);//间隔1000ms重新绘制一次
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	//用来停止线程
	public synchronized void stopCurrentThread() {
        this.adFlag = false;//用来停止线程
    }
}