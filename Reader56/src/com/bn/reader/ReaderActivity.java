package com.bn.reader;//打包

//相关类的引入
import static com.bn.reader.Constant.BITMAP;
import static com.bn.reader.Constant.CURRENT_LEFT_START;
import static com.bn.reader.Constant.CURRENT_PAGE;
import static com.bn.reader.Constant.PAGE_HEIGHT;
import static com.bn.reader.Constant.PAGE_WIDTH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

//标识所有SurfaceView的常量类
class WhatMessage{
	public static final int GOTO_WELLCOME_VIEW=0;
	public static final int GOTO_MAIN_MENU_VIEW=1;
	public static final int GOTO_SEARCHBOOK_LIST=2;
	public static final int GOTO_BACHGROUND_LIST=3;
}
//表示各个界面的安全类型枚举
enum WhichView {WELLCOM_VIEW,MAIN_VIEW,SEARCHBOOK_LIST,BACKGROUND_LIST }

public class ReaderActivity extends Activity {
	//背景图
	int[] drawableIds=
	{R.drawable.bg_fhzl,R.drawable.bg_lsct,R.drawable.bg_sjzx,R.drawable.bg_ssnh,R.drawable.bg_wffm,R.drawable.bg_ygmm};
	//背景图描述
	int[] msgIds={R.string.fhzl,R.string.lsct,R.string.sjzx,R.string.ssnh,R.string.wffm,R.string.ygmm};
	//文字颜色
	int[] drawColorIds={R.drawable.tc_black,R.drawable.tc_blue,R.drawable.tc_gray,R.drawable.tc_green,
			R.drawable.tc_purper,R.drawable.tc_red,R.drawable.tc_yellow,R.drawable.tc_white};
	//文字颜色描述
	int[] msgIds2={R.string.tc_black,R.string.tc_blue,R.string.tc_gray,R.string.tc_green,
			R.string.tc_purper,R.string.tc_red,R.string.tc_yellow,R.string.tc_white};
	//字体大小描述
	int[] fontSizeIds={R.string.font_size16,R.string.font_size24,R.string.font_size32};
	//字体图片
	int[] fontSizeDrawable={R.drawable.font_size3,R.drawable.font_size2,R.drawable.font_size1};
	
	//音乐名称
	int[] musicname={R.string.music_gs,R.string.music_mh};
	
	String sdcardPath="/sdcard";//根目录
	String leavePath;//子文件路径
	ListView lv;//列表的引用
	Button root_b;//返回根目录的按钮
	Button back_b;//返回到上层目录
	
	//dialog编号
	final int LIST_SEARCH=1;//找书按钮
	final int LIST_TURNPAGE=2;//自动翻书按钮
	final int LIST_SET=3;//设置按钮
	final int LIST_BOOKMARK=4;//书签按钮
	
	final int NAME_INPUT_DIALOG_ID=5;//输入书签名字的对话框
	final int SELECT_BOOKMARK=6;//选择书签的对话框	
	final int EXIT_READER=7;//退出软件对话框
	final int DELETE_ONE_BOOKMARK=8;//删除一条记录的对话框
	final int DELETE_ALL_BOOKMARK=9;//清空当前书的所有书签
	
	final int SET_FONT_SIZE	=10;//设置字体大小对话框
	final int SET_FONT_COLOR=11;//设置字体颜色对话框
	
	final int BACKGROUND_PIC=12;//背景图片
	final int BACKGROUND_MUSIC=13;//背景音乐对话框
	
	public boolean hideLayout_flag = false;//guningyi add，用于判断按钮的显示
	public Button nextChapter = null;
	public Button prevChapter = null;
	public LinearLayout hiddenBar = null;

	DownLoad dl;
	WhichView curr;
	ReaderView readerView;//ReaderView的引用
	WellcomeSurfaceView wellcomView;
	ListViewUtills lvutills;
	TurnPageThread turnpage;
	
	MediaPlayer mp;//媒体播放器引用
	
	SharedPreferences sp;//判断是第几次打开同一本书
	
	List<BookMark> dataBaseBookMark=new ArrayList<BookMark>();//存放所有将要放入“书签界面列表”中的书签
    
	String[] tempname=null;//存放从数据库中取出的所有“书签”的名字
	
	int[] temppage;//存放从数据库中取出的所有当前书书签的页数
	
	String deleteOneBookMarkName=null;//删除一条记录的书签名称
	boolean haveBookMark=false;//判断数据库中是否存在书签
	
	Handler myHandler = new Handler(){//处理各个SurfaceView发送的消息
        public void handleMessage(Message msg) {
        	switch(msg.what)
        	{
        	case WhatMessage.GOTO_WELLCOME_VIEW:
        		goToWellcomView();
        		break;
        	case WhatMessage.GOTO_MAIN_MENU_VIEW:
        		goToReaderView();
        		break;
        	case WhatMessage.GOTO_SEARCHBOOK_LIST:
        		goToSearchBooklist();
        		break;
        	case WhatMessage.GOTO_BACHGROUND_LIST:
        		goToBackground();
        		break;
        	}
        }
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        //全屏显示Activity的设置
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
        	   WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉标头
        //本软件是横屏应用
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制横屏
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //253241#guningyi 强制竖屏
        //获取分辨率
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);       
        //给常量类中的屏幕高和宽赋值
        if(dm.widthPixels>dm.heightPixels)
        {
        	 Constant.SCREEN_WIDTH=dm.heightPixels; 
             Constant.SCREEN_HEIGHT=dm.widthPixels;  
        }
        else
        {
        	Constant.SCREEN_WIDTH=dm.widthPixels;  
            Constant.SCREEN_HEIGHT=dm.heightPixels;
        }
        //给常量类中的自适应屏幕的变量设初值
        Constant.changeRatio();//调用自适应屏幕的方法
        
        readerView=new ReaderView(this);//拿到ReaderView的引用

        isWhichTime();//判断是第几次打开软件，根据第几次打开软件，打开的书页位置不同
        
        lvutills=new ListViewUtills(this);
        turnpage=new TurnPageThread(readerView);
        Log.e("guningyi->","wellcome view!");
        sendMessage(WhatMessage.GOTO_WELLCOME_VIEW);//跳转到主界面
    }
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent e)
	{
		 switch(keyCode)
		 {
		 	case 4:
		 		//跳转到主界面
		 		sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);//跳转到主界面
		 		break;
		   case 82:
			   super.openOptionsMenu();
			   break;   
		   }
		   return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
	   //menu菜单组
	   //找书按钮
	   MenuItem search=menu.add(0, 0, 0, R.string.search);
	   //MenuItem search=menu.add(0, 0, 0, "查找");
	   search.setIcon(R.drawable.m_search);//找书图片
	   OnMenuItemClickListener searchbook=new OnMenuItemClickListener()
	   {//实现菜单项点击事件监听接口
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(LIST_SEARCH);
				return true;
			}    		
		};
		search.setOnMenuItemClickListener(searchbook);//给“找书”添加监听器    	

	   //书签按钮
	   MenuItem bookMark=menu.add(0,0,0,R.string.bookmark);
	   bookMark.setIcon(R.drawable.m_bookmark);
	   OnMenuItemClickListener bookmark=new OnMenuItemClickListener()
	   {
		   @Override
		   public boolean onMenuItemClick(MenuItem item) {
			   showDialog(LIST_BOOKMARK);
			   
			   return true;
		   }	   
	   };
	   bookMark.setOnMenuItemClickListener(bookmark);

	   //翻页按钮
	   MenuItem turnPage=menu.add(0,0,0,R.string.turnpage);
	   turnPage.setIcon(R.drawable.m_turnpage);
	   OnMenuItemClickListener turn=new OnMenuItemClickListener()
	   {//实现菜单项点击事件监听接口
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(LIST_TURNPAGE);
				return true;
			}    		
		};
		turnPage.setOnMenuItemClickListener(turn);//给“翻页”添加监听器  
	    
	   //设置按钮
	   MenuItem setUp=menu.add(0,0,0,R.string.setup);
	   setUp.setIcon(R.drawable.m_set);
	   OnMenuItemClickListener set=new OnMenuItemClickListener()
	   {//实现菜单项点击事件监听接口
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(LIST_SET);
				return true;
			}    		
		};
		setUp.setOnMenuItemClickListener(set);//给“设置”添加监听器    
	     return true;
	}
   
	@Override
	public Dialog onCreateDialog(int id)
	{
		Dialog dialog=null;
		switch(id)
		{
		case LIST_SEARCH://找书
			Builder b=new AlertDialog.Builder(this); 
 		  	b.setIcon(R.drawable.m_search);//设置图标
 		  	b.setTitle(R.string.search);//设置标题
 		  	b.setItems(
 			 R.array.searchbook,
 			 new DialogInterface.OnClickListener()
     		 {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//这里加点击列表中的内容产生结果的代码
						switch(which)
						{
						case 0:
							searchBook();
							break;
						case 1:
							downTxt();
							break;
						}
					}      			
     		 }
 		   );
 		  	dialog=b.create();
 		  	break;
		case LIST_BOOKMARK://书签二级菜单
			b=new AlertDialog.Builder(this);
			b.setIcon(R.drawable.m_bookmark);//设置图标
			b.setTitle(R.string.bookmark);//设置“书签”标题
			b.setItems(
				R.array.bookmark,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					//这里添加点击列表中的内容产生结果的代码
						switch(which)
						{
						case 0://添加书签
							showDialog(NAME_INPUT_DIALOG_ID);
							break;
						case 1://选择书签
							try
							{//判断数据库中是否存在当前这本书的书签
								haveBookMark=SQLDBUtil.judgeHaveBookMark(Constant.FILE_PATH);
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							if(haveBookMark)
							{
								//如果存在书签
								showDialog(SELECT_BOOKMARK);
							}else
							{//如果不存在书签，出Toast
								Toast.makeText
								(
									ReaderActivity.this, 
									"请先添加书签", 
									Toast.LENGTH_SHORT
								).show();
							}
							
							break;
						case 2://清空书签
							showDialog(DELETE_ALL_BOOKMARK);							
							break;
						}
					}
				}	
			);
			dialog=b.create();
			break;
		case NAME_INPUT_DIALOG_ID://弹出添加书签对话框
			dialog=new MyDialog(this);
			break;
		case SELECT_BOOKMARK://弹出选择书签的对话框
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
		case EXIT_READER://退出软件对话框
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
		case DELETE_ONE_BOOKMARK://删除一条书签记录
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
		case DELETE_ALL_BOOKMARK://清空当前这本书的全部记录
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
			
		case SET_FONT_SIZE://字体大小
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
		case BACKGROUND_MUSIC://字体颜色
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
		case SET_FONT_COLOR://设置字体颜色
			dialog=new MyDialogFontColor(this);//用xml文件自己布局
			break;
		case BACKGROUND_PIC://背景图片对话框
			dialog=new MyDialogBackgroundPic(this);//用自己布局的xml问文件
			break;
		case LIST_TURNPAGE://自动翻书
			b=new AlertDialog.Builder(this); 
 		  	b.setIcon(R.drawable.m_turnpage);//设置图标
 		  	b.setTitle(R.string.turnpage);//设置标题
 		  	b.setItems(
 			 R.array.turnpage, 
 			 new DialogInterface.OnClickListener()
     		 {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//这里加点击列表中的内容产生结果的代码
						switch(which)
						{
						case 0://30秒自动翻书
							if(Constant.FILE_PATH==null)//如果没有选书
				    		    {
				    		    	Toast.makeText
									(
										ReaderActivity.this, 
										"请选择您要阅读的文本", 
										Toast.LENGTH_SHORT
									).show();
				    		    	
				    		    }else//如果已经选书
				    		    {
					    		    if(turnpage.isPageflag()==false)//如果线程没有开始，则开始线程
					    		    {
					    		    	turnpage.setPageflag(true);
					    		    	turnpage.start();
					    		    }
							    	turnpage.setFiftySecond(false);
							    	turnpage.setFortySecond(false);
							    	turnpage.setThirtySecond(true);//将30秒设为true其他为false
							    	sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);//跳转到主界面
				    		    }
							break;
						case 1://40秒自动翻书
			    		    if(Constant.FILE_PATH==null)//如果没有选书
			    		    {
			    		    	Toast.makeText
								(
									ReaderActivity.this, 
									"请选择您要阅读的文本", 
									Toast.LENGTH_SHORT
								).show();
			    		    	
			    		    }else//如果已经选书
			    		    {
			    		    	if(turnpage.isPageflag()==false)//如果线程没有开始，则开始线程
				    		    {
				    		    	turnpage.setPageflag(true);
				    		    	turnpage.start();
				    		    }
						    	turnpage.setFiftySecond(false);
						    	turnpage.setThirtySecond(false);//将40秒设为true其他为false
						    	turnpage.setFortySecond(true);
						    	sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);//跳转到主界面
						    }
			    		    break;
						case 2://50秒自动翻书
							if(Constant.FILE_PATH==null)//如果没有选书
				    		    {
				    		    	Toast.makeText
									(
										ReaderActivity.this, 
										"请选择您要阅读的文本", 
										Toast.LENGTH_SHORT
									).show();
				    		    	
				    		    }else//如果已经选书
				    		    {
				    		    	if(turnpage.isPageflag()==false)//如果线程没有开始，则开始线程
					    		    {
					    		    	turnpage.setPageflag(true);
					    		    	turnpage.start();
					    		    }
							    	turnpage.setThirtySecond(false);//将50秒设为true其他为false
							    	turnpage.setFortySecond(false);
							    	turnpage.setFiftySecond(true);
							    	sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);//跳转到主界面
				    		    }
							break;
						case 3://停止自动翻页
							turnpage.setPageflag(false);//停止线程
							sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);//跳转到主界面
							break;
						}
					}      			
     		 }
 		   );
 		  dialog=b.create();
 		  break;  
   	case LIST_SET://设置
   			b=new AlertDialog.Builder(this); 
 		  	b.setIcon(R.drawable.m_set);//设置图标
 		  	b.setTitle(R.string.setup);//设置标题
 		  	b.setItems(
 			 R.array.setup, 
 			 new DialogInterface.OnClickListener()
     		 {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//这里加点击列表中的内容产生结果的代码
						switch(which)
						{
						case 0://背景音乐
							showDialog(BACKGROUND_MUSIC);
							break;
						case 1://背景图片
							showDialog(BACKGROUND_PIC);
							break;
						case 2://字体颜色
							showDialog(SET_FONT_COLOR);//设置字体颜色
							break;
						case 3://字体大小
							showDialog(SET_FONT_SIZE);//显示字体大小对话框
							break;
						
						}
					}    
     		 }
 		   );
 		  	dialog=b.create();
 		  	break;    
		}
		return dialog;
   }
    //每次弹出对话框时被回调以动态更新对话框内容的方法
    @Override
    public void onPrepareDialog(int id, final Dialog dialog)
    {
    	//若不是等待对话框则返回
    	switch(id)
    	{
    	   case NAME_INPUT_DIALOG_ID://姓名输入对话框
    		   //确定按钮
    		   Button bjhmcok=(Button)dialog.findViewById(R.id.bjhmcOk);
    		   //取消按钮
       		   Button bjhmccancel=(Button)dialog.findViewById(R.id.bjhmcCancle);
       		   //给确定按钮添加监听器
       		   bjhmcok.setOnClickListener
               (
    	          new OnClickListener()
    	          {
    				@Override
    				public void onClick(View v) 
    				{
						if(Constant.FILE_PATH==null)//如果没有选择书，不可以加书签
						{
							Toast.makeText(ReaderActivity.this, "请先选择您想要阅读的书", Toast.LENGTH_SHORT).show();
						}else
						{
							//获取对话框里的内容并用Toast显示
	    					EditText et=(EditText)dialog.findViewById(R.id.etname);
	    					String name=et.getText().toString();
	    					if(name.equals(""))//如果书签为空
	    					{
	    						Toast.makeText(ReaderActivity.this, "书签名字不能为空", Toast.LENGTH_SHORT).show();
	    					}else//书签不为空
	    					{
	    						try
								{
									//当前书签的“名字”和当前书签的“页数”存入数据库
									SQLDBUtil.bookMarkInsert(name,Constant.CURRENT_PAGE);
								}catch(Exception e)
								{
									e.printStackTrace();
								}
	    						//关闭对话框
	    						dialog.cancel();
	    					}
						}	
    				}        	  
    	          }
    	        );   
       		    //给取消按钮添加监听器
       		    bjhmccancel.setOnClickListener
	            (
	 	          new OnClickListener()
	 	          {
	 				@Override
	 				public void onClick(View v) 
	 				{	//关闭对话框
	 					dialog.cancel();					
	 				}        	  
	 	          }
	 	        );   
    	   break;
    	   case SELECT_BOOKMARK://选择书签的对话框
    		   try
    		   {
    			   //在数据库中取出所有的书签记录
    			   dataBaseBookMark=SQLDBUtil.getBookmarkList(Constant.FILE_PATH);				   
    			   int i=0;
    			   tempname=new String[dataBaseBookMark.size()];//数组大小
    			   temppage=new int[dataBaseBookMark.size()];//书页

    			   for(BookMark dataBookMark:dataBaseBookMark)
    			   {
    				   tempname[i]=dataBookMark.bmname;//获取所有书签的名字
    				   temppage[i]=dataBookMark.page;
    				   i++;					   
    			   }
    		   }catch(Exception e)
    		   {
    			   e.printStackTrace();
    		   }
    		   
			   //对话框对应的总垂直方向LinearLayout
       		   	LinearLayout ll=new LinearLayout(ReaderActivity.this);
       			ll.setOrientation(LinearLayout.VERTICAL);		//设置朝向	
       			ll.setGravity(Gravity.CENTER_HORIZONTAL);
       			ll.setBackgroundResource(R.drawable.dialog);
       			
       			//标题行的水平LinearLayout
       			LinearLayout lln=new LinearLayout(ReaderActivity.this);
       			lln.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
       			lln.setGravity(Gravity.CENTER);//居中
       			lln.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
       			
       			//标题行的文字
       			TextView tvTitle=new TextView(ReaderActivity.this);
       			tvTitle.setText(R.string.bookmark_dialog);
       			tvTitle.setTextSize(20);//设置字体大小
       			tvTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//设置字体颜色
       			lln.addView(tvTitle);
       			
       			//将标题行添加到总LinearLayout
       			ll.addView(lln);		
       		   	
       		   	//为对话框中的历史记录条目创建ListView
       		    //初始化ListView
       	        ListView lv=new ListView(this);
       	        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
       	        
       	        //为ListView准备内容适配器
       	        BaseAdapter ba=new BaseAdapter()
       	        {
       				@Override
       				public int getCount() {
       					return tempname.length;//总共几个选项
       				}

       				@Override
       				public Object getItem(int arg0) { return null; }

       				@Override
       				public long getItemId(int arg0) { return 0; }

       				@Override
       				public View getView(int arg0, View arg1, ViewGroup arg2) {
       					
       					LinearLayout llb=new LinearLayout(ReaderActivity.this);
						llb.setOrientation(LinearLayout.HORIZONTAL);//设置朝向	
						llb.setPadding(5,5,5,5);//设置四周留白
       					
       					//为书签添加图片
       					ImageView image=new ImageView(ReaderActivity.this);
       					image.setImageDrawable(ReaderActivity.this.getResources().getDrawable(R.drawable.sl_bookmark));
       					image.setScaleType(ImageView.ScaleType.FIT_XY);//按照原图比例
       					image.setLayoutParams(new Gallery.LayoutParams(30, 30));
       					llb.addView(image);
       					
       					//动态生成每条书签对应的TextView
       					TextView tv=new TextView(ReaderActivity.this);
       					tv.setGravity(Gravity.LEFT);
       					tv.setText(tempname[arg0]+"     "+"第"+String.valueOf(temppage[arg0]+1)+"页");//设置内容
       					tv.setTextSize(20);//设置字体大小
       					tv.setTextColor(ReaderActivity.this.getResources().getColor(R.color.white));//设置字体颜色
       					tv.setPadding(0,0,0,0);//设置四周留白
       					llb.addView(tv);
       					return llb;
       				}        	
       	        };       
       	        lv.setAdapter(ba);//为ListView设置内容适配器
       	        
       	        //设置选项被单击的监听器
       	        lv.setOnItemClickListener(
       	           new OnItemClickListener()
       	           {
       				@Override
       				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
       						long arg3) {//重写选项被单击事件的处理方法
   					
   						int page=temppage[arg2];//得到这条记录对应的页号
   						readerView.currRR=readerView.currBook.get(page);//在hashMap中找到对应的readerView.currRR对象
   						Constant.CURRENT_LEFT_START=readerView.currRR.start;//记录当前读到处leftstart的值
   						Constant.CURRENT_PAGE=readerView.currRR.pageNo;//记录当前读到处的page的值
   						
   						//绘制左右两幅图片
   						//readerView.currRR.isLeft=true;
   						readerView.bmLeft=readerView.drawPage(readerView.currRR);
   						//readerView.bmRight=readerView.drawPage(readerView.currRR);
   						readerView.repaint();
       					
       					dialog.cancel();//关闭对话框
       				}        	   
       	           }
       	        );
       	        //将历史记录条的ListView加入总LinearLayout
	       	    ll.addView(lv);

	       	    lv.setOnItemLongClickListener(
	       	    		new OnItemLongClickListener()
	       	    		{
							@Override
							public boolean onItemLongClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								//根据当前的书签的名字，找到对应的书签的页号,删除这条记录
								deleteOneBookMarkName=tempname[arg2];
								try
								{
									showDialog(DELETE_ONE_BOOKMARK);
									
								}catch(Exception e)
								{
									e.printStackTrace();
								}
								dialog.cancel();//关闭对话框
					
								return true;
							}
	       	    		}
	       	    );
	       	     dialog.setContentView(ll); 
    		   break;
    	   case EXIT_READER://退出对话框
    		   
    		   //对话框对应的总垂直方向LinearLayout
      		   	LinearLayout lle=new LinearLayout(ReaderActivity.this);
      			lle.setOrientation(LinearLayout.VERTICAL);		//设置朝向	
      			lle.setGravity(Gravity.CENTER_HORIZONTAL);
      			lle.setBackgroundResource(R.drawable.dialog);
      			
      			//标题行的水平LinearLayout
      			LinearLayout llt=new LinearLayout(ReaderActivity.this);
      			llt.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
      			llt.setGravity(Gravity.CENTER);//居中
      			llt.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
      			
      			//标题行的文字
      			TextView eTitle=new TextView(ReaderActivity.this);
      			eTitle.setText(R.string.exit_bookmark);
      			eTitle.setTextSize(20);//设置字体大小
      			eTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//设置字体颜色
      			llt.addView(eTitle);
      			
      			//将标题行添加到总LinearLayout
      			lle.addView(llt);
      			
      			LinearLayout lleb=new LinearLayout(ReaderActivity.this);
      			lleb.setOrientation(LinearLayout.HORIZONTAL);//水平方向
      			lleb.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
      			lleb.setGravity(Gravity.CENTER);

	       	    //确定按钮
	       	    Button eok=new Button(ReaderActivity.this);//清空书签按钮
	       	    eok.setText(R.string.ok);//设置“按钮”的名字
	       	    eok.setTextSize(18);//设置字体大小
	       	    eok.setWidth(100);
	       	    eok.setHeight(20);
	       	    eok.setGravity(Gravity.CENTER);
	       	    eok.setOnClickListener(
	       	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {
								dialog.cancel();//取消对话框
								savePreference();//当前书页退出时,保存现场
								saveCurrentData();//当前hashMap的信息存入数据库
								System.exit(0);	
							} 
	       	    		 } 
	       	     );
	       	    lleb.addView(eok);//加入到linearLayout中
	       	     //取消按钮
	       	    Button eCancel=new Button(ReaderActivity.this);
	       	    eCancel.setText(R.string.cancel);//设置按钮的名字
	       	    eCancel.setTextSize(18);
	       	    eCancel.setWidth(100);
	       	    eCancel.setHeight(20);
	       	    eCancel.setGravity(Gravity.CENTER);
	            eCancel.setOnClickListener(
	      	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {
								dialog.cancel();//取消对话框
							}
	       	    		 }
	       	     );
	       	    lleb.addView(eCancel);
	       	    lle.addView(lleb);
	       	    dialog.setContentView(lle);
    		   break;
    	   case DELETE_ONE_BOOKMARK://删除一条书签记录对话框
    		   
    		   //对话框对应的总垂直方向LinearLayout
     		   	LinearLayout lld=new LinearLayout(ReaderActivity.this);
     			lld.setOrientation(LinearLayout.VERTICAL);		//设置朝向	
     			lld.setGravity(Gravity.CENTER_HORIZONTAL);
     			lld.setBackgroundResource(R.drawable.dialog);
     			
     			//标题行的水平LinearLayout
     			LinearLayout lldt=new LinearLayout(ReaderActivity.this);
     			lldt.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
     			lldt.setGravity(Gravity.CENTER);//居中
     			lldt.setLayoutParams(new ViewGroup.LayoutParams(240, LayoutParams.WRAP_CONTENT));
     			
     			//标题行的文字
     			TextView deTitle=new TextView(ReaderActivity.this);
     			deTitle.setText(R.string.delete_onebookmark);
     			deTitle.setTextSize(20);//设置字体大小
     			deTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//设置字体颜色
     			lldt.addView(deTitle);
     			
     			//将标题行添加到总LinearLayout
     			lld.addView(lldt);
     			
     			LinearLayout lldeb=new LinearLayout(ReaderActivity.this);
     			lldeb.setOrientation(LinearLayout.HORIZONTAL);//水平方向
     			lldeb.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
     			lldeb.setGravity(Gravity.CENTER);

	       	    //确定按钮
	       	    Button deok=new Button(ReaderActivity.this);//清空书签按钮
	       	    deok.setText(R.string.ok);//设置“按钮”的名字
	       	    deok.setTextSize(18);//设置字体大小
	       	    deok.setWidth(100);
	       	    deok.setHeight(20);
	       	    deok.setGravity(Gravity.CENTER);
	       	    deok.setOnClickListener(
	       	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {	
								try
								{//数据库中删除一条书签记录
									SQLDBUtil.deleteOneBookMark(deleteOneBookMarkName);
								}catch(Exception e)
								{
									e.printStackTrace();
								}
								dialog.cancel();//取消对话框
							} 
	       	    		 } 
	       	     );
	       	    lldeb.addView(deok);//加入到linearLayout中
	       	     //取消按钮
	       	    Button deCancel=new Button(ReaderActivity.this);
	       	    deCancel.setText(R.string.cancel);//设置按钮的名字
	       	    deCancel.setTextSize(18);
	       	    deCancel.setWidth(100);
	       	    deCancel.setHeight(20);
	       	    deCancel.setGravity(Gravity.CENTER);
	            deCancel.setOnClickListener(
	      	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {
								dialog.cancel();//取消对话框
								
								showDialog(SELECT_BOOKMARK);//显示选择书签的对话框
									
							}
	       	    		 }
	       	     );
	            lldeb.addView(deCancel);
	       	    lld.addView(lldeb);
	       	    dialog.setContentView(lld);
   		   break;
    	   case DELETE_ALL_BOOKMARK:
    		   
    		 //对话框对应的总垂直方向LinearLayout
    		   	LinearLayout lla=new LinearLayout(ReaderActivity.this);
    			lla.setOrientation(LinearLayout.VERTICAL);		//设置朝向	
    			lla.setGravity(Gravity.CENTER_HORIZONTAL);
    			lla.setBackgroundResource(R.drawable.dialog);
    			
    			//标题行的水平LinearLayout
    			LinearLayout llat=new LinearLayout(ReaderActivity.this);
    			llat.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
    			llat.setGravity(Gravity.CENTER);//居中
    			llat.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
    			
    			//标题行的文字
    			TextView deaTitle=new TextView(ReaderActivity.this);
    			deaTitle.setText(R.string.delete_allbookmark);
    			deaTitle.setTextSize(20);//设置字体大小
    			deaTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//设置字体颜色
    			llat.addView(deaTitle);
    			
    			//将标题行添加到总LinearLayout
    			lla.addView(llat);
    			
    			LinearLayout lldeab=new LinearLayout(ReaderActivity.this);
    			lldeab.setOrientation(LinearLayout.HORIZONTAL);//水平方向
    			lldeab.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
    			lldeab.setGravity(Gravity.CENTER);

	       	    //确定按钮
	       	    Button deaok=new Button(ReaderActivity.this);//清空书签按钮
	       	    deaok.setText(R.string.ok);//设置“按钮”的名字
	       	    deaok.setTextSize(18);//设置字体大小
	       	    deaok.setWidth(100);
	       	    deaok.setHeight(20);
	       	    deaok.setGravity(Gravity.CENTER);
	       	    deaok.setOnClickListener(
	       	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {	
						
								try
								{//清空当前这本书的全部书签
									SQLDBUtil.deleteAllBookMark(Constant.FILE_PATH);
								}catch(Exception e)
								{
									e.printStackTrace();
								}

								dialog.cancel();//取消对话框
							} 
	       	    		 } 
	       	     );
	       	    lldeab.addView(deaok);//加入到linearLayout中
	       	     //取消按钮
	       	    Button deaCancel=new Button(ReaderActivity.this);
	       	    deaCancel.setText(R.string.cancel);//设置按钮的名字
	       	    deaCancel.setTextSize(18);
	       	    deaCancel.setWidth(100);
	       	    deaCancel.setHeight(20);
	       	    deaCancel.setGravity(Gravity.CENTER);
	            deaCancel.setOnClickListener(
	      	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {
								dialog.cancel();//取消对话框
							}
	       	    		 }
	       	     );
	            lldeab.addView(deaCancel);
	       	    lla.addView(lldeab);
	       	    dialog.setContentView(lla);
    		   break;
    	   case BACKGROUND_MUSIC://布局背景音乐对话框
    		   setBackgroundMusicDialog(dialog);
    		   break;
    	   case BACKGROUND_PIC://背景图片
    		   setBackgroundPic(dialog);
    		   break;
    	   case SET_FONT_SIZE://自己布局字体大小对话框
    		   setFontSize(dialog);//调用setFontSize方法显示布局的dialog
    		   break;
    	   case SET_FONT_COLOR://布局字体的颜色
    		   setFontColor(dialog);//布局字体的颜色
    		   break;
    		   
    	}
    	
    }

	//查找书目
	public void searchBook()
	{

		goToSearchBooklist();
		
		lv=(ListView)ReaderActivity.this.findViewById(R.id.flist);//获取ListView控件对象
		root_b=(Button)findViewById(R.id.Button01);
	    back_b=(Button)findViewById(R.id.Button02);
		final File[] files=lvutills.getFiles(sdcardPath);//获取根节点文件列表 
		lvutills.intoListView(files,lv);//将各个文件添加到ListView列表中
		root_b.setOnClickListener(
				//OnClickListener为View的内部接口，其实现者负责监听鼠标点击事件
	           new View.OnClickListener(){ 
	        	   public void onClick(View v){
	        		   lvutills.intoListView(files,lv);
	        	   }}); 
		back_b.setOnClickListener(
				new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if(lvutills.currentPath.equals("/sdcard"))
						{
							Toast.makeText
							(
								ReaderActivity.this,
								"已经到根目录了", 
								Toast.LENGTH_SHORT
							).show();
						}else
						{
								File cf=new File(lvutills.currentPath);//获取当前文件列表的路径对应的文件
								cf=cf.getParentFile();//获取父目录文件
								lvutills.currentPath=cf.getPath();//记录当前文件列表路径
								lvutills.intoListView(lvutills.getFiles(lvutills.currentPath),lv);	
						}
						
					}});
	}

	public void setBackgroundMusicDialog(final Dialog dialog)
	{
		//对话框对应的总垂直方向LinearLayout
	   	LinearLayout ll=new LinearLayout(ReaderActivity.this);
		ll.setOrientation(LinearLayout.VERTICAL);		//设置朝向	
		ll.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.setBackgroundResource(R.drawable.dialog);
		
		//标题行的水平LinearLayout
		LinearLayout lln=new LinearLayout(ReaderActivity.this);
		lln.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
		lln.setGravity(Gravity.CENTER);//居中
		lln.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
		
		//标题行的文字
		TextView tvTitle=new TextView(ReaderActivity.this);
		tvTitle.setText(R.string.music_name);
		tvTitle.setTextSize(20);//设置字体大小
		tvTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//设置字体颜色
		lln.addView(tvTitle);
		
		//将标题行添加到总LinearLayout
		ll.addView(lln);		
	   	
	   	//为对话框中的历史记录条目创建ListView
	    //初始化ListView
        ListView lv=new ListView(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
        
        //为ListView准备内容适配器
        BaseAdapter ba=new BaseAdapter()
        {
			@Override
			public int getCount() {
				return musicname.length;//总共几个选项
			}

			@Override
			public Object getItem(int arg0) { return null; }

			@Override
			public long getItemId(int arg0) { return 0; }

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				
			LinearLayout llb=new LinearLayout(ReaderActivity.this);
			llb.setOrientation(LinearLayout.HORIZONTAL);//设置朝向	
			llb.setPadding(5,5,5,5);//设置四周留白
				
				//为书签添加图片
				ImageView image=new ImageView(ReaderActivity.this);
				image.setImageDrawable(ReaderActivity.this.getResources().getDrawable(R.drawable.sl_music));//设定图片
				image.setScaleType(ImageView.ScaleType.FIT_XY);//按照原图比例
				image.setLayoutParams(new Gallery.LayoutParams(30, 30));
				llb.addView(image);
				
				//动态生成每条书签对应的TextView
				TextView tv=new TextView(ReaderActivity.this);
				tv.setGravity(Gravity.LEFT);
				tv.setText(ReaderActivity.this.getResources().getString(musicname[arg0]));//设置内容
				tv.setTextSize(20);//设置字体大小
				tv.setTextColor(ReaderActivity.this.getResources().getColor(R.color.white));//设置字体颜色
				tv.setPadding(12,0,0,0);//设置四周留白
				llb.addView(tv);
				return llb;
			}        	
        };       
        lv.setAdapter(ba);//为ListView设置内容适配器
        
        //设置选项被单击的监听器
        lv.setOnItemClickListener(
           new OnItemClickListener()
           {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {//重写选项被单击事件的处理方法
				//根据当前的书签的名字，找到对应的书签的页号，根据页号确定切换到那一页

				switch(arg2)
				{
					case 0:
						if(ReaderActivity.this.mp==null||Constant.I==R.raw.mh)
						{
							Constant.I=R.raw.gsls;
							ReaderActivity.this.mp=MediaPlayer.create(ReaderActivity.this, R.raw.gsls);
							ReaderActivity.this.mp.setLooping(true);
							ReaderActivity.this.mp.start();
							
						}else{
							ReaderActivity.this.mp.release();
							ReaderActivity.this.mp=null;
						}
						
						ReaderActivity.this.goToReaderView();
						
						break;
					case 1:

						if(ReaderActivity.this.mp==null||Constant.I==R.raw.gsls)
						{  	
							Constant.I=R.raw.mh;
							ReaderActivity.this.mp=MediaPlayer.create(ReaderActivity.this, R.raw.mh);
							ReaderActivity.this.mp.setLooping(true);
							ReaderActivity.this.mp.start();
						}else{
							ReaderActivity.this.mp.release();
							ReaderActivity.this.mp=null;
							}
						
						ReaderActivity.this.goToReaderView();
						
						break;
				}
				dialog.cancel();//关闭对话框

				//初始化到当前文件第X页
				readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

				//绘制左右两幅图片
				readerView.bmLeft=readerView.drawPage(readerView.currRR);
				//readerView.bmRight=readerView.drawPage(readerView.currRR);
				readerView.repaint();
			}        	   
           }
        );
        //将历史记录条的ListView加入总LinearLayout
        ll.addView(lv);
        dialog.setContentView(ll); 	
		
	}
	//设置字体颜色
	public void setFontSize(final Dialog dialog)
	{
		//对话框对应的总垂直方向LinearLayout
	   	LinearLayout ll=new LinearLayout(ReaderActivity.this);
		ll.setOrientation(LinearLayout.VERTICAL);		//设置朝向	
		ll.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.setBackgroundResource(R.drawable.dialog);
		
		//标题行的水平LinearLayout
		LinearLayout lln=new LinearLayout(ReaderActivity.this);
		lln.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
		lln.setGravity(Gravity.CENTER);//居中
		lln.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
		
		//标题行的文字
		TextView tvTitle=new TextView(ReaderActivity.this);
		tvTitle.setText(R.string.font_size);
		tvTitle.setTextSize(20);//设置字体大小
		tvTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//设置字体颜色
		lln.addView(tvTitle);
		
		//将标题行添加到总LinearLayout
		ll.addView(lln);		
	   	
	   	//为对话框中的历史记录条目创建ListView
	    //初始化ListView
        ListView lv=new ListView(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
        
        //为ListView准备内容适配器
        BaseAdapter ba=new BaseAdapter()
        {
			@Override
			public int getCount() {
				return fontSizeIds.length;//总共几个选项
			}

			@Override
			public Object getItem(int arg0) { return null; }

			@Override
			public long getItemId(int arg0) { return 0; }

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				
			LinearLayout llb=new LinearLayout(ReaderActivity.this);
			llb.setOrientation(LinearLayout.HORIZONTAL);//设置朝向	
			llb.setPadding(5,5,5,5);//设置四周留白
				
				//为书签添加图片
				ImageView image=new ImageView(ReaderActivity.this);
				image.setImageDrawable(ReaderActivity.this.getResources().getDrawable(fontSizeDrawable[arg0]));//设定图片
				image.setScaleType(ImageView.ScaleType.FIT_XY);//按照原图比例
				image.setLayoutParams(new Gallery.LayoutParams(30, 30));
				llb.addView(image);
				
				//动态生成每条书签对应的TextView
				TextView tv=new TextView(ReaderActivity.this);
				tv.setGravity(Gravity.LEFT);
				tv.setText(ReaderActivity.this.getResources().getString(fontSizeIds[arg0]));//设置内容
				tv.setTextSize(20);//设置字体大小
				tv.setTextColor(ReaderActivity.this.getResources().getColor(R.color.white));//设置字体颜色
				tv.setPadding(0,0,0,0);//设置四周留白
				llb.addView(tv);
				return llb;
			}        	
        };       
        lv.setAdapter(ba);//为ListView设置内容适配器
        
        //设置选项被单击的监听器
        lv.setOnItemClickListener(
           new OnItemClickListener()
           {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {//重写选项被单击事件的处理方法
				
				switch(arg2)
				{
					case 0:
						if(Constant.TEXT_SIZE!=16)//如果当前字体大小不等于要换到的字体大小
						{
							//变换字体大小
							Constant.TEXT_SIZE=16;
							Constant.TEXT_SPACE_BETWEEN_CN=16;
							Constant.TEXT_SPACE_BETWEEN_EN=8;							
							//给常量类中的自适应屏幕的变量重新赋值
					        Constant.changeRatio();
							
					        updataBookMarkAndHashMap();//更新hashMap						
						}else//如果相等
						{
							//不做变换
						}
						
						break;
					case 1:
						if(Constant.TEXT_SIZE!=24)//如果当前字体大小不等于要换到的字体大小
						{
							//变换字体大小
							Constant.TEXT_SIZE=24;
							Constant.TEXT_SPACE_BETWEEN_CN=24;
							Constant.TEXT_SPACE_BETWEEN_EN=12;						
							//给常量类中的自适应屏幕的变量重新赋值
					        Constant.changeRatio();
							
					        updataBookMarkAndHashMap();//更新hashMap						
						}else//如果相等
						{
							//不做变换
						}
						break;
					case 2:
						if(Constant.TEXT_SIZE!=32)//如果当前字体大小不等于要换到的字体大小
						{
							//变换字体大小
							Constant.TEXT_SIZE=32;
							Constant.TEXT_SPACE_BETWEEN_CN=32;
							Constant.TEXT_SPACE_BETWEEN_EN=16;
							
							//给常量类中的自适应屏幕的变量重新赋值
					        Constant.changeRatio();
							
					        updataBookMarkAndHashMap();//更新hashMap
					
						}else//如果相等
						{
							//不做变换
						}
						break;
				}
				dialog.cancel();//关闭对话框


				//初始化到当前文件第X页
				readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

				//绘制左右两幅图片
				readerView.bmLeft=readerView.drawPage(readerView.currRR);
				//readerView.bmRight=readerView.drawPage(readerView.currRR);
				readerView.repaint();
			}        	   
           }
        );
        //将历史记录条的ListView加入总LinearLayout
        ll.addView(lv);
        dialog.setContentView(ll); 
	
	}
	
	//背景图片
	public void setBackgroundPic(final Dialog dialog)
	{
		Button bg_fhzl=(Button)dialog.findViewById(R.id.bg_fhzl);
		Button bg_lsct=(Button)dialog.findViewById(R.id.bg_lsct);
		
		Button bg_sjzx=(Button)dialog.findViewById(R.id.bg_sjzx);
		Button bg_ssnh=(Button)dialog.findViewById(R.id.bg_ssnh);
		
		Button bg_wffm=(Button)dialog.findViewById(R.id.bg_wffm);
		Button bg_ygmm=(Button)dialog.findViewById(R.id.bg_ygmm);
		
		
		
		bg_fhzl.setOnClickListener
        (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.BITMAP=drawableIds[0];
					
					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//自适应屏幕的背景图片
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);
					
					//初始化到当前文件第X页
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);
					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
 					dialog.cancel();
 						
				}        	  
	          }
	        );   
		
		bg_lsct.setOnClickListener
         (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{	
					Constant.BITMAP=drawableIds[1];
					
					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//自适应屏幕的背景图片
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);
					
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
					//关闭对话框
					dialog.cancel();					
				}        	  
	          }
	        );   
		
		bg_sjzx.setOnClickListener
        (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.BITMAP=drawableIds[2];
					
					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//自适应屏幕的背景图片
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);
					
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
 					dialog.cancel();
 						
				}        	  
	          }
	        );   
		
		bg_ssnh.setOnClickListener
         (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.BITMAP=drawableIds[3];
					
					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//自适应屏幕的背景图片
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);
					
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
					//关闭对话框
					dialog.cancel();					
				}        	  
	          }
	        );   
		
		bg_wffm.setOnClickListener
        (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.BITMAP=drawableIds[4];
					
					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//自适应屏幕的背景图片
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);
					
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
 					dialog.cancel();
 						
				}        	  
	          }
	        );   
		
		bg_ygmm.setOnClickListener
         (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{

					Constant.BITMAP=drawableIds[5];

					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//自适应屏幕的背景图片
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);

					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
					//关闭对话框
					dialog.cancel();					
				}        	  
	          }
	        );   	
	}
	
	
	
	//设置字体颜色
	public void setFontColor(final Dialog dialog)
	{
		Button black=(Button)dialog.findViewById(R.id.tc_black);
		Button blue=(Button)dialog.findViewById(R.id.tc_blue);
		
		Button gray=(Button)dialog.findViewById(R.id.tc_gray);
		Button green=(Button)dialog.findViewById(R.id.tc_green);
		
		Button purper=(Button)dialog.findViewById(R.id.tc_purper);
		Button red=(Button)dialog.findViewById(R.id.tc_red);
		
		Button white=(Button)dialog.findViewById(R.id.tc_white);
		Button yellow=(Button)dialog.findViewById(R.id.tc_yellow);
		black.setOnClickListener
        (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.COLOR=0xff000000;
					//初始化到当前文件第X页
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
 					dialog.cancel();
 						
				}        	  
	          }
	        );   
		
		blue.setOnClickListener
         (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.COLOR=0xff0000ff;
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
					
					//关闭对话框
					dialog.cancel();					
				}        	  
	          }
	        );   
		
		gray.setOnClickListener
        (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.COLOR=0xff5b5b5b;
					
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();

 						dialog.cancel();
 						
				}        	  
	          }
	        );   
		
		green.setOnClickListener
         (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.COLOR=0xff00ff00;
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();

					//关闭对话框
					dialog.cancel();					
				}        	  
	          }
	        );   
		
		purper.setOnClickListener
        (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.COLOR=0xff930093;
					
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();

 						dialog.cancel();
 						
				}        	  
	          }
	        );   
		
		red.setOnClickListener
         (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.COLOR=0xffff0000;
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();

					//关闭对话框
					dialog.cancel();					
				}        	  
	          }
	        );   
		
		yellow.setOnClickListener
        (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.COLOR=0xffffff00;
					
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();

 						dialog.cancel();
				}        	  
	          }
	        );   
		
		white.setOnClickListener
         (
	          new OnClickListener()
	          {
				@Override
				public void onClick(View v) 
				{
					Constant.COLOR=0xffffffff;
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//绘制左右两幅图片
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();

					//关闭对话框
					dialog.cancel();					
				}        	  
	          }
	        );   
	}
	
	
	public void downTxt()
	{
		dl=new DownLoad("txt_list.txt",ReaderActivity.this);
		dl.lv.setOnItemClickListener(
				new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {//重写选项被单击事件的处理方法
						String s=dl.txtName[arg2*2+1];
						dl.downFile(Constant.ADD_PRE+s,"/",s);
					} });
	}
	//向Handler发送信息的方法
    public void sendMessage(int what)
    {
    	Message msg = myHandler.obtainMessage(what); 
    	myHandler.sendMessage(msg);
    }  
    //向各个界面跳转的方法
	public void goToSearchBooklist()
    {
    	setContentView(R.layout.searchbook_list);
    	curr=WhichView.SEARCHBOOK_LIST;
    }
	
	
	public void goToReaderView()
	{
		if(readerView==null)
    	{
			readerView=new ReaderView(this);
    	}
    	setContentView(readerView);
    	
    	readerView.requestFocus();
    	readerView.setFocusableInTouchMode(true);
    	
    	readerView.requestFocus();
    	readerView.setFocusableInTouchMode(true);
    	
    	curr=WhichView.MAIN_VIEW;
	}
	public void goToBackground()
	{
		setContentView(R.layout.background_list);
    	curr=WhichView.BACKGROUND_LIST;
	}
	public void goToWellcomView()//进入"百纳科技"界面
	{
		if(wellcomView==null)
    	{
			wellcomView=new WellcomeSurfaceView(this);
    	}
		setContentView(wellcomView);
		curr=WhichView.WELLCOM_VIEW;
	}
	public void goToHiddenBar()//显示haddenBar
	{

		
		setContentView(R.layout.hidden_bar);//guningyi add 增加hiddenBar
		nextChapter = (Button)findViewById(R.id.nextChapter);
		prevChapter = (Button)findViewById(R.id.prevChapter);
		hiddenBar = (LinearLayout)findViewById(R.id.hiddenBar);
		
		
		
	}
	
	public void isWhichTime()//判断是第几次打开软件
	{
		sp=this.getSharedPreferences("read", Context.MODE_PRIVATE);//设为模式为私有模式
		
		String isOneTime=sp.getString("isOneTime", null);//判断是不是第一次打开软件
        String lastTimePath=sp.getString("path", null);//得到上一次访问的书的路径
        String lastTimePage=sp.getString("page", null);//得到上一次访问书的书页
        String lastTimeName=sp.getString("name", null);//得到上一次访问书的名字
        String lastTimeFontSize=sp.getString("fontsize", null);//得到上一次访问书的字体大小
        if(lastTimePath==null)//如果是没有选过书(包括在说明界面退出程序和第一次打开软件)
        {
        	Constant.FILE_PATH=null;//当前路径为空
        	Constant.CURRENT_LEFT_START=0;//当前书页左上方的索引值为0
        	Constant.CURRENT_PAGE=0;//当前书页为0	
        	
        	if(isOneTime==null)//如果是第一次打开软件
        	{//使用默认字体大小，无动作
        	}else//如果曾经在说明界面退出过软件
        	{
        		//确定字体大小
        		Constant.TEXT_SIZE=Integer.parseInt(lastTimeFontSize);//得到上一次在说明界面的字体大小
        		Constant.TEXT_SPACE_BETWEEN_CN=Constant.TEXT_SIZE;
        		Constant.TEXT_SPACE_BETWEEN_EN=Constant.TEXT_SIZE/2;
        		//给常量类中的各个常量重新赋值
                Constant.changeRatio();//调用自适应屏幕的方法
        	}        	
        	
        }else//否则，获取上一次打开书的“路径”与“页数”
        {
        	//确定字体的大小
        	Constant.TEXT_SIZE=Integer.parseInt(lastTimeFontSize);//得到上一次字体大小
    		Constant.TEXT_SPACE_BETWEEN_CN=Constant.TEXT_SIZE;
    		Constant.TEXT_SPACE_BETWEEN_EN=Constant.TEXT_SIZE/2;
    		//给常量类中的各个常量重新赋值
            Constant.changeRatio();//调用自适应屏幕的方法
        	//*****************************确定字体大小结束******************************************
        	Constant.TEXTNAME=lastTimeName;//获取上一次打开文件的书名
        	
        	Constant.FILE_PATH=lastTimePath;//获取上一次打开文件的路径
        	
        	//调用getCharacterCount方法计算文章的字符长度（防止读到最后一页可以继续向下读）
        	Constant.CONTENTCOUNT=TextLoadUtil.getCharacterCount(Constant.FILE_PATH);  
        	
        	
        	int page=Integer.parseInt(lastTimePage);//得到书的页数，转化为int型
        	try{
        		//根据当前路径查找数据库中的对应hashMap的byte型数据
        		byte[] data=SQLDBUtil.selectRecordData(Constant.FILE_PATH);
        		//为readerView中的hashMap
        		readerView.currBook=SQLDBUtil.fromBytesToListRowNodeList(data);//将byte型数据转化为hashMap型的数据      		
        		readerView.currRR=readerView.currBook.get(page);//根据hashMap的索引取出ReadRecord的对象（记录当前书页的左上点索引）
        		Constant.CURRENT_LEFT_START=readerView.currRR.start;//为当前书页左上索引赋值
        		Constant.CURRENT_PAGE=readerView.currRR.pageNo;//为当前书页的page赋值      		
        	}catch(Exception e)
        	{
        		e.printStackTrace();
        	}        	
        }	
	}
	//如果发生换书事件
	public void saveCurrentData()//退出软件和换书时都要存数据库
	{
		if(Constant.FILE_PATH==null)//如果是第一次打开软件(在说明界面退出软件)
		{
			//没有动作
		}else//如果是第n次选书
		{
			try
			{
				byte[] data=SQLDBUtil.fromListRowNodeListToBytes(readerView.currBook);//hashMap转化为byte
				SQLDBUtil.recordInsert(Constant.FILE_PATH,data);//将当前的路径和hashMap的byte形式存入数据库
				//当前书的书页信息存入数据库，方便下次打开时还是当前页
				SQLDBUtil.lastTimeInsert(Constant.FILE_PATH, Constant.CURRENT_PAGE,Constant.TEXT_SIZE);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public void savePreference()//当前书页退出时（点击退出按钮）保存现场Preference
	{
		SharedPreferences.Editor editor=sp.edit();//编辑SharedPreferences
		if(Constant.FILE_PATH==null)//如果是在说明界面退出
		{
			//没有动作发生
		}else//如果是在读书界面退出，保存现场
		{
			//当前的文件路径和文件的page存入preference
			int page=readerView.currRR.pageNo;//当前页数
			editor.putString("path", Constant.FILE_PATH);//当前路径存入SharedPreferences
			editor.putString("page", String.valueOf(page));//将当前页数放入preference(转化为String型)
			editor.putString("name",Constant.TEXTNAME);//将当前书的名字放入preference
		}
		editor.putString("isOneTime", "is");
		editor.putString("fontsize", String.valueOf(Constant.TEXT_SIZE));//把当前字体存入preference
		editor.commit();//提交
		
	}
	//当字体变化后,更新书签和HashMap中存放数据的方法
	public void updataBookMarkAndHashMap()
	{
		try
		{//判断数据库中是否存在当前这本书的书签
			haveBookMark=SQLDBUtil.judgeHaveBookMark(Constant.FILE_PATH);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(haveBookMark)//如果存在书签
		{
			/*
			 * 如果存在书签，先更新书签，在数据库中取出搜有书签的页数和名字，从而得到所有的left_start的值放在一个数组中
			 * 更新hashMap到书签中的left_start处的值。然后不清空hashMap，重新绘制到现在读到的位置
			 */
			
			String[] nameBookMark=null;//暂时存放书签的名字
			int[] pageBookMark = null;//暂时存放书签的页数
			int[] leftStart=null;//暂时存放每条书签对应的leftStart
		   try
		   {
			   //在数据库中取出所有的书签记录
			   dataBaseBookMark=SQLDBUtil.getBookmarkList(Constant.FILE_PATH);				   
			   int i=0;
			   nameBookMark=new String[dataBaseBookMark.size()];//数组大小
			   pageBookMark=new int[dataBaseBookMark.size()];//书页
			   leftStart=new int[dataBaseBookMark.size()];//对应的leftStart

			   for(BookMark bm:dataBaseBookMark)
			   {
				   nameBookMark[i]=bm.bmname;//获取所有书签的名字
				   pageBookMark[i]=bm.page;
				   i++;					   
			   }
		   }catch(Exception e)
		   {
			   e.printStackTrace();
		   }
		   for(int i=0;i<dataBaseBookMark.size();i++)
		   {
			   int m=i;
			   readerView.currRR=readerView.currBook.get(pageBookMark[m]);//在hashMap中找到对应的readerView.currRR对象
			   leftStart[m]=readerView.currRR.start;//获取leftstart的值  
		   }
		   int tempLeftStart=Constant.CURRENT_LEFT_START;//将当前的left_start记录下来
		   //重新绘制界面，并且存书签
		   for(int i=0;i<dataBaseBookMark.size();i++)
		   {			   
			   	Constant.CURRENT_LEFT_START=0;//因为要从第一页开始虚拟绘制 所以值要归零
		   		Constant.CURRENT_PAGE=0;
		   		Constant.nextPageStart=0;
		   		Constant.nextPageNo=0;
		   		
		   		readerView.currBook.clear();//清空hashMap
		   		int m=i;
		   		while(Constant.CURRENT_LEFT_START<leftStart[m])
		   		{
		   			readerView.currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
		   			
		   			Constant.CURRENT_LEFT_START=readerView.currRR.start;//记录当前读到处leftstart的值
		   			Constant.CURRENT_PAGE=readerView.currRR.pageNo;//记录当前读到处的page的值

		   			readerView.currBook.put(readerView.currRR.pageNo, readerView.currRR);//当前页的信息加入hashMap
		   			
		   			//readerView.currRR.isLeft=true;
		   			readerView.drawVirtualPage(readerView.currRR);//绘制左边虚拟页
		   			//readerView.drawVirtualPage(readerView.currRR);//绘制右边虚拟页	
		   		}
		   		//向bookMark中存入更新后的数据
		   		SQLDBUtil.bookMarkInsert(nameBookMark[m],Constant.CURRENT_PAGE);
		   }
		   //不清hashMap，重新绘制到我们现在读的界面
		   
		   Constant.CURRENT_LEFT_START=0;//因为要从第一页开始虚拟绘制 所以值要归零
		   Constant.CURRENT_PAGE=0;
		   Constant.nextPageStart=0;
		   Constant.nextPageNo=0;
		   
		   readerView.currRR=new ReadRecord(0,0);
		   readerView.currBook.put(0, readerView.currRR);//将第一页放入hashMap中
		   
		   while(Constant.CURRENT_LEFT_START<tempLeftStart)
		   {
			   readerView.currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
	
			   Constant.CURRENT_LEFT_START=readerView.currRR.start;//记录当前读到处leftstart的值
			   Constant.CURRENT_PAGE=readerView.currRR.pageNo;//记录当前读到处的page的值

			   readerView.currBook.put(readerView.currRR.pageNo, readerView.currRR);//当前页的信息加入hashMap
	
			   //readerView.currRR.isLeft=true;
			   readerView.drawVirtualPage(readerView.currRR);//绘制左边虚拟页
			   //readerView.drawVirtualPage(readerView.currRR);//绘制右边虚拟页	
		   }	   	
		}else//如果不存在书签，只更新当前页数的hashMap
		//否则，根据当前页的Left_Start计算
	   	{
	   		int tempLeftStart=Constant.CURRENT_LEFT_START;//将当前的left_start记录下来

	   		Constant.CURRENT_LEFT_START=0;//因为要从第一页开始虚拟绘制 所以值要归零
	   		Constant.CURRENT_PAGE=0;
	   		Constant.nextPageStart=0;
	   		Constant.nextPageNo=0;
	   		
	   		readerView.currBook.clear();//清空hashMap
	   		
	   		readerView.currRR=new ReadRecord(0,0);
			readerView.currBook.put(0, readerView.currRR);//将第一页放入hashMap中
	   		
	   		while(Constant.CURRENT_LEFT_START<tempLeftStart)
	   		{
	   			readerView.currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
	   			
	   			Constant.CURRENT_LEFT_START=readerView.currRR.start;//记录当前读到处leftstart的值
	   			Constant.CURRENT_PAGE=readerView.currRR.pageNo;//记录当前读到处的page的值

	   			readerView.currBook.put(readerView.currRR.pageNo, readerView.currRR);//当前页的信息加入hashMap
	   			
	   			//readerView.currRR.isLeft=true;
	   			readerView.drawVirtualPage(readerView.currRR);//绘制左边虚拟页
	   		}
	   	}
	}
	
    @Override 
    public void onResume()
    {
    	super.onResume();
    }
    @Override 
    public void onPause()
    {
    	super.onPause();
    }
}