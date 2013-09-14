package com.bn.reader;

public class AdThread  extends Thread
{
	boolean adFlag=true;//����ˢ�µı�־λ
	ReaderView reader;//�Ķ����������
	//�������ɵõ�����
	public AdThread(ReaderView reader)//�������ɵõ�����
	{
		this.reader=reader;//�õ�ReaderView������
	}
	//�߳�����
	public void run()//�߳�����
	{ 
		while(adFlag)
		{				
		 try{
			 Constant.NUM=(Constant.NUM+1)%reader.ad.length;
			 if(reader.repaintAdFlag){				 
				 reader.repaint();//�ػ����
			 }
			 Thread.sleep(1000);//���1000ms���»���һ��
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	//����ֹͣ�߳�
	public synchronized void stopCurrentThread() {
        this.adFlag = false;//����ֹͣ�߳�
    }
}