package com.example.ebook_v1;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScrollBar extends Activity implements
OnClickListener,OnLongClickListener,OnTouchListener,OnCheckedChangeListener
,Button.OnClickListener{

	//·����ر���
	private String SDPATH;
	private String filePath = null;
	
	//ͼ����ر���
	private ImageView image = null;
	private Bitmap bm1;
	//navigator��ر���
	private LinearLayout navigator = null;
	//private LayoutParams params = null;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private int left = 0;
	private int right = 0;
	private int top = 0;
	private int buttom = 0;
	private boolean navigatorFlag = false;
	
	//chapterInfo��ر���
	private TextView chapterInfo = null;
	private int chapterInfoHeight = 0;
	
	//Button��ر���
	private Button prevChapter = null;
	private Button nextChapter = null;
	
	@SuppressWarnings({ "deprecation", "deprecation", "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroll_bar);
		screenWidth  = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight(); 
	
		image = (ImageView)findViewById(R.id.image);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		SDPATH = Environment.getExternalStorageDirectory().getPath();
		Log.e("SDPATH","="+SDPATH);
		filePath = SDPATH+"/test.jpg";
		File file = new File(filePath);
		Log.e("filePath","="+filePath);
		if (file.exists())
		{   
			Log.e("file exists!","!!");
		    bm1 = BitmapFactory.decodeFile(filePath);
		    image.setImageBitmap(bm1);
		}
		

		chapterInfoHeight = 22;
		chapterInfo = (TextView)findViewById(R.id.chapterInfo);
		chapterInfo.setTextSize(chapterInfoHeight);
		chapterInfo.setBackgroundColor(Color.argb(255, 0, 255, 0));
		chapterInfo.setText("1.First Chapter, CNN news");
		
		navigator = (LinearLayout)findViewById(R.id.navigator);
		//��̬�Ļ�ȡ�豸��screen�ĸߺͿ���ȷ����Ȼ�ǹ�����Ļ�����ǵײ�������ʼ������ʾ��
		//��Ļ�Ŀ��ӷ�Χ�ĵ׶˶����ᱻ�ڸǵ�
		right = left = (int)(screenWidth *0.1);
		buttom = (int) (screenHeight* 0.0001);
		top = (int) (screenHeight * 0.7);
		navigator.setPadding(left,top,right,buttom);	
		
		//text.setOnTouchListener(this);//���ô�����������
		
		prevChapter = (Button)findViewById(R.id.prevChapter);
		nextChapter = (Button)findViewById(R.id.nextChapter);
		
		
		//���ð�ť�����¼�	
		//prevChapter.setOnClickListener((android.view.View.OnClickListener) this);
		prevChapter.setOnClickListener(this);
		nextChapter.setOnClickListener(this);
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scroll_bar, menu);
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		//��ô���������
		//float x = event.getX();
		//float y = event.getY(); 
		switch (event.getAction()) 
		{
		    //������Ļʱ��
		    case MotionEvent.ACTION_DOWN:
		    	if (false == navigatorFlag)
		    	{
		    		navigator.setVisibility(View.GONE);
		    		navigatorFlag = true;
		    	}
		    	else
		    	{
		    		navigator.setVisibility(View.VISIBLE);
		    		navigatorFlag = false;
		    	}
		        break;
		        //�������ƶ�ʱ��
		        case MotionEvent.ACTION_MOVE:

		        break;
		        //��ֹ����ʱ��
		        case MotionEvent.ACTION_UP:
		        break;
		}
		return true;
		}
	  public void showTheNavigator()
	  {
		    if (false == navigatorFlag)
		    {
		    	
		    }
		    else
		    {
		    	
		    }
		    return;
	  }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float downTime = (float) 0.0;
		float upTime = (float) 0.0;
		float diff = (float) 0.0;
		
		// TODO Auto-generated method stub
		//��ô���������
				//float x = event.getX();
//				/float y = event.getY(); 
				switch (event.getAction()) 
				{
				    //������Ļʱ��
				    case MotionEvent.ACTION_DOWN:
				    	downTime = event.getDownTime();
				        break;
				        //�������ƶ�ʱ��
				        case MotionEvent.ACTION_MOVE:

				        break;
				        //��ֹ����ʱ��
				        case MotionEvent.ACTION_UP:
				        	upTime = event.getEventTime();
				        break;
				}
				diff = upTime - downTime;
				Log.e("upTime","="+upTime);
				Log.e("downTime","="+downTime);
				Log.e("diff","="+diff);
				
				if (false == navigatorFlag && diff < 0.0000001)
		    	{
		    		navigator.setVisibility(View.GONE);
		    		navigatorFlag = true;
		    	}
		    	else if (true == navigatorFlag && diff < 0.0000001)
		    	{
		    		navigator.setVisibility(View.VISIBLE);
		    		navigatorFlag = false;
		    	}
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
	
}