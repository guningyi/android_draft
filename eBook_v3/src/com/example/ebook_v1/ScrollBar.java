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
				float x = event.getX();
                float y = event.getY(); 
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
//在这个版本中不使用触发式按钮，直接在屏幕底部进行布局。
/*				diff = upTime - downTime;
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
		    	}*/
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
		content2 = "对于美国无业青年状况的报道经常见诸报端，但7月份关于就业市场健康状况的报告，却让我们对这个问题的规模有了新的认识。"+
		           "有经济学家认为，年轻人失业状况并没有人们描写的那么糟糕，因为许多在校生要么是自谋职业，要么就是还没有开始求职，因此不能算入劳动力当中。"+
				   "今年7月份的一份报告让我们更清晰地了解到如今年轻人所面临的问题——大多数年轻人都临时离开学校，开始求职。 经济大萧条期间，"+
		           "16至24岁既未上学、也没有全职工作的年轻人比例有所减少。与其他年龄段不同，这种减少的趋势并未加强：7月，36%的年轻人拥有全职工作，"+
				   "相比经济危机之前2007年同期减少了10%。当然，在七月份，年轻人纷纷开始暑期实习，因此统计数据中反映了从事带薪实习的年轻人，和从事无薪"+
		           "实习但有兼职工作的年轻人。进步政策研究所（Progressive Policy Institute）研究年轻人失业问题的经济学家戴安娜•卡鲁说：“他们没有上学，"+
				   "那他们到底在干什么？”她指出，7月份的就业报告显示，16至24岁未上学的无业者比例为17.1%，而六年前仅有11%。而且，个别年龄段的工人因为即将退休而"+
		           "不再被计入劳动力当中，而大量年轻人此时放弃求职的状况尤为令人不安：7月份，840万名16至24岁的年轻人停止求职，而一年前的这一数字只有680万。不论经济"+
				   "创造就业岗位的速度有多缓慢，这么多年轻人，尤其是没有上学的年轻人，生活依然如此艰难，这确实令人非常吃惊。7月份新增就业岗位主要集中在零售、酒店和酒吧。"+
		           "而这些自然不是薪酬最高的岗位，但它们所需要的技能更低，肯定能吸引那些受教育水平较低的年轻人。原因或许在于卡鲁所谓的“大挤压”，即由于缺乏要求中等技术水平的岗位，"+
				   "迫使许多工人接受薪酬更少、技术要求更低的工作，结果把教育水平和经验更少的年轻人挤出了就业市场。这种趋势产生了连锁效应。任何人失业都会非常困难，"+
		           "但对于刚刚起步的年轻人，一旦失业，他们的状况将格外艰难；无论从哪个方面来说，他们的状况或许最为严峻。调查显示，由于年轻人之前的经历，再加上错过了发展技能的机会，"+
				   "因此失业的年轻人在失业之后的许多年里，他们的收入也会相对较低。据美国进步中心（ Center for American Progress）四月份的一份报告估计，在经济衰退最严重的"+
		           "时期经历过长期失业的年轻人，未来十年的收入会减少超过200亿美元，相当于人均22,000美元。而这种情况会对美国经济造成深远的影响，我们在从住房到汽车销售的各行各业都看"+
				   "到过这一点。如果有人质疑经济增长速度为何跟不上就业增长速度，认真研究一下美国年轻人或许会有所帮助。（财富中文网）";
		text.setTextSize(22);
		text.setText(content2);
	}
	
}
