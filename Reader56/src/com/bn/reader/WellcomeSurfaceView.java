package com.bn.reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WellcomeSurfaceView extends SurfaceView 
implements SurfaceHolder.Callback  //ʵ���������ڻص��ӿ�
{
	ReaderActivity activity;//����readeractivity�Ķ���
	Paint paint;//����
	int currentAlpha=0;//��ǰ�Ĳ�͸��ֵ
	
	int sleepSpan=50;//������ʱ��ms
	
	Bitmap[] logos=new Bitmap[2];//logoͼƬ����
	Bitmap currentLogo;//��ǰlogoͼƬ����
	int currentX;//ͼƬλ�õ�����
	int currentY;//ͼƬλ�õ�����
	
	public WellcomeSurfaceView(ReaderActivity activity) {//������
		super(activity);
		this.activity = activity; //activity���ø�ֵ
		this.getHolder().addCallback(this);//�����������ڻص��ӿڵ�ʵ����
		paint = new Paint();//��������
		paint.setAntiAlias(true);//�򿪿����
		
		//����ͼƬ
		logos[0]=BitmapFactory.decodeResource(activity.getResources(), R.drawable.dukea); 
		logos[1]=BitmapFactory.decodeResource(activity.getResources(), R.drawable.dukeb);		
	}

	public void onDraw(Canvas canvas){	//onDraw����
		//���ƺ��������屳��
		paint.setColor(Color.BLACK);//���û�����ɫ
		paint.setAlpha(255);
		canvas.drawRect(0, 0, Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT, paint);
		//����ƽ����ͼ
		if(currentLogo==null)return;//���ͼƬ����Ϊnull�򷵻�
		paint.setAlpha(currentAlpha);//����͸����		
		//����ͼƬ
		canvas.drawBitmap(currentLogo, currentX, currentY, paint);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}

	public void surfaceCreated(SurfaceHolder holder) {//����ʱ������		
		new Thread()  //����һ���µ��߳�
		{
			public void run()//��дrun�ķ���
			{
				for(Bitmap bm:logos)//����λͼ��Դ��forѭ��
				{
					currentLogo=bm;//Ϊ��ǰͼƬ��Դ�����ø�ֵ
					//����ͼƬλ��
					currentX=Constant.SCREEN_WIDTH/2-bm.getWidth()/2;
					currentY=Constant.SCREEN_HEIGHT/2-bm.getHeight()/2;
					
					for(int i=255;i>-10;i=i-10)//ˢ��͸���ȵ�ѭ��
					{//��̬����ͼƬ��͸����ֵ�������ػ�	
						currentAlpha=i;//͸���ȵĿ�ʼֵΪ255
						if(currentAlpha<0)
						{
							currentAlpha=0;
						}
						SurfaceHolder myholder=WellcomeSurfaceView.this.getHolder();
						Canvas canvas = myholder.lockCanvas();//��ȡ����
						try{
							    synchronized(myholder){//����myholder
								draw(canvas);//����
							    }
						}
						catch(Exception e){ //�����쳣
							e.printStackTrace(); //�����쳣
						}
						finally{
							if(canvas != null){  //��canvs��Ϊ��ʱ
								myholder.unlockCanvasAndPost(canvas);//�������
							}
						}
						
						try  //�����쳣
						{
							if(i==255)
							{//������ͼƬ����ȴ�һ��
								Thread.sleep(1000);
							}
							Thread.sleep(sleepSpan);//�̶߳���Ϣ50ms
						}
						catch(Exception e)//�����쳣
						{
							e.printStackTrace();//�����쳣
						}
					}
				}
				activity.sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);
			}
		}.start();//��ʼ�߳�
		
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {//����ʱ������

	}
}