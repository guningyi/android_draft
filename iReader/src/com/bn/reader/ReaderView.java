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
	noTurning,left,right,//����ҳ����ǰ�������
}

public class ReaderView extends ScrollView{
	ReaderActivity readerActivity;//ReaderActivity������
	Paint paint;
	Bitmap bmLeft;
    ReadRecord currRR;//��ǰҳ����
	
	//��ҳ���õ�����ʱ����
	Bitmap bmLeft_temp;//���ͼƬ��ʱ����	
	ReadRecord currRR_temp;//��¼ReadRecord��һ����ʱ����
	Bitmap bmBack;// ����ͼƬ
	Bitmap title;// ��ͷͼƬ/

	//AdThread at;//�������ˢ���߳�
	//���ͼƬ����
	//int ad[]={R.drawable.ad_a,R.drawable.ad_b,R.drawable.ad_c,R.drawable.ad_d,
	//		R.drawable.ad_e,R.drawable.ad_f,R.drawable.ad_g,R.drawable.ad_h};
	//���صĹ��ͼƬ����
	//Bitmap adb[]=new Bitmap[ad.length];
	
	//Bitmap hiddenBar; //guningyi add ���ڻ���hiddenBarͼƬ
	//boolean show_hiddenBar_flag = false;
	
	//��ǰ����ı��ļ����˱��飩���Ķ�����
	HashMap<Integer,ReadRecord> currBook=new HashMap<Integer,ReadRecord>();
	
	//��ǰ��ҳ���ص�����
	float ax=-1;
	float ay=-1;	
	//���½�����
	int bx;
	int by;
	
	//int[] cd;//c��d������������,����c��d����ֱ�Ϊ��������ҳ���͸ߵĽ���
	TurnDir turnDir=TurnDir.noTurning;//��ҳ����ö������
	boolean repaintAdFlag=true;//���ƹ��ı�־
	//ReaderView�Ĺ��췽��
	public ReaderView(Context context)
	{
		spuer(context);
	}
	
	public ReaderView(ReaderActivity readerActivity) {
			super(readerActivity);  //���ø��෽��		
			this.readerActivity=readerActivity;  //��Ա������ֵ
			
			this.getHolder().addCallback((Callback) this);//�ص��ӿ�
			//��������
			paint=new Paint();		
		}
	
}

public class ReaderView_backup extends SurfaceView implements SurfaceHolder.Callback
{

	
	ReaderActivity readerActivity;//ReaderActivity������
	Paint paint;//���ʵ�����
	//��Ҫ���Ƶ���������ͼ������
	Bitmap bmLeft;//��ߵ�
	ReadRecord currRR;//��ǰҳ����
	
	//��ҳ���õ�����ʱ����
	Bitmap bmLeft_temp;//���ͼƬ��ʱ����	
	ReadRecord currRR_temp;//��¼ReadRecord��һ����ʱ����
	Bitmap bmBack;// ����ͼƬ
	Bitmap title;// ��ͷͼƬ

	AdThread at;//�������ˢ���߳�
	//���ͼƬ����
	int ad[]={R.drawable.ad_a,R.drawable.ad_b,R.drawable.ad_c,R.drawable.ad_d,
			R.drawable.ad_e,R.drawable.ad_f,R.drawable.ad_g,R.drawable.ad_h};
	//���صĹ��ͼƬ����
	Bitmap adb[]=new Bitmap[ad.length];
	
	Bitmap hiddenBar; //guningyi add ���ڻ���hiddenBarͼƬ
	boolean show_hiddenBar_flag = false;
	
	//��ǰ����ı��ļ����˱��飩���Ķ�����
	HashMap<Integer,ReadRecord> currBook=new HashMap<Integer,ReadRecord>();
	
	//��ǰ��ҳ���ص�����
	float ax=-1;
	float ay=-1;	
	//���½�����
	int bx;
	int by;
	
	int[] cd;//c��d������������,����c��d����ֱ�Ϊ��������ҳ���͸ߵĽ���
	TurnDir turnDir=TurnDir.noTurning;//��ҳ����ö������
	boolean repaintAdFlag=true;//���ƹ��ı�־
	//ReaderView�Ĺ��췽��
    public ReaderView(ReaderActivity readerActivity) {
		super(readerActivity);  //���ø��෽��		
		this.readerActivity=readerActivity;  //��Ա������ֵ
		
		this.getHolder().addCallback((Callback) this);//�ص��ӿ�
		//��������
		paint=new Paint();		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		at=new AdThread(this);//�������ˢ���߳�
		bmBack=PicLoadUtil.LoadBitmap(this.getResources(), BITMAP);//����Ӧ��Ļ�ı���ͼƬ
		bmBack=PicLoadUtil.scaleToFit(bmBack, PAGE_WIDTH, PAGE_HEIGHT);
		
		title=PicLoadUtil.LoadBitmap(this.getResources(), R.drawable.bt);//����Ӧ��Ļ�ı�ͷͼƬ
		title=PicLoadUtil.scaleToFit(title, SCREEN_WIDTH, BLANK);
		
		for(int i=0;i<ad.length;i++)//����Ӧ��Ļ�Ĺ��ͼƬ
		{
			adb[i]=PicLoadUtil.LoadBitmap(this.getResources(), ad[i]);
			adb[i]=PicLoadUtil.scaleToFit(adb[i], AD_WIDTH, BLANK);
		}
		//��ʼ������ǰ�ļ���Xҳ
		currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);
		
		if(CURRENT_PAGE==0)//����ǵ�һ�δ�ĳһ����
		{
			currBook.put(currRR.pageNo, currRR);//��һҳ����Ϣ����hashMap��			
		}
		
		//������������ͼƬ
		bmLeft=this.drawPage(currRR);
		//bmRight=this.drawPage(currRR); //253241#guningyi
		repaint();
		at.start();//�������ˢ���߳�
		

		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
    }

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		at.stopCurrentThread();//��ֹ����ˢ��
	}
	
	
	//ningyigu ��д�µ��Ķ�������ƺ���
	public void onDraw(Canvas canvas)
	{
		synchronized(paint)
		{
		    canvas.drawColor(Color.BLACK);//���ս���
		    canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, BLANK, paint);//��������Զ����ͼƬ
            //ʹ��bitmap��ʵ��hiddenBar�ķ���
		    /*		    
            if (show_hiddenBar_flag == true)
		    {
		    	int y = SCREEN_HEIGHT - BLANK;
		    	canvas.drawBitmap(hiddenBar, LEFT_OR_RIGHT_X, y, paint);//guningyi ����hiddenBar
		    }
		    else
		    {
		    	canvas.drawBitmap(hiddenBar, SCREEN_WIDTH, SCREEN_HEIGHT, paint);//guningyi ����hiddenBar
		    }*/
		    //canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, 0, paint);//����ͼƬ
			//�ָ�����״̬
			canvas.restore();	
		}
	}
	
	/*//�����Ķ�����ķ���
	public void onDraw(Canvas canvas)
	{
		synchronized(paint)//��������
		{	
			canvas.drawColor(Color.BLACK);//���ս���
			canvas.drawBitmap(title, 0, 0, paint);//���Ʊ�ͷͼƬ
			canvas.drawBitmap(adb[NUM],Constant.SCREEN_WIDTH-AD_WIDTH, 0, paint);//���ƹ����
			//drawcut_line(canvas);//���Ʒָ���  253241#guningyi
			drawTitle(canvas);//������Ŀ			
			if(turnDir==TurnDir.right)
			{	
				//�������ҳ����Ƶ�λ�ý���
				//�ԣ�0��30��Ϊ���Ͻ������ͼƬ��ʹ��Ψһ������ҳ��bmLeft
				canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, BLANK, paint);//��������Զ����ͼƬ
	
				//����a���λ�û����ұ���������ҳ====================begin===========================
				//���滭��״̬
				canvas.save();
				//�½�һ��·��
				Path path2=new Path();
				//��·��Ȧס�ķ�ΧΪe��f��c��d��g
				path2.moveTo(RIGHT_OR_LEFT_x,0);
				path2.lineTo(RIGHT_OR_LEFT_x, PAGE_HEIGHT+BLANK);			
				path2.lineTo(cd[0], cd[1]);
				path2.lineTo(cd[2], cd[3]);
				path2.lineTo(RIGHT_OR_LEFT_x+PAGE_WIDTH, BLANK);
				path2.lineTo(RIGHT_OR_LEFT_x,0);
				//Ϊ�������û��Ƽ���
				canvas.clipPath(path2);
				//����Ч����
				//��(right_or_left_x,30)Ϊ���Ͻ�����������ͼƬ
				//right_or_left_x��ֵΪ��Ļ���һ��+5
				//�����õ���ͼ��Ч������Ļ��һ��
				//canvas.drawBitmap(bmRight, RIGHT_OR_LEFT_x,BLANK, paint);//�����Ҳ�ͼƬ
				canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, BLANK, paint);// add by guningyi
				
				
				//�ָ�����״̬
				canvas.restore();
				//����a���λ�û����ұ���������ҳ=====================end============================
				
				
				//����a���λ�û��Ʊ��������ķ���ҳ����===============begin==========================
				//���㷴����ҳ�����ת������
				float angle=(float)Math.toDegrees(Math.atan((ay-cd[3])/(ax-cd[2])));
				//������������ת����
				Matrix m1=new Matrix();
				m1.setRotate(90+angle, LEFT_OR_RIGHT_X,PAGE_HEIGHT);
				//����������תҳ�����½ǶԵ����ص���
				Matrix m2=new Matrix();
				m2.setTranslate
				(
					ax,	
					ay-PAGE_HEIGHT
				);
				//�����ܾ���
				Matrix mz=new Matrix();
				mz.setConcat(m2, m1);
				//���滭��״̬
				canvas.save();
				//��·��Ȧס�ķ�ΧΪa��c��d
				Path path3=new Path();
				path3.moveTo(ax,ay);		
				path3.lineTo(cd[0], cd[1]);
				path3.lineTo(cd[2], cd[3]);		
				path3.lineTo(ax,ay);
				canvas.clipPath(path3);
							
	
				canvas.drawBitmap(bmLeft_temp, mz, paint);//��������Զ����ͼƬ
	
				//�ָ�����״̬
				canvas.restore();
				//����a���λ�û��Ʊ��������ķ���ҳ����================end===========================
				
				
				//����a���λ�û���������Ҫ����������ҳ�Ļ���================begin====================
				//���滭��״̬
				canvas.save();
				//�½�һ��·��
				Path path1=new Path();
				//��·��Ȧס�ķ�ΧΪc��b��d
				path1.moveTo(cd[0], cd[1]);
				path1.lineTo(bx, by);
				path1.lineTo(cd[2], cd[3]);
				path1.lineTo(cd[0], cd[1]);
				//Ϊ�������û��Ƽ���
				canvas.clipPath(path1);
				paint.setAlpha(220);
				
                //ʹ��canvas.drawBitmap��������ʾλͼ����bmRight_temp
				//������(RIGHT_OR_LEFT_x,BLANK)Ϊ���Ͻ����꣬ʹ�û���paint
				//canvas.drawBitmap(bmRight_temp, RIGHT_OR_LEFT_x,BLANK, paint);//�����Ҳ�ͼƬ
				paint.setAlpha(255);
				//�ָ�����״̬
				canvas.restore();	
				//����a���λ�û���������Ҫ����������ҳ�Ļ���=================end=====================			
	
			}//�������
			else if(turnDir==TurnDir.left)			
			{
				
				//����a���λ�û��������������ҳ====================begin===========================
				//���滭��״̬
				canvas.save();
				//�½�һ��·��
				Path path2=new Path();
				//��·��Ȧס�ķ�ΧΪe��f��c��d��g
				path2.moveTo(PAGE_WIDTH,0);
				path2.lineTo(PAGE_WIDTH, PAGE_HEIGHT+BLANK);			
				path2.lineTo(cd[0], cd[1]);
				path2.lineTo(cd[2], cd[3]);
				path2.lineTo(0, BLANK);	
				path2.lineTo(PAGE_WIDTH,0);	
				//Ϊ�������û��Ƽ���
				canvas.clipPath(path2);
				canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, BLANK, paint);//��������Զ����ͼƬ
				//�ָ�����״̬
				canvas.restore();
				//����a���λ�û��������������ҳ=====================end============================
				
				
				//�����ұ���������ҳ
				//ningyigu bmRight -> bmLeft
				//canvas.drawBitmap(bmLeft, RIGHT_OR_LEFT_x,BLANK, paint);
				
				//����a���λ�û���������Ҫ����������ҳ�Ļ���================begin====================
				//���滭��״̬
				canvas.save();
				//�½�һ��·��
				Path path1=new Path();
				//��·��Ȧס�ķ�ΧΪc��b��d
				path1.moveTo(cd[0], cd[1]);
				path1.lineTo(bx, by);
				path1.lineTo(cd[2], cd[3]);
				path1.lineTo(cd[0], cd[1]);
				//Ϊ�������û��Ƽ���
				canvas.clipPath(path1);
				paint.setAlpha(220);//�����Ľ�Ϊ��͸��
				
				canvas.drawBitmap(bmLeft_temp, LEFT_OR_RIGHT_X, BLANK, paint);//��������Զ����ͼƬ
			
				paint.setAlpha(255);
				//�ָ�����״̬
				canvas.restore();
				//����a���λ�û���������Ҫ����������ҳ�Ļ���=================end=====================
				
				
				//����a���λ�û��Ʊ��������ķ���ҳ����===============begin==========================
				//���㷴����ҳ�����ת������
				float angle=(float)Math.toDegrees(Math.atan((ax-cd[0])/(ay-cd[1])));//��ǰ��ҳʱ������Ƕ��õ�c������
				//������������ת����
				Matrix m1=new Matrix();
				m1.setRotate(-90-angle, PAGE_WIDTH ,PAGE_HEIGHT );//��ͼƬ���½�Ϊ��ת���ĵ㣬��ʱ����ת90+angle
				//����������תҳ�����½ǶԵ����ص���
				Matrix m2=new Matrix();
				m2.setTranslate
				(
					ax-PAGE_WIDTH ,	
					ay-PAGE_HEIGHT
				);
				//�����ܾ���
				Matrix mz=new Matrix();
				mz.setConcat(m2, m1);
				//���滭��״̬
				canvas.save();
				//��·��Ȧס�ķ�ΧΪa��c��d
				Path path3=new Path();
				path3.moveTo(ax,ay);		
				path3.lineTo(cd[0], cd[1]);
				path3.lineTo(cd[2], cd[3]);		
				path3.lineTo(ax,ay);
				canvas.clipPath(path3);
				
				//canvas.drawBitmap(bmRight_temp, mz, paint);
				//�ָ�����״̬
				canvas.restore();
				//����a���λ�û��Ʊ��������ķ���ҳ����================end===========================
			}
			else//���һ��ʼû�а�������࣬Ҳû�а������Ҳࡣԭ����ʵ���ǽ������������ڸ�Ϊһ����
			{
				canvas.drawBitmap(bmLeft, LEFT_OR_RIGHT_X, BLANK, paint);//��������Զ����ͼƬ
				//canvas.drawBitmap(bmRight, RIGHT_OR_LEFT_x,BLANK, paint);//�����Ҳ��Զ����ͼƬ
			}
		}
	}*/
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent e)
	{
		
		 switch(keyCode)
		 {
		    case 4:
		    	readerActivity.showDialog(readerActivity.EXIT_READER);//�˳��Ի���
				break;
		    case 22:
		    	repaintAdFlag=false;//���ƹ��ı�־��Ϊfalse
				//��ʼ������һҳ����
		    	currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
	
		    	Constant.CURRENT_LEFT_START=currRR.start;//��¼��ǰ������leftstart��ֵ
		    	Constant.CURRENT_PAGE=currRR.pageNo;//��¼��ǰ��������page��ֵ
	    	
		    	currBook.put(currRR.pageNo, currRR);//��ǰҳ����Ϣ����hashMap
		    	
		    	
		    	if(currRR.start>Constant.CONTENTCOUNT){
				   Toast.makeText
				   (
						this.getContext(), 
						"B�Ѿ������һҳ�ˣ��������������ˣ�", 
						Toast.LENGTH_SHORT
					).show();
				}else
				{
					//������������ͼƬ
					bmLeft=drawPage(currRR);
					//bmRight=drawPage(currRR);
					repaint();
				}
		    	repaintAdFlag=true;//����ͼƬ���ػ�
				break;
			    case 21:
			    	repaintAdFlag=false;//���ƹ��ı�־��Ϊfalse
				   if(currRR.pageNo==0){
						Toast.makeText
						(
							this.getContext(), 
							"B�Ѿ�����һҳ������������ǰ���ˣ�", 
							Toast.LENGTH_SHORT
						).show();				
					}
					else
					{
						currRR=currBook.get(currRR.pageNo-1);
						
						Constant.CURRENT_LEFT_START=currRR.start;//��¼��ǰ������leftstart��ֵ
						Constant.CURRENT_PAGE=currRR.pageNo;//��¼��ǰ��������page��ֵ

						//currRR.isLeft=true;
						bmLeft=drawPage(currRR);
						//bmRight=drawPage(currRR);
						repaint();
					}
				   repaintAdFlag=true;//����ͼƬ���ػ�
				   break;		    	
		    case 82:
		    	readerActivity.openOptionsMenu();
				   break; 
		 }
		   return true;
	}

	 
	//guningyi �Լ���д��onTouchEvent����
	public boolean onTouchEvent(MotionEvent e)
	{
		repaintAdFlag = false;
		float x = e.getX();
		float y = e.getY();
		
		switch(e.getAction())
		{
		    case MotionEvent.ACTION_DOWN:
		    	if(x > RIGHT_OR_LEFT_x){
		    		//��ʼ��Ϊ���½�����
            		bx=SCREEN_WIDTH;
            		by=PAGE_HEIGHT+BLANK;
		    	}
		    	else{
		    		//��ʼ��Ϊ���½�����
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
        					"�Ѿ������һҳ�ˣ��������������ˣ�", 
        					Toast.LENGTH_SHORT
        				).show();
        				repaintAdFlag=true;//����ͼƬ���ػ�
        				return true;
        			}
		    		//�����������Ҫ��ʾ
		    		turnDir=TurnDir.right;             	   
	            	 //������ʱ��һҳ��ReadRecord��Ķ���
	       			currRR_temp = new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
	       			currBook.put(Constant.nextPageNo ,currRR_temp); //���Ķ���¼���뵽hashmap��
	           		//������һҳ��ͼƬ
	           		bmLeft=drawPage(currRR_temp);   
					Constant.nextPageNo=nextPageNo;//���³�����Constant��ֵ��������һ���ж�
	           		Constant.nextPageStart=nextPageStart;
	           		currRR = currRR_temp;//guningyi add ���µ�ǰ���Ķ���¼
		    	}
		    	else if(x< 0.2 * SCREEN_WIDTH  
		    			&& x > 0
		    			&& y < SCREEN_HEIGHT
		    			&& y > 0.8 * SCREEN_HEIGHT ){
		    		if(currRR.pageNo<=0){  //��ǰ��ҳ������ͳ�������ط� pageNo����Ϊ0
						Toast.makeText
						(
							this.getContext(), 
							"�Ѿ�����һҳ������������ǰ���ˣ�", 
							Toast.LENGTH_SHORT
						).show();	
						repaintAdFlag=true;//����ͼƬ���ػ�
						return true;
					}
                    turnDir=TurnDir.left;
            		Log.e("guningyi->","currRR.pageNo="+currRR.pageNo);	
            		currRR_temp=currBook.get(currRR.pageNo-1);
                    //��hashMap�л�ȡ��һҳ�ļ�¼
            		//������ʼλ�ú�pageNo
            		bmLeft=drawPage(currRR_temp);  
					Constant.nextPageNo=nextPageNo;
            		Constant.nextPageStart=nextPageStart;
            		currRR = currRR_temp;//guningyi add
		    	}
		    	/*		    	if(x > 0  //�����˵�
    			&& x<SCREEN_WIDTH
    			&& y > 0
    			&& y < SCREEN_HEIGHT)
    	{*/
    		//ʹ��linearlayout��ʵ��hiddenBar�ķ���
    		//readerActivity.goToHiddenBar();
            //guningyi add
    		//����hiddenBar����Ӧ����
/*		    		if (readerActivity.hideLayout_flag == false){
    			if(readerActivity.hiddenBar != null)
    			{
    			    //readerActivity.hiddenBar.setVisibility(View.GONE);//����linearlayout
    				//readerActivity.hiddenBar.setLayoutParams(new LinearLayout.LayoutParams(500, 40));
    			    readerActivity.hiddenBar.getBackground().setAlpha(0);//����linearlayoutΪ͸��
    			    readerActivity.hideLayout_flag = true;
    			}
    			else
    			{
    				Toast.makeText
    				(
    					this.getContext(), 
    					"readerActivity.hiddenBar ���ڿ��", 
    					Toast.LENGTH_SHORT
    				).show();
    			}
	        }*/
/*			        else
	        {
	        	readerActivity.hiddenBar.getBackground().setAlpha(100);//��ɰ�͸��Ч��
	        	//readerActivity.hiddenBar.setVisibility(View.VISIBLE);//��ʾlinearlayout
	        	readerActivity.hideLayout_flag = false;
	        }
    		*/
    		
    		//ʹ��bitmap��ʵ��hiddenBar�ķ���
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
        repaintAdFlag=true;//����ͼƬ���ػ�
        return true;
	}
	
	/*public boolean old_onTouchEvent(MotionEvent e) 
	{  
		repaintAdFlag=false;
    	float x = e.getX();//��ȡ���ص�X����
        float y = e.getY();//��ȡ���ص�Y����    	
        
        switch (e.getAction()) 
        {
            case MotionEvent.ACTION_DOWN:
            	
            	 * ������ʱ�ж���Ҫ��󷭻���Ҫ��ǰ����
            	 * �ٳ�ʼ����Ӧ��b�������ֵ
            	 
      	
            	if(x>RIGHT_OR_LEFT_x )//��������ұߣ�ȷ��ΪҪ���ҳ
            	{
            		//��ʼ��Ϊ���½�����
            		bx=SCREEN_WIDTH;
            		by=PAGE_HEIGHT+BLANK;
            	}
            	else//���������ߣ�ȷ��ΪҪ��ǰ��ҳ
            	{
            		//��ʼ��Ϊ���½�����
            		bx=0;
            		by=PAGE_HEIGHT+BLANK;
            	}
            	//����c��d��������
            	//��������CD�õ���cd��������꣬�������ڵ������ʱʹ�õġ�
            	//����Ϊ����˺ҳЧ����׼��
            	//��Ϊ������"���"��ҳ����"�ƶ�"��ҳ����һ�������ض��ǵ����Ļ��
            	//�������������̧���������"���"��ҳ�����������ǻ������������
            	//���ƶ�����ҳ
            	//���������"���"����ȥʵ�ַ�ҳ��cd����������ȷ��񶼲���Ӱ�췭ҳ��
            	//��ǰ��ҳ�����쳣��ԭ����currRR.pageNo��¼������
            	cd=CalUtil.calCD(x, y, bx, by);
            	
            	//�����ΰ��µ�λ�������½�ָ����Χ����������Ʒ�ҳЧ��
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
        					"A�Ѿ������һҳ�ˣ��������������ˣ�", 
        					Toast.LENGTH_SHORT
        				).show();
        				repaintAdFlag=true;//����ͼƬ���ػ�
        				return true;
        			}

            	turnDir=TurnDir.right;             	   
            	 //������ʱ��һҳ��ReadRecord��Ķ���
            	Log.e("guningyi->","nextPageNo="+Constant.nextPageNo);
       			currRR_temp=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
       			//����Constant.nextPageNo��Constant.nextPageNo����ֵ
           		int t1=Constant.nextPageNo;
           		int t2=Constant.nextPageStart;
           		//������һҳ��ͼƬ
           		Log.e("guningyissssssssssssssssssss","sssssssssssssssss");
           		//bmLeft_temp=drawPage(currRR_temp);
           		bmLeft=drawPage(currRR_temp);     			
				Constant.nextPageNo=t1;
           		Constant.nextPageStart=t2;        	
            	   
                }//�����ΰ��µ�λ�������½�ָ����Χ�������������ǰ�ķ�ҳЧ��
            	//else if(x<PAGE_WIDTH*0.3&&cd[0]<PAGE_WIDTH)
            	else if (x< 0.5 * SCREEN_WIDTH  && x > 0)
                {
            		Log.e("guningyi->","currRR.pageNo="+currRR.pageNo);
            	   
            		Iterator<Integer> iter = currBook.keySet().iterator();
            		while (iter.hasNext()) {
            			Log.e("guningyi->","HashMap"+currBook.get(iter.next()).pageNo);
            		}
        			if(currRR.pageNo<=0){  //��ǰ��ҳ������ͳ�������ط� pageNo����Ϊ0
						Toast.makeText
						(
							this.getContext(), 
							"A�Ѿ�����һҳ������������ǰ���ˣ�", 
							Toast.LENGTH_SHORT
						).show();	
						repaintAdFlag=true;//����ͼƬ���ػ�
						return true;
					}
        			
            		turnDir=TurnDir.left;
            		
            		currRR_temp=currBook.get(currRR.pageNo-1);
                    //��hashMap�л�ȡ��һҳ�ļ�¼
            		//������ʼλ�ú�pageNo
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
            	//��ҳʱ��̬����c��d��������
            	cd=CalUtil.calCD(x, y, bx, by);
        		
            	
            	//���ƶ�������û��˺ֽ��������Ʒ�ҳЧ��
                if(x>0&&x<SCREEN_WIDTH&&
             		   (turnDir==TurnDir.right&&cd[0]>PAGE_WIDTH)||//���ҳʱû��˺ֽ
             		   turnDir==TurnDir.left&&cd[0]<PAGE_WIDTH)//��ǰ��ҳʱû��˺ֽ 
            	
                {
             	   ax=x;
             	   ay=y;            	   
                }
                else
                {
             	   turnDir=TurnDir.noTurning;
                }
              //��̧���λ�������ָ����Χ��ʵʩ���ҳ������һҳ������ֵС������������
                Log.e("guningyiax","="+ax);
                Log.e("guningyiPAGE_WIDTH*0.1f","="+PAGE_WIDTH*0.1f);
                //if(turnDir==TurnDir.right && ax<PAGE_WIDTH*0.1f)	
                if(turnDir==TurnDir.right && ax<PAGE_WIDTH)	
                { 
                	currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
  				   	Constant.CURRENT_LEFT_START=currRR.start;//��¼��ǰ������leftstart��ֵ
  				   	Constant.CURRENT_PAGE=currRR.pageNo;//��¼��ǰ��������page��ֵ
  				   	
  				   	currBook.put(currRR.pageNo, currRR);//��ǰҳ����Ϣ����hashMap
			   
  				    Log.e("guningyi->","start draw page!!");
					//������һ��ͼƬ
					bmLeft=drawPage(currRR);
					
					turnDir=TurnDir.noTurning;
                }
                //��̧���λ�����ұ�ָ����Χ��ʵʩ��ǰ��ҳ
                else if(turnDir==TurnDir.left && ax>PAGE_WIDTH*1.9f)	
                {
					currRR=currBook.get(currRR.pageNo-1);
					
					Constant.CURRENT_LEFT_START=currRR.start;//��¼��ǰ������leftstart��ֵ
					Constant.CURRENT_PAGE=currRR.pageNo;//��¼��ǰ��������page��ֵ
					
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
        repaintAdFlag=true;//����ͼƬ���ػ�
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
	
	//guningyi��д�µ�drawPage����
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
			synchronized(paint)//��������
			{
				String str=null;
				paint.setColor(COLOR);
				paint.setTextSize(TEXT_SIZE);//�����ֵĴ�С
				if(Constant.FILE_PATH==null)
				{
					str=TextLoadUtil.loadFromSDFile(this,start,PAGE_LENGTH,Constant.DIRECTIONSNAME);//��ȡ˵��
					CONTENTCOUNT=TextLoadUtil.getCharacterCountApk(this, Constant.DIRECTIONSNAME);
				}else//���������
				{
					str=TextLoadUtil.readFragment(start, PAGE_LENGTH, FILE_PATH);//��ȡ����
				}
				int index=0;
				int index2=0;//��\n'ռ�����ַ�
				char c=str.charAt(index);
				boolean finishFlag=false;		
				int currRow=0;
				int currX=0;
				while(!finishFlag)
				{
					if(c=='\n')  
					{//����ǻ��� 
						currRow++;
						currX=0;
						index2++;
					}
					else if((c<='z'&&c>='a')||(c<='Z'&&c>='A')||(c<='9'&&c>='0'))
					{//Ӣ�Ĵ�Сд������
						canvas.drawText(c+"", currX+TEXT_SIZE/2, currRow*TEXT_SIZE+TEXT_SIZE, paint);
						currX=currX+TEXT_SPACE_BETWEEN_EN;
					}
					else
					{//����
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
	
	
  /* //����Bitmap�ķ���
   //253241#guningyi �ع�
   //����ĳɵ�ҳ����ʾ
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
			synchronized(paint)//��������
			{
				String str=null;
				paint.setColor(COLOR);
				paint.setTextSize(TEXT_SIZE);//�����ֵĴ�С
				//Log.e("guningyi->","synchronized");
				if(Constant.FILE_PATH==null)
				{
					str=TextLoadUtil.loadFromSDFile(this,start,PAGE_LENGTH,Constant.DIRECTIONSNAME);//��ȡ˵��
					CONTENTCOUNT=TextLoadUtil.getCharacterCountApk(this, Constant.DIRECTIONSNAME);
				}else//���������
				{
					Log.e("guingyi->","read the text!");
					str=TextLoadUtil.readFragment(start, PAGE_LENGTH, FILE_PATH);//��ȡ����
				}
				int index=0;
				int index2=0;//��\n'ռ�����ַ�
				char c=str.charAt(index);
				boolean finishFlag=false;		
				int currRow=0;
				int currX=0;
				while(!finishFlag)
				{
					if(c=='\n')  
					{//����ǻ��� 
						currRow++;
						currX=0;
						index2++;
					}
					else if((c<='z'&&c>='a')||(c<='Z'&&c>='A')||(c<='9'&&c>='0'))
					{//Ӣ�Ĵ�Сд������
						canvas.drawText(c+"", currX+TEXT_SIZE/2, currRow*TEXT_SIZE+TEXT_SIZE, paint);
						currX=currX+TEXT_SPACE_BETWEEN_EN;
					}
					else
					{//����
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
				paint.setTextSize(TEXT_SIZE);//�����ֵĴ�С
				if(Constant.FILE_PATH==null)
				{
					str=TextLoadUtil.loadFromSDFile(this,start,PAGE_LENGTH,Constant.DIRECTIONSNAME);//��ȡ˵��
					CONTENTCOUNT=TextLoadUtil.getCharacterCountApk(this, Constant.DIRECTIONSNAME);
				}else//���������
				{
					str=TextLoadUtil.readFragment(start, PAGE_LENGTH, FILE_PATH);//��ȡ����
				}
				int index=0;
				int index2=0;//��\n'ռ�����ַ�
				char c=str.charAt(index);
				boolean finishFlag=false;		
				int currRow=0;
				int currX=0;
				while(!finishFlag)
				{
					if(c=='\n')  
					{//����ǻ��� 
						currRow++;
						currX=0;
						index2++;
					}
					else if((c<='z'&&c>='a')||(c<='Z'&&c>='A')||(c<='9'&&c>='0'))
					{//Ӣ�Ĵ�Сд������
						canvas.drawText(c+"", currX+TEXT_SIZE/2, currRow*TEXT_SIZE+TEXT_SIZE, paint);
						currX=currX+TEXT_SPACE_BETWEEN_EN;
					}
					else
					{//����
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
	

	   //��������ҳBitmap�ķ���
	public void drawVirtualPage(ReadRecord rr)
	{
		int start = rr.start;
		try
		{
			synchronized(paint)
			{
				String str=null;
				paint.setColor(COLOR);
				paint.setTextSize(TEXT_SIZE);//�����ֵĴ�С
				
				
				str=TextLoadUtil.readFragment(start, PAGE_LENGTH, FILE_PATH);//��ȡ����
				
				int index=0;
				int index2=0;//��\n'ռ�����ַ�
				char c=str.charAt(index);
				boolean finishFlag=false;		
				int currRow=0;
				int currX=0;
				while(!finishFlag)
				{
					if(c=='\n')  
					{//����ǻ��� 
						currRow++;
						currX=0;
						index2++;
					}
					else if((c<='z'&&c>='a')||(c<='Z'&&c>='A')||(c<='9'&&c>='0'))
					{//Ӣ�Ĵ�Сд������
						currX=currX+TEXT_SPACE_BETWEEN_EN;
					}
					else
					{//����
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
				canvas.drawText("����Ķ���", 0, TITLE_SIZE, paint);
				if(Constant.FILE_PATH==null)
				{
					canvas.drawText("˵��", Constant.SCREEN_WIDTH-TITLE_SIZE, TITLE_SIZE, paint);//���ơ�˵����
					
				}else//������д����txt������
				{
					//�������ִ�Լ�����м�λ��
					canvas.drawText(Constant.TEXTNAME,Constant.SCREEN_WIDTH-3*TITLE_SIZE,TITLE_SIZE, paint);//������Ҫ��
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//���Ʒָ��ߵķ���
	/*
	public void drawcut_line(Canvas canvas)
	{
		try
		{
			synchronized(paint)
			{
				paint.setColor(Color.YELLOW);//���Ʒָ���
				canvas.drawRect(CENTER_LEFT_X, CENTER_LEFT_Y, CENTER_RIGHT_X, CENTER_RIGHT_Y, paint);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	*/
	//���»��Ƶķ���
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
