package com.bn.reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WellcomeSurfaceView extends SurfaceView 
implements SurfaceHolder.Callback  //实现生命周期回调接口
{
	ReaderActivity activity;//声明readeractivity的对象
	Paint paint;//画笔
	int currentAlpha=0;//当前的不透明值
	
	int sleepSpan=50;//动画的时延ms
	
	Bitmap[] logos=new Bitmap[2];//logo图片数组
	Bitmap currentLogo;//当前logo图片引用
	int currentX;//图片位置的坐标
	int currentY;//图片位置的坐标
	
	public WellcomeSurfaceView(ReaderActivity activity) {//构造器
		super(activity);
		this.activity = activity; //activity引用赋值
		this.getHolder().addCallback(this);//设置生命周期回调接口的实现者
		paint = new Paint();//创建画笔
		paint.setAntiAlias(true);//打开抗锯齿
		
		//加载图片
		logos[0]=BitmapFactory.decodeResource(activity.getResources(), R.drawable.dukea); 
		logos[1]=BitmapFactory.decodeResource(activity.getResources(), R.drawable.dukeb);		
	}

	public void onDraw(Canvas canvas){	//onDraw方法
		//绘制黑填充矩形清背景
		paint.setColor(Color.BLACK);//设置画笔颜色
		paint.setAlpha(255);
		canvas.drawRect(0, 0, Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT, paint);
		//进行平面贴图
		if(currentLogo==null)return;//如果图片引用为null则返回
		paint.setAlpha(currentAlpha);//设置透明度		
		//绘制图片
		canvas.drawBitmap(currentLogo, currentX, currentY, paint);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}

	public void surfaceCreated(SurfaceHolder holder) {//创建时被调用		
		new Thread()  //创建一个新的线程
		{
			public void run()//重写run的方法
			{
				for(Bitmap bm:logos)//创建位图资源的for循环
				{
					currentLogo=bm;//为当前图片资源的引用赋值
					//计算图片位置
					currentX=Constant.SCREEN_WIDTH/2-bm.getWidth()/2;
					currentY=Constant.SCREEN_HEIGHT/2-bm.getHeight()/2;
					
					for(int i=255;i>-10;i=i-10)//刷新透明度的循环
					{//动态更改图片的透明度值并不断重绘	
						currentAlpha=i;//透明度的开始值为255
						if(currentAlpha<0)
						{
							currentAlpha=0;
						}
						SurfaceHolder myholder=WellcomeSurfaceView.this.getHolder();
						Canvas canvas = myholder.lockCanvas();//获取画布
						try{
							    synchronized(myholder){//锁定myholder
								draw(canvas);//绘制
							    }
						}
						catch(Exception e){ //捕获异常
							e.printStackTrace(); //处理异常
						}
						finally{
							if(canvas != null){  //当canvs不为空时
								myholder.unlockCanvasAndPost(canvas);//解除锁定
							}
						}
						
						try  //处理异常
						{
							if(i==255)
							{//若是新图片，多等待一会
								Thread.sleep(1000);
							}
							Thread.sleep(sleepSpan);//线程多休息50ms
						}
						catch(Exception e)//捕获异常
						{
							e.printStackTrace();//处理异常
						}
					}
				}
				activity.sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);
			}
		}.start();//开始线程
		
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {//销毁时被调用

	}
}