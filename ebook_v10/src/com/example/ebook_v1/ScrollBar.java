package com.example.ebook_v1;



import java.io.File;
import java.util.HashMap;




import com.example.ebook_v1.Constant.*;
import com.example.ebook_v1.TextLoadUtil;






import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ScrollBar extends Activity{

	//界面相关的参数
	//表示各个界面的安全类型枚举
	enum WhichView {WELLCOM_VIEW,MAIN_VIEW,SEARCHBOOK_LIST,BACKGROUND_LIST }
	WhichView curr;
	
	
	//书籍阅读记录相关
	HashMap<Integer,ReadRecord> currBook=new HashMap<Integer,ReadRecord>();
	ReadRecord currRR;//当前页数据
	//ReadRecord currRR_temp;
	
	//书籍查找数目的listview相关变量
	private ListView lv = null;
	ListViewUtills lvutills;
	
	private String SDPATH;
	
	String sdcardPath=null;//根目录
	String leavePath;//子文件路径
	Button root_b;//返回根目录的按钮
	Button back_b;//返回到上层目录
	
	
	//从SDCARD中读取文本的数量
	public static int PAGE_LENGTH;
	public String FILE_PATH;
	public static int CONTENTCOUNT;//文本的总字符数


	
	//text正文相关变量
	private TextView text = null;
	private String content1 = null; 
	private String content2 = null; 
	private String textFormSdcardFile = null; 
	
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
/*	private TextView chapterInfo = null;
	private int chapterInfoHeight = 0;*/
	
	//Button相关变量
	private Button prevChapter = null;
	private Button nextChapter = null;
	private Button settingBook = null;
	private Button searchBook = null;
	
	
    public ScrollBar(){
    	currRR = new ReadRecord(0,0);
	}
	
	
	@SuppressWarnings({ "deprecation", "deprecation", "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroll_bar);
		
		lvutills=new ListViewUtills(this);
		
		screenWidth  = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight(); 
		SDPATH = Environment.getExternalStorageDirectory().getPath();
		sdcardPath = SDPATH;
		
		//读数据库找到上次退出时保存在其中的阅读记录，将这个记录赋值给currRR
		//iswhich();
		
		
		content1 = "请使用查找按钮从sdcard中选择要读的书";

		text = (TextView)findViewById(R.id.text);
		text.setTextSize(22);
		text.setText(content1);

		searchBook = (Button)findViewById(R.id.searchBooks);
		prevChapter = (Button)findViewById(R.id.prevChapter);
		nextChapter = (Button)findViewById(R.id.nextChapter);
		settingBook = (Button)findViewById(R.id.settings);
		
		prevChapter.setOnClickListener(new OnClickListener(){
	        public void onClick(View v) 
			{
	        	//不需要读取数据库，只要直接去读Constant.CURRENT_PAGE;
	        	//Constant.CURRENT_START
	        	//向前翻页要加保护，比如跳出已经到第一页的提示
	    		ReadRecord currRR_temp = new ReadRecord(0,0);
	    		if(Constant.CURRENT_PAGE == 0)
	    		{
	    			//打印出已经到第一页的提示
	    			printInfo();
	    		}
	    		else
	    		{
	    			currRR_temp.pageNo = Constant.CURRENT_PAGE--;
	    			if (Constant.CURRENT_START > 6000)
	    			{	
	    	        	currRR_temp.start = Constant.CURRENT_START - 6000;
	    			}
	    			else
	    			{
	    				currRR_temp.start = 0;
	    			}
	    		}
	        	
	    		String prevPage = null;
	    		
	    		prevPage = read_text(currRR_temp);
	    		
	    		text = (TextView)findViewById(R.id.text);
	    		text.setTextSize(22);
	    		text.setText(prevPage);
	            
	            //更新数据
	        	Constant.CURRENT_PAGE = currRR_temp.pageNo;
				Constant.CURRENT_START = currRR_temp.start;
	    		saveCurrentData();		
			}	  
          }
        );
		
		
		nextChapter.setOnClickListener(new OnClickListener(){
	        public void onClick(View v) 
			{
	        	//不需要读取数据库，只要直接去读Constant.CURRENT_PAGE;
	        	//Constant.CURRENT_START
	    		ReadRecord currRR_temp = new ReadRecord(0,0);
	        	currRR_temp.pageNo = Constant.CURRENT_PAGE++;
	        	currRR_temp.start = Constant.CURRENT_START + 6000;
	    		String nextPage = null;
	    		
	    		nextPage = read_text(currRR_temp);
	    		
	    		text = (TextView)findViewById(R.id.text);
	    		text.setTextSize(22);
	    		text.setText(nextPage);
	            
	            //更新数据
	        	Constant.CURRENT_PAGE = currRR_temp.pageNo;
				Constant.CURRENT_START = currRR_temp.start;
	    		saveCurrentData();		
			}	  
          }
        );
		
		settingBook.setOnClickListener(new OnClickListener(){
		        public void onClick(View v) 
				{
					Intent intent = new Intent();
					intent.setClass(ScrollBar.this, SettingBook.class);
					startActivity(intent);
					finish();	
				}	  
	          }
	        );
		
		
		//settingBook.setOnClickListener((android.view.View.OnClickListener) this);
		//prevChapter.setOnClickListener((android.view.View.OnClickListener) this);
		//nextChapter.setOnClickListener((android.view.View.OnClickListener) this);
		
		searchBook.setOnClickListener(new OnClickListener(){
	        public void onClick(View v) 
			{
	        	searchBook();
				//finish();	
			}	  
          });
		
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scroll_bar, menu);
		return true;
	}


	
	
	public void showContent(String string) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_scroll_bar);
		searchBook = (Button)findViewById(R.id.searchBooks);
		prevChapter = (Button)findViewById(R.id.prevChapter);
		nextChapter = (Button)findViewById(R.id.nextChapter);
		settingBook = (Button)findViewById(R.id.settings);
		
		prevChapter.setOnClickListener(new OnClickListener(){

			public void onClick(View v) 
			{
				//不需要读取数据库，只要直接去读Constant.CURRENT_PAGE;
	        	//Constant.CURRENT_START
	        	//向前翻页要加保护，比如跳出已经到第一页的提示
	    		ReadRecord currRR_temp = new ReadRecord(0,0);
	    		if(Constant.CURRENT_PAGE == 0)
	    		{
	    			//打印出已经到第一页的提示
	    			printInfo();
	    		}
	    		else
	    		{
	    			currRR_temp.pageNo = Constant.CURRENT_PAGE--;
	    			if (Constant.CURRENT_START > 6000)
	    			{	
	    	        	currRR_temp.start = Constant.CURRENT_START - 6000;
	    			}
	    			else
	    			{
	    				currRR_temp.start = 0;
	    			}
	    		}
	        	
	    		String prevPage = null;
	    		
	    		prevPage = read_text(currRR_temp);
	    		
	    		text = (TextView)findViewById(R.id.text);
	    		text.setTextSize(22);
	    		text.setText(prevPage);
	            
	            //更新数据
	        	Constant.CURRENT_PAGE = currRR_temp.pageNo;
				Constant.CURRENT_START = currRR_temp.start;
	    		saveCurrentData();		
				
	        	//不需要读取数据库，只要直接去读Constant.CURRENT_PAGE;
	        	//Constant.CURRENT_START
				/*ReadRecord currRR_temp = new ReadRecord(0,0);
	        	currRR_temp.pageNo = Constant.CURRENT_PAGE++;
	        	currRR_temp.start = Constant.CURRENT_START + 6000;
	    		String prevPage = null;
	    		
	    		prevPage = read_text(currRR_temp);
	    		
	    		text = (TextView)findViewById(R.id.text);
	    		text.setTextSize(22);
	    		text.setText(prevPage);
	            if (Constant.CURRENT_PAGE > 0)
	            {
	            	Constant.CURRENT_PAGE++;
	            }
	            
	            //更新数据
	        	Constant.CURRENT_PAGE++;
				Constant.CURRENT_START += 6000;
	    		saveCurrentData();		*/    		
			}	  
          }
        );
		
		
		nextChapter.setOnClickListener(new OnClickListener(){
	        public void onClick(View v) 
			{
	        	//不需要读取数据库，只要直接去读Constant.CURRENT_PAGE;
	        	//Constant.CURRENT_START
	    		ReadRecord currRR_temp = new ReadRecord(0,0);
	        	currRR_temp.pageNo = Constant.CURRENT_PAGE++;
	        	currRR_temp.start = Constant.CURRENT_START + 6000;
	    		String nextPage = null;
	    		
	    		nextPage = read_text(currRR_temp);
	    		
	    		text = (TextView)findViewById(R.id.text);
	    		text.setTextSize(22);
	    		text.setText(nextPage);
	            
	            //更新数据
	        	Constant.CURRENT_PAGE = currRR_temp.pageNo;
				Constant.CURRENT_START = currRR_temp.start;
	    		saveCurrentData();		
	        	
	        	
	        	//不需要读取数据库，只要直接去读Constant.CURRENT_PAGE;
	        	//Constant.CURRENT_START
	    		/*ReadRecord currRR_temp = new ReadRecord(0,0);
	        	currRR_temp.pageNo = Constant.CURRENT_PAGE++;
	        	currRR_temp.start = Constant.CURRENT_START + 6000;
	    		String prevPage = null;
	    		
	    		prevPage = read_text(currRR_temp);
	    		
	    		text = (TextView)findViewById(R.id.text);
	    		text.setTextSize(22);
	    		text.setText(prevPage);
	            if (Constant.CURRENT_PAGE > 0)
	            {
	            	Constant.CURRENT_PAGE++;
	            }
	            
	            //更新数据
	        	Constant.CURRENT_PAGE++;
				Constant.CURRENT_START += 6000;
	    		saveCurrentData();		*/
			}	  
          }
        );
		
		settingBook.setOnClickListener(new OnClickListener(){
		        public void onClick(View v) 
				{
					Intent intent = new Intent();
					intent.setClass(ScrollBar.this, SettingBook.class);
					startActivity(intent);
					finish();	
				}	  
	          }
	        );
		
		searchBook.setOnClickListener(new OnClickListener(){
	        public void onClick(View v) 
			{
	        	searchBook();
				//finish();	
			}	  
          });
		
		String content = string;
		text = (TextView)findViewById(R.id.text);
		text.setTextSize(22);
		text.setText(content);
        if (Constant.CURRENT_PAGE > 0)
        {
        	Constant.CURRENT_PAGE++;
        }
		saveCurrentData();	
	}
	

	public void goToSearchBooklist()
    {
    	setContentView(R.layout.searchbook_list);
    	curr=WhichView.SEARCHBOOK_LIST;
    }
	
	//查找书目
	public void searchBook()
			{

				goToSearchBooklist();
				
				lv=(ListView)ScrollBar.this.findViewById(R.id.flist);//获取ListView控件对象
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
										ScrollBar.this,
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
			
			//读取文本文件
			public String read_text(ReadRecord record)
			{
				int start = record.start;	
				if(Constant.FILE_PATH==null)
				{
					textFormSdcardFile=TextLoadUtil.loadFromSDFile(this,start,PAGE_LENGTH,Constant.DIRECTIONSNAME);//读取说明
					CONTENTCOUNT=TextLoadUtil.getCharacterCountApk(this, Constant.DIRECTIONSNAME);
				}else//否则读正文
				{
					textFormSdcardFile=TextLoadUtil.readFragment(start, 6000, Constant.FILE_PATH);//读取正文,固定读取6000characters
				}
				return textFormSdcardFile;
				//因为是读的文本文档，而且是按照字数读取，就没有必要再计算行数了。
				//直接读文档并且显示
				
				
				
/*				int index=0;
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
				}*/
			
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
						
						//如果在这里保存的currBook中没有正确的值，那么插入数据库的数据也就没有意义
						//等再次打开数据库时，得到的值也是空的。
						
						this.currRR.pageNo = Constant.CURRENT_PAGE;
						this.currRR.start = Constant.CURRENT_START;
						this.currBook.put(Constant.CURRENT_PAGE,this.currRR);
						byte[] data=SQLDBUtil.fromListRowNodeListToBytes(this.currBook);//hashMap转化为byte
						SQLDBUtil.recordInsert(Constant.FILE_PATH,data);//将当前的路径和hashMap的byte形式存入数据库
						//当前书的书页信息存入数据库，方便下次打开时还是当前页
						SQLDBUtil.lastTimeInsert(Constant.FILE_PATH, Constant.CURRENT_PAGE,Constant.TEXT_SIZE);
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			
			public void printInfo()
			{
				
				Toast.makeText
				(
					this, 
					"已经到达第一页", 
					Toast.LENGTH_SHORT
				).show();
			}
			
			
}

