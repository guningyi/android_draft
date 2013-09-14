package com.example.ebook_v1;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScrollBar extends Activity implements
OnClickListener,OnLongClickListener,OnTouchListener,OnCheckedChangeListener
,Button.OnClickListener{

	//text正文相关变量
	private TextView text = null;
	private String content1 = null; 
	private String content2 = null; 
	private String content3 = null; 
	
	//navigator相关变量
	private LinearLayout navigator = null;
	//private LayoutParams params = null;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private int left = 0;
	private int right = 0;
	private int top = 0;
	private int buttom = 0;
	private boolean navigatorFlag = false;
	
	//chapterInfo相关变量
	private TextView chapterInfo = null;
	private int chapterInfoHeight = 0;
	
	//Button相关变量
	private Button prevChapter = null;
	private Button nextChapter = null;
	
	@SuppressWarnings({ "deprecation", "deprecation", "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroll_bar);
		screenWidth  = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight(); 
		content1 = "nvestors are worried about India's large current account deficit, which reflects the nation's tendency to import many more goods than it exports and leaves it heavily reliant on foreign capital." +
				"Talk of tighter U.S. monetary policy has seen some investors pull out of emerging markets in recent months." +
				"Prime Minister Manmohan Singh has tried to calm nerves, saying the government has enough foreign reserves to defend the rupee for months."+
"               There is no question of going back to the 1991 [balance of payment crisis], Singh told the Press Times of India, referring to an episode that nearly " +
"               resulted in India defaulting on its debt payments.ut with elevated inflation, a sky-high government deficit and the economy slowing, some are worried that" +
"               recent government attempts to shore up confidence may have had the opposite effect.Related story: BRIC markets left in the dust Policymakers last week " +
"               unveiled a series of measures designed to support the rupee, including limits on the import of gold, oil and other key commodities.he government also made a" +
"               controversial move to restrict the amount of money Indian citizens can take out of the country, and similar restraints were placed on outgoing corporate investment.";
		text = (TextView)findViewById(R.id.text);
		text.setTextSize(22);
		text.setText(content1);
		
		chapterInfoHeight = 22;
		chapterInfo = (TextView)findViewById(R.id.chapterInfo);
		chapterInfo.setTextSize(chapterInfoHeight);
		chapterInfo.setBackgroundColor(Color.argb(255, 0, 255, 0));
		chapterInfo.setText("1.First Chapter, CNN news");
		
		navigator = (LinearLayout)findViewById(R.id.navigator);
		//动态的获取设备的screen的高和宽，确保虽然是滚动屏幕，但是底部导航栏始终是显示在
		//屏幕的可视范围的底端而不会被遮盖掉
		right = left = (int)(screenWidth *0.1);
		buttom = (int) (screenHeight* 0.0001);
		top = (int) (screenHeight * 0.7);
		navigator.setPadding(left,top,right,buttom);	
		
		text.setOnTouchListener(this);//设置触摸屏监听器
		
		prevChapter = (Button)findViewById(R.id.prevChapter);
		nextChapter = (Button)findViewById(R.id.nextChapter);
		
		
		//设置按钮监听事件	
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
		//获得触摸的坐标
		//float x = event.getX();
		//float y = event.getY(); 
		switch (event.getAction()) 
		{
		    //触摸屏幕时刻
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
		        //触摸并移动时刻
		        case MotionEvent.ACTION_MOVE:

		        break;
		        //终止触摸时刻
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
		//获得触摸的坐标
				//float x = event.getX();
//				/float y = event.getY(); 
				switch (event.getAction()) 
				{
				    //触摸屏幕时刻
				    case MotionEvent.ACTION_DOWN:
				    	downTime = event.getDownTime();
				        break;
				        //触摸并移动时刻
				        case MotionEvent.ACTION_MOVE:

				        break;
				        //终止触摸时刻
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
		content2 = "xxxxxxxxxxnvestors are worried about India's large current account deficit, which reflects the nation's tendency to import many more goods than it exports and leaves it heavily reliant on foreign capital." +
				"Talk of tighter U.S. monetary policy has seen some investors pull out of emerging markets in recent months." +
				"Prime Minister Manmohan Singh has tried to calm nerves, saying the government has enough foreign reserves to defend the rupee for months."+
"               There is no question of going back to the 1991 [balance of payment crisis], Singh told the Press Times of India, referring to an episode that nearly " +
"               resulted in India defaulting on its debt payments.ut with elevated inflation, a sky-high government deficit and the economy slowing, some are worried that" +
"               recent government attempts to shore up confidence may have had the opposite effect.Related story: BRIC markets left in the dust Policymakers last week " +
"               unveiled a series of measures designed to support the rupee, including limits on the import of gold, oil and other key commodities.he government also made a" +
"               controversial move to restrict the amount of money Indian citizens can take out of the country, and similar restraints were placed on outgoing corporate investment.";
		text.setTextSize(22);
		text.setText(content2);
	}
	
}
