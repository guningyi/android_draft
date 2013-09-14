package com.bn.reader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import static com.bn.reader.Constant.*;

enum TurnDir{
	noTurning,left,right,//不翻页，向前翻，向后翻
}

public class ReaderView extends ScrollView{
	ReaderActivity readerActivity;//ReaderActivity的引用
	Paint paint;
	Bitmap bmLeft;
    ReadRecord currRR;//当前页数据
	
	//翻页中用到的临时对象
	Bitmap bmLeft_temp;//左边图片临时引用	
	ReadRecord currRR_temp;//记录ReadRecord的一个临时对象
	Bitmap bmBack;// 底纹图片
	Bitmap title;// 标头图片/

	//AdThread at;//广告条的刷新线程
	//广告图片数组
	//int ad[]={R.drawable.ad_a,R.drawable.ad_b,R.drawable.ad_c,R.drawable.ad_d,
	//		R.drawable.ad_e,R.drawable.ad_f,R.drawable.ad_g,R.drawable.ad_h};
	//加载的广告图片数组
	//Bitmap adb[]=new Bitmap[ad.length];
	
	//Bitmap hiddenBar; //guningyi add 用于绘制hiddenBar图片
	//boolean show_hiddenBar_flag = false;
	
	//当前这个文本文件（此本书）的阅读数据
	HashMap<Integer,ReadRecord> currBook=new HashMap<Integer,ReadRecord>();
	
	//当前翻页触控点坐标
	float ax=-1;
	float ay=-1;	
	//右下角坐标
	int bx;
	int by;
	
	//int[] cd;//c、d两点坐标数组,其中c、d两点分别为翻折线与页面宽和高的交点
	TurnDir turnDir=TurnDir.noTurning;//翻页方向，枚举类型
	boolean repaintAdFlag=true;//绘制广告的标志
	//ReaderView的构造方法
	public ReaderView(Context context)
	{
		spuer(context);
	}
	
	public ReaderView(ReaderActivity readerActivity) {
			super(readerActivity);  //调用父类方法		
			this.readerActivity=readerActivity;  //成员变量赋值
			
			this.getHolder().addCallback((Callback) this);//回调接口
			//创建画笔
			paint=new Paint();		
		}
	
}

public class ReaderView_backup extends SurfaceView implements SurfaceHolder.Callback
{

	
	ReaderActivity readerActivity;//ReaderActivity的引用
	Paint paint;//画笔的引用
	//将要绘制的左右两幅图的引用
	Bitmap bmLeft;//左边的
	ReadRecord currRR;//当前页数据
	
	//翻页中用到的临时对象
	Bitmap bmLeft_temp;//左边图片临时引用	
	ReadRecord currRR_temp;//记录ReadRecord的一个临时对象
	Bitmap bmBack;// 底纹图片
	Bitmap title;// 标头图片

	AdThread at;//广告条的刷新线程
	//广告图片数组
	int ad[]={R.drawable.ad_a,R.drawable.ad_b,R.drawable.ad_c,R.drawable.ad_d,
			R.drawable.ad_e,R.drawable.ad_f,R.drawable.ad_g,R.drawable.ad_h};
	//加载的广告图片数组
	Bitmap adb[]=new Bitmap[ad.length];
	
	Bitmap hiddenBar; //guningyi add 用于绘制hiddenBar图片
	boolean show_hiddenBar_flag = false;
	
	//当前这个文本文件（此本书）的阅读数据
	HashMap<Integer,ReadRecord> currBook=new HashMap<Integer,ReadRecord>();
	
	//当前翻页触控点坐标
	float ax=-1;
	float ay=-1;	
	//右下角坐标
	int bx;
	int by;
	
	int[] cd;//c、d两点坐标数组,其中c、d两点分别为翻折线与页面宽和高的交点
	TurnDir turnDir=TurnDir.noTurning;//翻页方向，枚举类型
	boolean repaintAdFlag=true;//绘制广告的标志
	//ReaderView的构造方法
    public ReaderView(ReaderActivity readerActivity) {
		super(readerActivity);  //调用父类方法		
		this.readerActivity=readerActivity;  //成员变量赋值
		
		this.getHolder().addCallback((Callback) this);//回调接口
		//创建画笔
		paint=new Paint();		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		at=new AdThread(this);//创建广告刷新线程
		bmBack=PicLoadUtil.LoadBitmap(this.getResources(), BITMAP);//自适应屏幕的背景图片
		bmBack=PicLoadUtil.scaleToFit(bmBack, PAGE_WIDTH, PAGE_HEIGHT);
		
		title=PicLoadUtil.LoadBitmap(this.getResources(), R.drawable.bt);//自适应屏幕的标头图片
		title=PicLoadUtil.scaleToFit(title, SCREEN_WIDTH, BLANK);
		
		for(int i=0;i<ad.length;i++)//自适应屏幕的广告图片
		{
			adb[i]=PicLoadUtil.LoadBitmap(this.getResources(), ad[i]);
			adb[i]=PicLoadUtil.scaleToFit(adb[i], AD_WIDTH, BLANK);
		}
		//初始化到当前文件第X页
		currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);
		
		if(CURRENT_PAGE==0)//如果是第一次打开某一本书
		{
			currBook.put(currRR.pageNo, currRR);//第一页的信息放入hashMap中			
		}
		
		//绘制左右两幅图片
		bmLeft=this.drawPage(currRR);
		//bmRight=this.drawPage(currRR); //253241#guningyi
		repaint();
		at.start();//开启广告刷新线程
		

		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
    }

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		at.stopCurrentThread();//终止进程刷新
	}
	
	
	//ningyigu 编写新的阅读界面绘制函数
	public void onDraw(Canvas canvas)
	{
		synchronized(paint)
		{
		    canvas.drawColor(Color.BLACK);//擦空界面
		    canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, BLANK, paint);//绘制左侧自定义的图片
            //使用bitmap来实现hiddenBar的方案
		    /*		    
            if (show_hiddenBar_flag == true)
		    {
		    	int y = SCREEN_HEIGHT - BLANK;
		    	canvas.drawBitmap(hiddenBar, LEFT_OR_RIGHT_X, y, paint);//guningyi 绘制hiddenBar
		    }
		    else
		    {
		    	canvas.drawBitmap(hiddenBar, SCREEN_WIDTH, SCREEN_HEIGHT, paint);//guningyi 隐藏hiddenBar
		    }*/
		    //canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, 0, paint);//绘制图片
			//恢复画笔状态
			canvas.restore();	
		}
	}
	
	/*//绘制阅读界面的方法
	public void onDraw(Canvas canvas)
	{
		synchronized(paint)//锁定画笔
		{	
			canvas.drawColor(Color.BLACK);//擦空界面
			canvas.drawBitmap(title, 0, 0, paint);//绘制标头图片
			canvas.drawBitmap(adb[NUM],Constant.SCREEN_WIDTH-AD_WIDTH, 0, paint);//绘制广告条
			//drawcut_line(canvas);//绘制分割线  253241#guningyi
			drawTitle(canvas);//绘制题目			
			if(turnDir==TurnDir.right)
			{	
				//这个虚拟页面绘制的位置将是
				//以（0，30）为左上角坐标的图片，使用唯一的虚拟页面bmLeft
				canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, BLANK, paint);//绘制左侧自定义的图片
	
				//根据a点的位置绘制右边用来看的页====================begin===========================
				//保存画笔状态
				canvas.save();
				//新建一个路径
				Path path2=new Path();
				//让路径圈住的范围为e、f、c、d、g
				path2.moveTo(RIGHT_OR_LEFT_x,0);
				path2.lineTo(RIGHT_OR_LEFT_x, PAGE_HEIGHT+BLANK);			
				path2.lineTo(cd[0], cd[1]);
				path2.lineTo(cd[2], cd[3]);
				path2.lineTo(RIGHT_OR_LEFT_x+PAGE_WIDTH, BLANK);
				path2.lineTo(RIGHT_OR_LEFT_x,0);
				//为画笔设置绘制剪裁
				canvas.clipPath(path2);
				//绘制效果是
				//以(right_or_left_x,30)为左上角坐标来绘制图片
				//right_or_left_x的值为屏幕宽度一半+5
				//这样得到的图像效果是屏幕的一半
				//canvas.drawBitmap(bmRight, RIGHT_OR_LEFT_x,BLANK, paint);//绘制右侧图片
				canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, BLANK, paint);// add by guningyi
				
				
				//恢复画笔状态
				canvas.restore();
				//根据a点的位置绘制右边用来看的页=====================end============================
				
				
				//根据a点的位置绘制被翻出来的反的页画面===============begin==========================
				//计算反过来页面的旋转修正角
				float angle=(float)Math.toDegrees(Math.atan((ay-cd[3])/(ax-cd[2])));
				//创建并设置旋转矩阵
				Matrix m1=new Matrix();
				m1.setRotate(90+angle, LEFT_OR_RIGHT_X,PAGE_HEIGHT);
				//将反过来旋转页的左下角对到触控点上
				Matrix m2=new Matrix();
				m2.setTranslate
				(
					ax,	
					ay-PAGE_HEIGHT
				);
				//创建总矩阵
				Matrix mz=new Matrix();
				mz.setConcat(m2, m1);
				//保存画笔状态
				canvas.save();
				//让路径圈住的范围为a、c、d
				Path path3=new Path();
				path3.moveTo(ax,ay);		
				path3.lineTo(cd[0], cd[1]);
				path3.lineTo(cd[2], cd[3]);		
				path3.lineTo(ax,ay);
				canvas.clipPath(path3);
							
	
				canvas.drawBitmap(bmLeft_temp, mz, paint);//绘制左侧自定义的图片
	
				//恢复画笔状态
				canvas.restore();
				//根据a点的位置绘制被翻出来的反的页画面================end===========================
				
				
				//根据a点的位置绘制最下面要被翻出来的页的画面================begin====================
				//保存画笔状态
				canvas.save();
				//新建一个路径
				Path path1=new Path();
				//让路径圈住的范围为c、b、d
				path1.moveTo(cd[0], cd[1]);
				path1.lineTo(bx, by);
				path1.lineTo(cd[2], cd[3]);
				path1.lineTo(cd[0], cd[1]);
				//为画笔设置绘制剪裁
				canvas.clipPath(path1);
				paint.setAlpha(220);
				
                //使用canvas.drawBitmap方法来显示位图对象bmRight_temp
				//以坐标(RIGHT_OR_LEFT_x,BLANK)为左上角坐标，使用画笔paint
				//canvas.drawBitmap(bmRight_temp, RIGHT_OR_LEFT_x,BLANK, paint);//绘制右侧图片
				paint.setAlpha(255);
				//恢复画笔状态
				canvas.restore();	
				//根据a点的位置绘制最下面要被翻出来的页的画面=================end=====================			
	
			}//如果向左翻
			else if(turnDir==TurnDir.left)			
			{
				
				//根据a点的位置绘制左边用来看的页====================begin===========================
				//保存画笔状态
				canvas.save();
				//新建一个路径
				Path path2=new Path();
				//让路径圈住的范围为e、f、c、d、g
				path2.moveTo(PAGE_WIDTH,0);
				path2.lineTo(PAGE_WIDTH, PAGE_HEIGHT+BLANK);			
				path2.lineTo(cd[0], cd[1]);
				path2.lineTo(cd[2], cd[3]);
				path2.lineTo(0, BLANK);	
				path2.lineTo(PAGE_WIDTH,0);	
				//为画笔设置绘制剪裁
				canvas.clipPath(path2);
				canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, BLANK, paint);//绘制左侧自定义的图片
				//恢复画笔状态
				canvas.restore();
				//根据a点的位置绘制左边用来看的页=====================end============================
				
				
				//绘制右边用来看的页
				//ningyigu bmRight -> bmLeft
				//canvas.drawBitmap(bmLeft, RIGHT_OR_LEFT_x,BLANK, paint);
				
				//根据a点的位置绘制最下面要被翻出来的页的画面================begin====================
				//保存画笔状态
				canvas.save();
				//新建一个路径
				Path path1=new Path();
				//让路径圈住的范围为c、b、d
				path1.moveTo(cd[0], cd[1]);
				path1.lineTo(bx, by);
				path1.lineTo(cd[2], cd[3]);
				path1.lineTo(cd[0], cd[1]);
				//为画笔设置绘制剪裁
				canvas.clipPath(path1);
				paint.setAlpha(220);//翻出的角为半透明
				
				canvas.drawBitmap(bmLeft_temp, LEFT_OR_RIGHT_X, BLANK, paint);//绘制左侧自定义的图片
			
				paint.setAlpha(255);
				//恢复画笔状态
				canvas.restore();
				//根据a点的位置绘制最下面要被翻出来的页的画面=================end=====================
				
				
				//根据a点的位置绘制被翻出来的反的页画面===============begin==========================
				//计算反过来页面的旋转修正角
				float angle=(float)Math.toDegrees(Math.atan((ax-cd[0])/(ay-cd[1])));//向前翻页时，计算角度用的c点坐标
				//创建并设置旋转矩阵
				Matrix m1=new Matrix();
				m1.setRotate(-90-angle, PAGE_WIDTH ,PAGE_HEIGHT );//以图片右下角为旋转中心点，逆时针旋转90+angle
				//将反过来旋转页的右下角对到触控点上
				Matrix m2=new Matrix();
				m2.setTranslate
				(
					ax-PAGE_WIDTH ,	
					ay-PAGE_HEIGHT
				);
				//创建总矩阵
				Matrix mz=new Matrix();
				mz.setConcat(m2, m1);
				//保存画笔状态
				canvas.save();
				//让路径圈住的范围为a、c、d
				Path path3=new Path();
				path3.moveTo(ax,ay);		
				path3.lineTo(cd[0], cd[1]);
				path3.lineTo(cd[2], cd[3]);		
				path3.lineTo(ax,ay);
				canvas.clipPath(path3);
				
				//canvas.drawBitmap(bmRight_temp, mz, paint);
				//恢复画笔状态
				canvas.restore();
				//根据a点的位置绘制被翻出来的反的页画面================end===========================
			}
			else//如果一开始没有按触屏左侧，也没有按触屏右侧。原来的实现是建立两幅，现在改为一副。
			{
				canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, BLANK, paint);//绘制左侧自定义的图片
				//canvas.drawBitmap(bmRight, RIGHT_OR_LEFT_x,BLANK, paint);//绘制右侧自定义的图片
			}
		}
	}*/
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent e)
	{
		
		 switch(keyCode)
		 {
		    case 4:
		    	readerActivity.showDialog(readerActivity.EXIT_READER);//退出对话框
				break;
		    case 22:
		    	repaintAdFlag=false;//绘制广告的标志设为false
				//初始化到下一页数据
		    	currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
	
		    	Constant.CURRENT_LEFT_START=currRR.start;//记录当前读到处leftstart的值
		    	Constant.CURRENT_PAGE=currRR.pageNo;//记录当前读到处的page的值
	    	
		    	currBook.put(currRR.pageNo, currRR);//当前页的信息加入hashMap
		    	
		    	
		    	if(currRR.start>Constant.CONTENTCOUNT){
				   Toast.makeText
				   (
						this.getContext(), 
						"B已经到最后一页了，不可以再往后了！", 
						Toast.LENGTH_SHORT
					).show();
				}else
				{
					//绘制左右两幅图片
					bmLeft=drawPage(currRR);
					//bmRight=drawPage(currRR);
					repaint();
				}
		    	repaintAdFlag=true;//换完图片再重绘
				break;
			    case 21:
			    	repaintAdFlag=false;//绘制广告的标志设为false
				   if(currRR.pageNo==0){
						Toast.makeText
						(
							this.getContext(), 
							"B已经到第一页，不可以再往前翻了！", 
							Toast.LENGTH_SHORT
						).show();				
					}
					else
					{
						currRR=currBook.get(currRR.pageNo-1);
						
						Constant.CURRENT_LEFT_START=currRR.start;//记录当前读到处leftstart的值
						Constant.CURRENT_PAGE=currRR.pageNo;//记录当前读到处的page的值

						//currRR.isLeft=true;
						bmLeft=drawPage(currRR);
						//bmRight=drawPage(currRR);
						repaint();
					}
				   repaintAdFlag=true;//换完图片再重绘
				   break;		    	
		    case 82:
		    	readerActivity.openOptionsMenu();
				   break; 
		 }
		   return true;
	}

	 
	//guningyi 自己编写的onTouchEvent函数
	public boolean onTouchEvent(MotionEvent e)
	{
		repaintAdFlag = false;
		float x = e.getX();
		float y = e.getY();
		
		switch(e.getAction())
		{
		    case MotionEvent.ACTION_DOWN:
		    	if(x > RIGHT_OR_LEFT_x){
		    		//初始化为右下角坐标
            		bx=SCREEN_WIDTH;
            		by=PAGE_HEIGHT+BLANK;
		    	}
		    	else{
		    		//初始化为左下角坐标
            		bx=0;
            		by=PAGE_HEIGHT+BLANK;
		    	}
		    	if(x < SCREEN_WIDTH 
		    			&& x > 0.2 *  SCREEN_WIDTH
		    			&& y < SCREEN_HEIGHT
		    			&& y > 0.8 * SCREEN_HEIGHT){
		    		if(Constant.nextPageStart>Constant.CONTENTCOUNT){
        				Toast.makeText
        				(
        					this.getContext(), 
        					"已经到最后一页了，不可以再往后了！", 
        					Toast.LENGTH_SHORT
        				).show();
        				repaintAdFlag=true;//换完图片再重绘
        				return true;
        			}
		    		//如果还有内容要显示
		    		turnDir=TurnDir.right;             	   
	            	 //生成临时下一页的ReadRecord类的对象
	       			currRR_temp = new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
	       			currBook.put(Constant.nextPageNo ,currRR_temp); //将阅读记录加入到hashmap中
	           		//创建下一页的图片
	           		bmLeft=drawPage(currRR_temp);   
					Constant.nextPageNo=nextPageNo;//更新常量类Constant的值，用于下一次判断
	           		Constant.nextPageStart=nextPageStart;
	           		currRR = currRR_temp;//guningyi add 更新当前的阅读记录
		    	}
		    	else if(x< 0.2 * SCREEN_WIDTH  
		    			&& x > 0
		    			&& y < SCREEN_HEIGHT
		    			&& y > 0.8 * SCREEN_HEIGHT ){
		    		if(currRR.pageNo<=0){  //向前翻页的问题就出在这个地方 pageNo可能为0
						Toast.makeText
						(
							this.getContext(), 
							"已经到第一页，不可以再往前翻了！", 
							Toast.LENGTH_SHORT
						).show();	
						repaintAdFlag=true;//换完图片再重绘
						return true;
					}
                    turnDir=TurnDir.left;
            		Log.e("guningyi->","currRR.pageNo="+currRR.pageNo);	
            		currRR_temp=currBook.get(currRR.pageNo-1);
                    //从hashMap中获取上一页的记录
            		//包含开始位置和pageNo
            		bmLeft=drawPage(currRR_temp);  
					Constant.nextPageNo=nextPageNo;
            		Constant.nextPageStart=nextPageStart;
            		currRR = currRR_temp;//guningyi add
		    	}
		    	/*		    	if(x > 0  //弹出菜单
    			&& x<SCREEN_WIDTH
    			&& y > 0
    			&& y < SCREEN_HEIGHT)
    	{*/
    		//使用linearlayout来实现hiddenBar的方案
    		//readerActivity.goToHiddenBar();
            //guningyi add
    		//增加hiddenBar的响应代码
/*		    		if (readerActivity.hideLayout_flag == false){
    			if(readerActivity.hiddenBar != null)
    			{
    			    //readerActivity.hiddenBar.setVisibility(View.GONE);//隐藏linearlayout
    				//readerActivity.hiddenBar.setLayoutParams(new LinearLayout.LayoutParams(500, 40));
    			    readerActivity.hiddenBar.getBackground().setAlpha(0);//设置linearlayout为透明
    			    readerActivity.hideLayout_flag = true;
    			}
    			else
    			{
    				Toast.makeText
    				(
    					this.getContext(), 
    					"readerActivity.hiddenBar 等于空嘛！", 
    					Toast.LENGTH_SHORT
    				).show();
    			}
	        }*/
/*			        else
	        {
	        	readerActivity.hiddenBar.getBackground().setAlpha(100);//造成半透明效果
	        	//readerActivity.hiddenBar.setVisibility(View.VISIBLE);//显示linearlayout
	        	readerActivity.hideLayout_flag = false;
	        }
    		*/
    		
    		//使用bitmap来实现hiddenBar的方案
/*		    		if (show_hiddenBar_flag == false)
    		{
    			Log.e("guningyi->","show_hiddenBar_flag == false");
    			show_hiddenBar_flag = true;
    			showHiddenBarPage();
    		}
    		else
    		{
    			show_hiddenBar_flag = false;
    		}*/
    	 //}
		       	ax=x;
          	   	ay=y;     
          	   	break;
		        case MotionEvent.ACTION_UP:            	
                    turnDir=TurnDir.noTurning;                 
                    break;        	   	
		}
        this.repaint();
        repaintAdFlag=true;//换完图片再重绘
        return true;
	}
	
	/*public boolean old_onTouchEvent(MotionEvent e) 
	{  
		repaintAdFlag=false;
    	float x = e.getX();//获取触控点X坐标
        float y = e.getY();//获取触控点Y坐标    	
        
        switch (e.getAction()) 
        {
            case MotionEvent.ACTION_DOWN:
            	
            	 * 当按下时判断是要向后翻还是要向前翻，
            	 * 再初始化对应的b点的坐标值
            	 
      	
            	if(x>RIGHT_OR_LEFT_x )//如果按在右边，确定为要向后翻页
            	{
            		//初始化为右下角坐标
            		bx=SCREEN_WIDTH;
            		by=PAGE_HEIGHT+BLANK;
            	}
            	else//如果按在左边，确定为要向前翻页
            	{
            		//初始化为左下角坐标
            		bx=0;
            		by=PAGE_HEIGHT+BLANK;
            	}
            	//计算c、d两点坐标
            	//这里计算的CD得到的cd数组的坐标，并不是在点击动作时使用的。
            	//而是为产生撕页效果做准备
            	//因为无论是"点击"翻页还是"移动"翻页，第一个动作必定是点击屏幕，
            	//如果后续动作是抬起，则表明是"点击"翻页，后续动作是滑动，则表明是
            	//“移动”翻页
            	//所以如果用"点击"操作去实现翻页，cd坐标无论正确与否都不会影响翻页。
            	//向前翻页出现异常的原因是currRR.pageNo记录错误导致
            	cd=CalUtil.calCD(x, y, bx, by);
            	
            	//若初次按下的位置在右下角指定范围内则允许绘制翻页效果
            	//if(x>PAGE_WIDTH*1.7f&&x<SCREEN_WIDTH&&cd[0]>RIGHT_OR_LEFT_x)
            	//if(x<SCREEN_WIDTH&&cd[0]>RIGHT_OR_LEFT_x)
            	if(x < SCREEN_WIDTH && x > 0.5 *  SCREEN_WIDTH)
                {
            		//Log.e("guningyiConstant.nextPageStart","="+Constant.nextPageStart);
            		//Log.e("guningyiConstant.CONTENTCOUNT","="+Constant.CONTENTCOUNT);
            		if(Constant.nextPageStart>Constant.CONTENTCOUNT){
        				Toast.makeText
        				(
        					this.getContext(), 
        					"A已经到最后一页了，不可以再往后了！", 
        					Toast.LENGTH_SHORT
        				).show();
        				repaintAdFlag=true;//换完图片再重绘
        				return true;
        			}

            	turnDir=TurnDir.right;             	   
            	 //生成临时下一页的ReadRecord类的对象
            	Log.e("guningyi->","nextPageNo="+Constant.nextPageNo);
       			currRR_temp=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
       			//保护Constant.nextPageNo、Constant.nextPageNo两个值
           		int t1=Constant.nextPageNo;
           		int t2=Constant.nextPageStart;
           		//创建下一页的图片
           		Log.e("guningyissssssssssssssssssss","sssssssssssssssss");
           		//bmLeft_temp=drawPage(currRR_temp);
           		bmLeft=drawPage(currRR_temp);     			
				Constant.nextPageNo=t1;
           		Constant.nextPageStart=t2;        	
            	   
                }//若初次按下的位置在左下角指定范围内则允许绘制向前的翻页效果
            	//else if(x<PAGE_WIDTH*0.3&&cd[0]<PAGE_WIDTH)
            	else if (x< 0.5 * SCREEN_WIDTH  && x > 0)
                {
            		Log.e("guningyi->","currRR.pageNo="+currRR.pageNo);
            	   
            		Iterator<Integer> iter = currBook.keySet().iterator();
            		while (iter.hasNext()) {
            			Log.e("guningyi->","HashMap"+currBook.get(iter.next()).pageNo);
            		}
        			if(currRR.pageNo<=0){  //向前翻页的问题就出在这个地方 pageNo可能为0
						Toast.makeText
						(
							this.getContext(), 
							"A已经到第一页，不可以再往前翻了！", 
							Toast.LENGTH_SHORT
						).show();	
						repaintAdFlag=true;//换完图片再重绘
						return true;
					}
        			
            		turnDir=TurnDir.left;
            		
            		currRR_temp=currBook.get(currRR.pageNo-1);
                    //从hashMap中获取上一页的记录
            		//包含开始位置和pageNo
            		Log.e("guningyi->","currRR_temp.pageNo="+currRR_temp.pageNo);
            		Log.e("guningyi->","currRR_temp.start="+currRR_temp.start);
            		int t1=Constant.nextPageNo;
            		int t2=Constant.nextPageStart;
            		//currRR_temp.isLeft=true;    		
					//bmLeft_temp=drawPage(currRR_temp);
					//bmRight_temp=drawPage(currRR_temp);
            		bmLeft=drawPage(currRR_temp);  
					Constant.nextPageNo=t1;
            		Constant.nextPageStart=t2;	
                }
            	ax=x;
          	   	ay=y;          	 
            break;        
            case MotionEvent.ACTION_MOVE: 
            	//翻页时动态计算c、d两点坐标
            	cd=CalUtil.calCD(x, y, bx, by);
        		
            	
            	//若移动过程中没有撕纸则允许绘制翻页效果
                if(x>0&&x<SCREEN_WIDTH&&
             		   (turnDir==TurnDir.right&&cd[0]>PAGE_WIDTH)||//向后翻页时没有撕纸
             		   turnDir==TurnDir.left&&cd[0]<PAGE_WIDTH)//向前翻页时没有撕纸 
            	
                {
             	   ax=x;
             	   ay=y;            	   
                }
                else
                {
             	   turnDir=TurnDir.noTurning;
                }
              //若抬起的位置在左边指定范围则实施向后翻页并且下一页的索引值小于文章总字数
                Log.e("guningyiax","="+ax);
                Log.e("guningyiPAGE_WIDTH*0.1f","="+PAGE_WIDTH*0.1f);
                //if(turnDir==TurnDir.right && ax<PAGE_WIDTH*0.1f)	
                if(turnDir==TurnDir.right && ax<PAGE_WIDTH)	
                { 
                	currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
  				   	Constant.CURRENT_LEFT_START=currRR.start;//记录当前读到处leftstart的值
  				   	Constant.CURRENT_PAGE=currRR.pageNo;//记录当前读到处的page的值
  				   	
  				   	currBook.put(currRR.pageNo, currRR);//当前页的信息加入hashMap
			   
  				    Log.e("guningyi->","start draw page!!");
					//绘制下一幅图片
					bmLeft=drawPage(currRR);
					
					turnDir=TurnDir.noTurning;
                }
                //若抬起的位置在右边指定范围则实施向前翻页
                else if(turnDir==TurnDir.left && ax>PAGE_WIDTH*1.9f)	
                {
					currRR=currBook.get(currRR.pageNo-1);
					
					Constant.CURRENT_LEFT_START=currRR.start;//记录当前读到处leftstart的值
					Constant.CURRENT_PAGE=currRR.pageNo;//记录当前读到处的page的值
					
					//currRR.isLeft=true;
					bmLeft=drawPage(currRR);
					
					turnDir=TurnDir.noTurning;
                }
            break;
            case MotionEvent.ACTION_UP:            	
                turnDir=TurnDir.noTurning;                 
              break;              
        }  
        this.repaint();
        repaintAdFlag=true;//换完图片再重绘
        return true;
    }*/

	public void showHiddenBarPage()
	{
		hiddenBar = drawHiddenBarPage();
		this.repaint();
	}
	
	public Bitmap drawHiddenBarPage()
	{
		int hiddenBar_width = SCREEN_WIDTH;
		int hiddenBar_height = 30;
		
		Bitmap bm = Bitmap.createBitmap(hiddenBar_width, hiddenBar_height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);
		canvas.drawBitmap(bmBack,0,0, paint);
		try{
			synchronized(paint)
			{
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bm;
	}
	
	//guningyi编写新的drawPage函数
	public Bitmap drawPage(ReadRecord rr)
	{
		int start = rr.start;
		int extend_page_width = PAGE_WIDTH;
		int extend_page_height = PAGE_HEIGHT * 5;
		//Bitmap bm = Bitmap.createBitmap(PAGE_WIDTH, PAGE_HEIGHT,Bitmap.Config.ARGB_8888);
		Bitmap bm = Bitmap.createBitmap(extend_page_width, extend_page_height,Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(bm);
		canvas.drawBitmap(bmBack,0,0, paint);
		try
		{
			synchronized(paint)//锁定画笔
			{
				String str=null;
				paint.setColor(COLOR);
				paint.setTextSize(TEXT_SIZE);//设置字的大小
				if(Constant.FILE_PATH==null)
				{
					str=TextLoadUtil.loadFromSDFile(this,start,PAGE_LENGTH,Constant.DIRECTIONSNAME);//读取说明
					CONTENTCOUNT=TextLoadUtil.getCharacterCountApk(this, Constant.DIRECTIONSNAME);
				}else//否则读正文
				{
					str=TextLoadUtil.readFragment(start, PAGE_LENGTH, FILE_PATH);//读取正文
				}
				int index=0;
				int index2=0;//’\n'占两个字符
				char c=str.charAt(index);
				boolean finishFlag=false;		
				int currRow=0;
				int currX=0;
				while(!finishFlag)
				{
					if(c=='\n')  
					{//如果是换行 
						currRow++;
						currX=0;
						index2++;
					}
					else if((c<='z'&&c>='a')||(c<='Z'&&c>='A')||(c<='9'&&c>='0'))
					{//英文大小写或数字
						canvas.drawText(c+"", currX+TEXT_SIZE/2, currRow*TEXT_SIZE+TEXT_SIZE, paint);
						currX=currX+TEXT_SPACE_BETWEEN_EN;
					}
					else
					{//中文
						canvas.drawText(c+"", currX+TEXT_SIZE/2, currRow*TEXT_SIZE+TEXT_SIZE, paint);
						currX=currX+TEXT_SPACE_BETWEEN_CN;
					}
					index++;
					c=str.charAt(index);
					
					if(currX>=Constant.PAGE_WIDTH-TEXT_SIZE)
					{
						currRow=currRow+1;
						currX=0;
					}
					if(currRow==ROWS)
					{
						finishFlag=true;		
					    nextPageStart=rr.start+index+index2;
						nextPageNo=rr.pageNo+1;
					}
				}
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bm;
		
	}
	
	
  /* //绘制Bitmap的方法
   //253241#guningyi 重构
   //将其改成单页面显示
	public Bitmap old_drawPage(ReadRecord rr)
	{
		int start = rr.start;
		Log.e("guningyi->","rr.start="+rr.start);
		Log.e("guningyi->","rr.pageNo="+rr.pageNo);
		
		Bitmap bm=Bitmap.createBitmap(PAGE_WIDTH, PAGE_HEIGHT,Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(bm);
		
		canvas.drawBitmap(bmBack,0,0, paint);
		//canvas.drawBitmap(bmBack,0,0, paint);
		Log.e("guningyi->","start drawpage!");
		try
		{
			synchronized(paint)//锁定画笔
			{
				String str=null;
				paint.setColor(COLOR);
				paint.setTextSize(TEXT_SIZE);//设置字的大小
				//Log.e("guningyi->","synchronized");
				if(Constant.FILE_PATH==null)
				{
					str=TextLoadUtil.loadFromSDFile(this,start,PAGE_LENGTH,Constant.DIRECTIONSNAME);//读取说明
					CONTENTCOUNT=TextLoadUtil.getCharacterCountApk(this, Constant.DIRECTIONSNAME);
				}else//否则读正文
				{
					Log.e("guingyi->","read the text!");
					str=TextLoadUtil.readFragment(start, PAGE_LENGTH, FILE_PATH);//读取正文
				}
				int index=0;
				int index2=0;//’\n'占两个字符
				char c=str.charAt(index);
				boolean finishFlag=false;		
				int currRow=0;
				int currX=0;
				while(!finishFlag)
				{
					if(c=='\n')  
					{//如果是换行 
						currRow++;
						currX=0;
						index2++;
					}
					else if((c<='z'&&c>='a')||(c<='Z'&&c>='A')||(c<='9'&&c>='0'))
					{//英文大小写或数字
						canvas.drawText(c+"", currX+TEXT_SIZE/2, currRow*TEXT_SIZE+TEXT_SIZE, paint);
						currX=currX+TEXT_SPACE_BETWEEN_EN;
					}
					else
					{//中文
						canvas.drawText(c+"", currX+TEXT_SIZE/2, currRow*TEXT_SIZE+TEXT_SIZE, paint);
						currX=currX+TEXT_SPACE_BETWEEN_CN;
					}
					index++;
					c=str.charAt(index);
					
					if(currX>=Constant.PAGE_WIDTH-TEXT_SIZE)
					{
						currRow=currRow+1;
						currX=0;
					}
					if(currRow==ROWS)
					{
						Log.e("guningyi->","ROWS="+ROWS);
						finishFlag=true;		
					    nextPageStart=rr.start+index+index2;
					    Log.e("guningyi->","nextPageStart="+nextPageStart);
					    Log.e("guningyi->","CONTENTCOUNT="+CONTENTCOUNT);
						nextPageNo=rr.pageNo+1;
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return bm;
	}*/
	
	/*public Bitmap drawPage(ReadRecord rr)
	{
		int start=0;
		if(rr.isLeft)
		{
			start=rr.leftStart;
		}
		else
		{
			start=rr.rightStart;
		}
		
		Bitmap bm=Bitmap.createBitmap(PAGE_WIDTH, PAGE_HEIGHT,Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(bm);
		
		canvas.drawBitmap(bmBack,0,0, paint);
		canvas.drawBitmap(bmBack,0,0, paint);
		
		try
		{
			synchronized(paint)
			{
				String str=null;
				paint.setColor(COLOR);
				paint.setTextSize(TEXT_SIZE);//设置字的大小
				if(Constant.FILE_PATH==null)
				{
					str=TextLoadUtil.loadFromSDFile(this,start,PAGE_LENGTH,Constant.DIRECTIONSNAME);//读取说明
					CONTENTCOUNT=TextLoadUtil.getCharacterCountApk(this, Constant.DIRECTIONSNAME);
				}else//否则读正文
				{
					str=TextLoadUtil.readFragment(start, PAGE_LENGTH, FILE_PATH);//读取正文
				}
				int index=0;
				int index2=0;//’\n'占两个字符
				char c=str.charAt(index);
				boolean finishFlag=false;		
				int currRow=0;
				int currX=0;
				while(!finishFlag)
				{
					if(c=='\n')  
					{//如果是换行 
						currRow++;
						currX=0;
						index2++;
					}
					else if((c<='z'&&c>='a')||(c<='Z'&&c>='A')||(c<='9'&&c>='0'))
					{//英文大小写或数字
						canvas.drawText(c+"", currX+TEXT_SIZE/2, currRow*TEXT_SIZE+TEXT_SIZE, paint);
						currX=currX+TEXT_SPACE_BETWEEN_EN;
					}
					else
					{//中文
						canvas.drawText(c+"", currX+TEXT_SIZE/2, currRow*TEXT_SIZE+TEXT_SIZE, paint);
						currX=currX+TEXT_SPACE_BETWEEN_CN;
					}
					index++;
					c=str.charAt(index);
					
					if(currX>=Constant.PAGE_WIDTH-TEXT_SIZE)
					{
						currRow=currRow+1;
						currX=0;
					}
					if(currRow==ROWS)
					{
						finishFlag=true;
						if(rr.isLeft)
						{
							rr.isLeft=false;
							rr.rightStart=index+index2+rr.leftStart;
						}
						else
						{
							nextPageStart=rr.rightStart+index+index2;
							nextPageNo=rr.pageNo+1;
						}
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return bm;
	}*/
	

	   //绘制虚拟页Bitmap的方法
	public void drawVirtualPage(ReadRecord rr)
	{
		int start = rr.start;
		try
		{
			synchronized(paint)
			{
				String str=null;
				paint.setColor(COLOR);
				paint.setTextSize(TEXT_SIZE);//设置字的大小
				
				
				str=TextLoadUtil.readFragment(start, PAGE_LENGTH, FILE_PATH);//读取正文
				
				int index=0;
				int index2=0;//’\n'占两个字符
				char c=str.charAt(index);
				boolean finishFlag=false;		
				int currRow=0;
				int currX=0;
				while(!finishFlag)
				{
					if(c=='\n')  
					{//如果是换行 
						currRow++;
						currX=0;
						index2++;
					}
					else if((c<='z'&&c>='a')||(c<='Z'&&c>='A')||(c<='9'&&c>='0'))
					{//英文大小写或数字
						currX=currX+TEXT_SPACE_BETWEEN_EN;
					}
					else
					{//中文
						currX=currX+TEXT_SPACE_BETWEEN_CN;
					}
					index++;
					c=str.charAt(index);
					
					if(currX>=Constant.PAGE_WIDTH-TEXT_SIZE)
					{
						currRow=currRow+1;
						currX=0;
					}
					if(currRow==ROWS)
					{
						finishFlag=true;
						nextPageStart=rr.start+index+index2;
						nextPageNo=rr.pageNo+1;
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

	public void drawTitle(Canvas canvas)
	{
		try
		{
			synchronized(paint)
			{
				paint.setColor(Color.BLACK);
				paint.setTextSize(TITLE_SIZE);
				canvas.drawText("酷读阅读器", 0, TITLE_SIZE, paint);
				if(Constant.FILE_PATH==null)
				{
					canvas.drawText("说明", Constant.SCREEN_WIDTH-TITLE_SIZE, TITLE_SIZE, paint);//绘制“说明”
					
				}else//否则书写文章txt的名字
				{
					//将书名字大约画在中间位置
					canvas.drawText(Constant.TEXTNAME,Constant.SCREEN_WIDTH-3*TITLE_SIZE,TITLE_SIZE, paint);//后期需要调
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//绘制分隔线的方法
	/*
	public void drawcut_line(Canvas canvas)
	{
		try
		{
			synchronized(paint)
			{
				paint.setColor(Color.YELLOW);//绘制分割线
				canvas.drawRect(CENTER_LEFT_X, CENTER_LEFT_Y, CENTER_RIGHT_X, CENTER_RIGHT_Y, paint);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	*/
	//重新绘制的方法
    public void repaint()
	{
		Canvas canvas=this.getHolder().lockCanvas();
		try
		{
			synchronized(canvas)
			{
				onDraw(canvas);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(canvas!=null)
			{
				this.getHolder().unlockCanvasAndPost(canvas);
			}
		}
	}
    
}
