package com.bn.reader;//���

//����������
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

//��ʶ����SurfaceView�ĳ�����
class WhatMessage{
	public static final int GOTO_WELLCOME_VIEW=0;
	public static final int GOTO_MAIN_MENU_VIEW=1;
	public static final int GOTO_SEARCHBOOK_LIST=2;
	public static final int GOTO_BACHGROUND_LIST=3;
}
//��ʾ��������İ�ȫ����ö��
enum WhichView {WELLCOM_VIEW,MAIN_VIEW,SEARCHBOOK_LIST,BACKGROUND_LIST }

public class ReaderActivity extends Activity {
	//����ͼ
	int[] drawableIds=
	{R.drawable.bg_fhzl,R.drawable.bg_lsct,R.drawable.bg_sjzx,R.drawable.bg_ssnh,R.drawable.bg_wffm,R.drawable.bg_ygmm};
	//����ͼ����
	int[] msgIds={R.string.fhzl,R.string.lsct,R.string.sjzx,R.string.ssnh,R.string.wffm,R.string.ygmm};
	//������ɫ
	int[] drawColorIds={R.drawable.tc_black,R.drawable.tc_blue,R.drawable.tc_gray,R.drawable.tc_green,
			R.drawable.tc_purper,R.drawable.tc_red,R.drawable.tc_yellow,R.drawable.tc_white};
	//������ɫ����
	int[] msgIds2={R.string.tc_black,R.string.tc_blue,R.string.tc_gray,R.string.tc_green,
			R.string.tc_purper,R.string.tc_red,R.string.tc_yellow,R.string.tc_white};
	//�����С����
	int[] fontSizeIds={R.string.font_size16,R.string.font_size24,R.string.font_size32};
	//����ͼƬ
	int[] fontSizeDrawable={R.drawable.font_size3,R.drawable.font_size2,R.drawable.font_size1};
	
	//��������
	int[] musicname={R.string.music_gs,R.string.music_mh};
	
	String sdcardPath="/sdcard";//��Ŀ¼
	String leavePath;//���ļ�·��
	ListView lv;//�б������
	Button root_b;//���ظ�Ŀ¼�İ�ť
	Button back_b;//���ص��ϲ�Ŀ¼
	
	//dialog���
	final int LIST_SEARCH=1;//���鰴ť
	final int LIST_TURNPAGE=2;//�Զ����鰴ť
	final int LIST_SET=3;//���ð�ť
	final int LIST_BOOKMARK=4;//��ǩ��ť
	
	final int NAME_INPUT_DIALOG_ID=5;//������ǩ���ֵĶԻ���
	final int SELECT_BOOKMARK=6;//ѡ����ǩ�ĶԻ���	
	final int EXIT_READER=7;//�˳�����Ի���
	final int DELETE_ONE_BOOKMARK=8;//ɾ��һ����¼�ĶԻ���
	final int DELETE_ALL_BOOKMARK=9;//��յ�ǰ���������ǩ
	
	final int SET_FONT_SIZE	=10;//���������С�Ի���
	final int SET_FONT_COLOR=11;//����������ɫ�Ի���
	
	final int BACKGROUND_PIC=12;//����ͼƬ
	final int BACKGROUND_MUSIC=13;//�������ֶԻ���
	
	public boolean hideLayout_flag = false;//guningyi add�������жϰ�ť����ʾ
	public Button nextChapter = null;
	public Button prevChapter = null;
	public LinearLayout hiddenBar = null;

	DownLoad dl;
	WhichView curr;
	ReaderView readerView;//ReaderView������
	WellcomeSurfaceView wellcomView;
	ListViewUtills lvutills;
	TurnPageThread turnpage;
	
	MediaPlayer mp;//ý�岥��������
	
	SharedPreferences sp;//�ж��ǵڼ��δ�ͬһ����
	
	List<BookMark> dataBaseBookMark=new ArrayList<BookMark>();//������н�Ҫ���롰��ǩ�����б��е���ǩ
    
	String[] tempname=null;//��Ŵ����ݿ���ȡ�������С���ǩ��������
	
	int[] temppage;//��Ŵ����ݿ���ȡ�������е�ǰ����ǩ��ҳ��
	
	String deleteOneBookMarkName=null;//ɾ��һ����¼����ǩ����
	boolean haveBookMark=false;//�ж����ݿ����Ƿ������ǩ
	
	Handler myHandler = new Handler(){//�������SurfaceView���͵���Ϣ
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
        
        
        //ȫ����ʾActivity������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ������
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
        	   WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȥ����ͷ
        //������Ǻ���Ӧ��
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//ǿ�ƺ���
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //253241#guningyi ǿ������
        //��ȡ�ֱ���
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);       
        //���������е���Ļ�ߺͿ�ֵ
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
        //���������е�����Ӧ��Ļ�ı������ֵ
        Constant.changeRatio();//��������Ӧ��Ļ�ķ���
        
        readerView=new ReaderView(this);//�õ�ReaderView������

        isWhichTime();//�ж��ǵڼ��δ���������ݵڼ��δ�������򿪵���ҳλ�ò�ͬ
        
        lvutills=new ListViewUtills(this);
        turnpage=new TurnPageThread(readerView);
        Log.e("guningyi->","wellcome view!");
        sendMessage(WhatMessage.GOTO_WELLCOME_VIEW);//��ת��������
    }
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent e)
	{
		 switch(keyCode)
		 {
		 	case 4:
		 		//��ת��������
		 		sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);//��ת��������
		 		break;
		   case 82:
			   super.openOptionsMenu();
			   break;   
		   }
		   return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
	   //menu�˵���
	   //���鰴ť
	   MenuItem search=menu.add(0, 0, 0, R.string.search);
	   //MenuItem search=menu.add(0, 0, 0, "����");
	   search.setIcon(R.drawable.m_search);//����ͼƬ
	   OnMenuItemClickListener searchbook=new OnMenuItemClickListener()
	   {//ʵ�ֲ˵������¼������ӿ�
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(LIST_SEARCH);
				return true;
			}    		
		};
		search.setOnMenuItemClickListener(searchbook);//�������顱��Ӽ�����    	

	   //��ǩ��ť
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

	   //��ҳ��ť
	   MenuItem turnPage=menu.add(0,0,0,R.string.turnpage);
	   turnPage.setIcon(R.drawable.m_turnpage);
	   OnMenuItemClickListener turn=new OnMenuItemClickListener()
	   {//ʵ�ֲ˵������¼������ӿ�
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(LIST_TURNPAGE);
				return true;
			}    		
		};
		turnPage.setOnMenuItemClickListener(turn);//������ҳ����Ӽ�����  
	    
	   //���ð�ť
	   MenuItem setUp=menu.add(0,0,0,R.string.setup);
	   setUp.setIcon(R.drawable.m_set);
	   OnMenuItemClickListener set=new OnMenuItemClickListener()
	   {//ʵ�ֲ˵������¼������ӿ�
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(LIST_SET);
				return true;
			}    		
		};
		setUp.setOnMenuItemClickListener(set);//�������á���Ӽ�����    
	     return true;
	}
   
	@Override
	public Dialog onCreateDialog(int id)
	{
		Dialog dialog=null;
		switch(id)
		{
		case LIST_SEARCH://����
			Builder b=new AlertDialog.Builder(this); 
 		  	b.setIcon(R.drawable.m_search);//����ͼ��
 		  	b.setTitle(R.string.search);//���ñ���
 		  	b.setItems(
 			 R.array.searchbook,
 			 new DialogInterface.OnClickListener()
     		 {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//����ӵ���б��е����ݲ�������Ĵ���
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
		case LIST_BOOKMARK://��ǩ�����˵�
			b=new AlertDialog.Builder(this);
			b.setIcon(R.drawable.m_bookmark);//����ͼ��
			b.setTitle(R.string.bookmark);//���á���ǩ������
			b.setItems(
				R.array.bookmark,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					//������ӵ���б��е����ݲ�������Ĵ���
						switch(which)
						{
						case 0://�����ǩ
							showDialog(NAME_INPUT_DIALOG_ID);
							break;
						case 1://ѡ����ǩ
							try
							{//�ж����ݿ����Ƿ���ڵ�ǰ�Ȿ�����ǩ
								haveBookMark=SQLDBUtil.judgeHaveBookMark(Constant.FILE_PATH);
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							if(haveBookMark)
							{
								//���������ǩ
								showDialog(SELECT_BOOKMARK);
							}else
							{//�����������ǩ����Toast
								Toast.makeText
								(
									ReaderActivity.this, 
									"���������ǩ", 
									Toast.LENGTH_SHORT
								).show();
							}
							
							break;
						case 2://�����ǩ
							showDialog(DELETE_ALL_BOOKMARK);							
							break;
						}
					}
				}	
			);
			dialog=b.create();
			break;
		case NAME_INPUT_DIALOG_ID://���������ǩ�Ի���
			dialog=new MyDialog(this);
			break;
		case SELECT_BOOKMARK://����ѡ����ǩ�ĶԻ���
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
		case EXIT_READER://�˳�����Ի���
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
		case DELETE_ONE_BOOKMARK://ɾ��һ����ǩ��¼
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
		case DELETE_ALL_BOOKMARK://��յ�ǰ�Ȿ���ȫ����¼
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
			
		case SET_FONT_SIZE://�����С
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
		case BACKGROUND_MUSIC://������ɫ
			b=new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialog=b.create();
			break;
		case SET_FONT_COLOR://����������ɫ
			dialog=new MyDialogFontColor(this);//��xml�ļ��Լ�����
			break;
		case BACKGROUND_PIC://����ͼƬ�Ի���
			dialog=new MyDialogBackgroundPic(this);//���Լ����ֵ�xml���ļ�
			break;
		case LIST_TURNPAGE://�Զ�����
			b=new AlertDialog.Builder(this); 
 		  	b.setIcon(R.drawable.m_turnpage);//����ͼ��
 		  	b.setTitle(R.string.turnpage);//���ñ���
 		  	b.setItems(
 			 R.array.turnpage, 
 			 new DialogInterface.OnClickListener()
     		 {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//����ӵ���б��е����ݲ�������Ĵ���
						switch(which)
						{
						case 0://30���Զ�����
							if(Constant.FILE_PATH==null)//���û��ѡ��
				    		    {
				    		    	Toast.makeText
									(
										ReaderActivity.this, 
										"��ѡ����Ҫ�Ķ����ı�", 
										Toast.LENGTH_SHORT
									).show();
				    		    	
				    		    }else//����Ѿ�ѡ��
				    		    {
					    		    if(turnpage.isPageflag()==false)//����߳�û�п�ʼ����ʼ�߳�
					    		    {
					    		    	turnpage.setPageflag(true);
					    		    	turnpage.start();
					    		    }
							    	turnpage.setFiftySecond(false);
							    	turnpage.setFortySecond(false);
							    	turnpage.setThirtySecond(true);//��30����Ϊtrue����Ϊfalse
							    	sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);//��ת��������
				    		    }
							break;
						case 1://40���Զ�����
			    		    if(Constant.FILE_PATH==null)//���û��ѡ��
			    		    {
			    		    	Toast.makeText
								(
									ReaderActivity.this, 
									"��ѡ����Ҫ�Ķ����ı�", 
									Toast.LENGTH_SHORT
								).show();
			    		    	
			    		    }else//����Ѿ�ѡ��
			    		    {
			    		    	if(turnpage.isPageflag()==false)//����߳�û�п�ʼ����ʼ�߳�
				    		    {
				    		    	turnpage.setPageflag(true);
				    		    	turnpage.start();
				    		    }
						    	turnpage.setFiftySecond(false);
						    	turnpage.setThirtySecond(false);//��40����Ϊtrue����Ϊfalse
						    	turnpage.setFortySecond(true);
						    	sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);//��ת��������
						    }
			    		    break;
						case 2://50���Զ�����
							if(Constant.FILE_PATH==null)//���û��ѡ��
				    		    {
				    		    	Toast.makeText
									(
										ReaderActivity.this, 
										"��ѡ����Ҫ�Ķ����ı�", 
										Toast.LENGTH_SHORT
									).show();
				    		    	
				    		    }else//����Ѿ�ѡ��
				    		    {
				    		    	if(turnpage.isPageflag()==false)//����߳�û�п�ʼ����ʼ�߳�
					    		    {
					    		    	turnpage.setPageflag(true);
					    		    	turnpage.start();
					    		    }
							    	turnpage.setThirtySecond(false);//��50����Ϊtrue����Ϊfalse
							    	turnpage.setFortySecond(false);
							    	turnpage.setFiftySecond(true);
							    	sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);//��ת��������
				    		    }
							break;
						case 3://ֹͣ�Զ���ҳ
							turnpage.setPageflag(false);//ֹͣ�߳�
							sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);//��ת��������
							break;
						}
					}      			
     		 }
 		   );
 		  dialog=b.create();
 		  break;  
   	case LIST_SET://����
   			b=new AlertDialog.Builder(this); 
 		  	b.setIcon(R.drawable.m_set);//����ͼ��
 		  	b.setTitle(R.string.setup);//���ñ���
 		  	b.setItems(
 			 R.array.setup, 
 			 new DialogInterface.OnClickListener()
     		 {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//����ӵ���б��е����ݲ�������Ĵ���
						switch(which)
						{
						case 0://��������
							showDialog(BACKGROUND_MUSIC);
							break;
						case 1://����ͼƬ
							showDialog(BACKGROUND_PIC);
							break;
						case 2://������ɫ
							showDialog(SET_FONT_COLOR);//����������ɫ
							break;
						case 3://�����С
							showDialog(SET_FONT_SIZE);//��ʾ�����С�Ի���
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
    //ÿ�ε����Ի���ʱ���ص��Զ�̬���¶Ի������ݵķ���
    @Override
    public void onPrepareDialog(int id, final Dialog dialog)
    {
    	//�����ǵȴ��Ի����򷵻�
    	switch(id)
    	{
    	   case NAME_INPUT_DIALOG_ID://��������Ի���
    		   //ȷ����ť
    		   Button bjhmcok=(Button)dialog.findViewById(R.id.bjhmcOk);
    		   //ȡ����ť
       		   Button bjhmccancel=(Button)dialog.findViewById(R.id.bjhmcCancle);
       		   //��ȷ����ť��Ӽ�����
       		   bjhmcok.setOnClickListener
               (
    	          new OnClickListener()
    	          {
    				@Override
    				public void onClick(View v) 
    				{
						if(Constant.FILE_PATH==null)//���û��ѡ���飬�����Լ���ǩ
						{
							Toast.makeText(ReaderActivity.this, "����ѡ������Ҫ�Ķ�����", Toast.LENGTH_SHORT).show();
						}else
						{
							//��ȡ�Ի���������ݲ���Toast��ʾ
	    					EditText et=(EditText)dialog.findViewById(R.id.etname);
	    					String name=et.getText().toString();
	    					if(name.equals(""))//�����ǩΪ��
	    					{
	    						Toast.makeText(ReaderActivity.this, "��ǩ���ֲ���Ϊ��", Toast.LENGTH_SHORT).show();
	    					}else//��ǩ��Ϊ��
	    					{
	    						try
								{
									//��ǰ��ǩ�ġ����֡��͵�ǰ��ǩ�ġ�ҳ�����������ݿ�
									SQLDBUtil.bookMarkInsert(name,Constant.CURRENT_PAGE);
								}catch(Exception e)
								{
									e.printStackTrace();
								}
	    						//�رնԻ���
	    						dialog.cancel();
	    					}
						}	
    				}        	  
    	          }
    	        );   
       		    //��ȡ����ť��Ӽ�����
       		    bjhmccancel.setOnClickListener
	            (
	 	          new OnClickListener()
	 	          {
	 				@Override
	 				public void onClick(View v) 
	 				{	//�رնԻ���
	 					dialog.cancel();					
	 				}        	  
	 	          }
	 	        );   
    	   break;
    	   case SELECT_BOOKMARK://ѡ����ǩ�ĶԻ���
    		   try
    		   {
    			   //�����ݿ���ȡ�����е���ǩ��¼
    			   dataBaseBookMark=SQLDBUtil.getBookmarkList(Constant.FILE_PATH);				   
    			   int i=0;
    			   tempname=new String[dataBaseBookMark.size()];//�����С
    			   temppage=new int[dataBaseBookMark.size()];//��ҳ

    			   for(BookMark dataBookMark:dataBaseBookMark)
    			   {
    				   tempname[i]=dataBookMark.bmname;//��ȡ������ǩ������
    				   temppage[i]=dataBookMark.page;
    				   i++;					   
    			   }
    		   }catch(Exception e)
    		   {
    			   e.printStackTrace();
    		   }
    		   
			   //�Ի����Ӧ���ܴ�ֱ����LinearLayout
       		   	LinearLayout ll=new LinearLayout(ReaderActivity.this);
       			ll.setOrientation(LinearLayout.VERTICAL);		//���ó���	
       			ll.setGravity(Gravity.CENTER_HORIZONTAL);
       			ll.setBackgroundResource(R.drawable.dialog);
       			
       			//�����е�ˮƽLinearLayout
       			LinearLayout lln=new LinearLayout(ReaderActivity.this);
       			lln.setOrientation(LinearLayout.HORIZONTAL);		//���ó���	
       			lln.setGravity(Gravity.CENTER);//����
       			lln.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
       			
       			//�����е�����
       			TextView tvTitle=new TextView(ReaderActivity.this);
       			tvTitle.setText(R.string.bookmark_dialog);
       			tvTitle.setTextSize(20);//���������С
       			tvTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//����������ɫ
       			lln.addView(tvTitle);
       			
       			//����������ӵ���LinearLayout
       			ll.addView(lln);		
       		   	
       		   	//Ϊ�Ի����е���ʷ��¼��Ŀ����ListView
       		    //��ʼ��ListView
       	        ListView lv=new ListView(this);
       	        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
       	        
       	        //ΪListView׼������������
       	        BaseAdapter ba=new BaseAdapter()
       	        {
       				@Override
       				public int getCount() {
       					return tempname.length;//�ܹ�����ѡ��
       				}

       				@Override
       				public Object getItem(int arg0) { return null; }

       				@Override
       				public long getItemId(int arg0) { return 0; }

       				@Override
       				public View getView(int arg0, View arg1, ViewGroup arg2) {
       					
       					LinearLayout llb=new LinearLayout(ReaderActivity.this);
						llb.setOrientation(LinearLayout.HORIZONTAL);//���ó���	
						llb.setPadding(5,5,5,5);//������������
       					
       					//Ϊ��ǩ���ͼƬ
       					ImageView image=new ImageView(ReaderActivity.this);
       					image.setImageDrawable(ReaderActivity.this.getResources().getDrawable(R.drawable.sl_bookmark));
       					image.setScaleType(ImageView.ScaleType.FIT_XY);//����ԭͼ����
       					image.setLayoutParams(new Gallery.LayoutParams(30, 30));
       					llb.addView(image);
       					
       					//��̬����ÿ����ǩ��Ӧ��TextView
       					TextView tv=new TextView(ReaderActivity.this);
       					tv.setGravity(Gravity.LEFT);
       					tv.setText(tempname[arg0]+"     "+"��"+String.valueOf(temppage[arg0]+1)+"ҳ");//��������
       					tv.setTextSize(20);//���������С
       					tv.setTextColor(ReaderActivity.this.getResources().getColor(R.color.white));//����������ɫ
       					tv.setPadding(0,0,0,0);//������������
       					llb.addView(tv);
       					return llb;
       				}        	
       	        };       
       	        lv.setAdapter(ba);//ΪListView��������������
       	        
       	        //����ѡ������ļ�����
       	        lv.setOnItemClickListener(
       	           new OnItemClickListener()
       	           {
       				@Override
       				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
       						long arg3) {//��дѡ������¼��Ĵ�����
   					
   						int page=temppage[arg2];//�õ�������¼��Ӧ��ҳ��
   						readerView.currRR=readerView.currBook.get(page);//��hashMap���ҵ���Ӧ��readerView.currRR����
   						Constant.CURRENT_LEFT_START=readerView.currRR.start;//��¼��ǰ������leftstart��ֵ
   						Constant.CURRENT_PAGE=readerView.currRR.pageNo;//��¼��ǰ��������page��ֵ
   						
   						//������������ͼƬ
   						//readerView.currRR.isLeft=true;
   						readerView.bmLeft=readerView.drawPage(readerView.currRR);
   						//readerView.bmRight=readerView.drawPage(readerView.currRR);
   						readerView.repaint();
       					
       					dialog.cancel();//�رնԻ���
       				}        	   
       	           }
       	        );
       	        //����ʷ��¼����ListView������LinearLayout
	       	    ll.addView(lv);

	       	    lv.setOnItemLongClickListener(
	       	    		new OnItemLongClickListener()
	       	    		{
							@Override
							public boolean onItemLongClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								//���ݵ�ǰ����ǩ�����֣��ҵ���Ӧ����ǩ��ҳ��,ɾ��������¼
								deleteOneBookMarkName=tempname[arg2];
								try
								{
									showDialog(DELETE_ONE_BOOKMARK);
									
								}catch(Exception e)
								{
									e.printStackTrace();
								}
								dialog.cancel();//�رնԻ���
					
								return true;
							}
	       	    		}
	       	    );
	       	     dialog.setContentView(ll); 
    		   break;
    	   case EXIT_READER://�˳��Ի���
    		   
    		   //�Ի����Ӧ���ܴ�ֱ����LinearLayout
      		   	LinearLayout lle=new LinearLayout(ReaderActivity.this);
      			lle.setOrientation(LinearLayout.VERTICAL);		//���ó���	
      			lle.setGravity(Gravity.CENTER_HORIZONTAL);
      			lle.setBackgroundResource(R.drawable.dialog);
      			
      			//�����е�ˮƽLinearLayout
      			LinearLayout llt=new LinearLayout(ReaderActivity.this);
      			llt.setOrientation(LinearLayout.HORIZONTAL);		//���ó���	
      			llt.setGravity(Gravity.CENTER);//����
      			llt.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
      			
      			//�����е�����
      			TextView eTitle=new TextView(ReaderActivity.this);
      			eTitle.setText(R.string.exit_bookmark);
      			eTitle.setTextSize(20);//���������С
      			eTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//����������ɫ
      			llt.addView(eTitle);
      			
      			//����������ӵ���LinearLayout
      			lle.addView(llt);
      			
      			LinearLayout lleb=new LinearLayout(ReaderActivity.this);
      			lleb.setOrientation(LinearLayout.HORIZONTAL);//ˮƽ����
      			lleb.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
      			lleb.setGravity(Gravity.CENTER);

	       	    //ȷ����ť
	       	    Button eok=new Button(ReaderActivity.this);//�����ǩ��ť
	       	    eok.setText(R.string.ok);//���á���ť��������
	       	    eok.setTextSize(18);//���������С
	       	    eok.setWidth(100);
	       	    eok.setHeight(20);
	       	    eok.setGravity(Gravity.CENTER);
	       	    eok.setOnClickListener(
	       	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {
								dialog.cancel();//ȡ���Ի���
								savePreference();//��ǰ��ҳ�˳�ʱ,�����ֳ�
								saveCurrentData();//��ǰhashMap����Ϣ�������ݿ�
								System.exit(0);	
							} 
	       	    		 } 
	       	     );
	       	    lleb.addView(eok);//���뵽linearLayout��
	       	     //ȡ����ť
	       	    Button eCancel=new Button(ReaderActivity.this);
	       	    eCancel.setText(R.string.cancel);//���ð�ť������
	       	    eCancel.setTextSize(18);
	       	    eCancel.setWidth(100);
	       	    eCancel.setHeight(20);
	       	    eCancel.setGravity(Gravity.CENTER);
	            eCancel.setOnClickListener(
	      	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {
								dialog.cancel();//ȡ���Ի���
							}
	       	    		 }
	       	     );
	       	    lleb.addView(eCancel);
	       	    lle.addView(lleb);
	       	    dialog.setContentView(lle);
    		   break;
    	   case DELETE_ONE_BOOKMARK://ɾ��һ����ǩ��¼�Ի���
    		   
    		   //�Ի����Ӧ���ܴ�ֱ����LinearLayout
     		   	LinearLayout lld=new LinearLayout(ReaderActivity.this);
     			lld.setOrientation(LinearLayout.VERTICAL);		//���ó���	
     			lld.setGravity(Gravity.CENTER_HORIZONTAL);
     			lld.setBackgroundResource(R.drawable.dialog);
     			
     			//�����е�ˮƽLinearLayout
     			LinearLayout lldt=new LinearLayout(ReaderActivity.this);
     			lldt.setOrientation(LinearLayout.HORIZONTAL);		//���ó���	
     			lldt.setGravity(Gravity.CENTER);//����
     			lldt.setLayoutParams(new ViewGroup.LayoutParams(240, LayoutParams.WRAP_CONTENT));
     			
     			//�����е�����
     			TextView deTitle=new TextView(ReaderActivity.this);
     			deTitle.setText(R.string.delete_onebookmark);
     			deTitle.setTextSize(20);//���������С
     			deTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//����������ɫ
     			lldt.addView(deTitle);
     			
     			//����������ӵ���LinearLayout
     			lld.addView(lldt);
     			
     			LinearLayout lldeb=new LinearLayout(ReaderActivity.this);
     			lldeb.setOrientation(LinearLayout.HORIZONTAL);//ˮƽ����
     			lldeb.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
     			lldeb.setGravity(Gravity.CENTER);

	       	    //ȷ����ť
	       	    Button deok=new Button(ReaderActivity.this);//�����ǩ��ť
	       	    deok.setText(R.string.ok);//���á���ť��������
	       	    deok.setTextSize(18);//���������С
	       	    deok.setWidth(100);
	       	    deok.setHeight(20);
	       	    deok.setGravity(Gravity.CENTER);
	       	    deok.setOnClickListener(
	       	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {	
								try
								{//���ݿ���ɾ��һ����ǩ��¼
									SQLDBUtil.deleteOneBookMark(deleteOneBookMarkName);
								}catch(Exception e)
								{
									e.printStackTrace();
								}
								dialog.cancel();//ȡ���Ի���
							} 
	       	    		 } 
	       	     );
	       	    lldeb.addView(deok);//���뵽linearLayout��
	       	     //ȡ����ť
	       	    Button deCancel=new Button(ReaderActivity.this);
	       	    deCancel.setText(R.string.cancel);//���ð�ť������
	       	    deCancel.setTextSize(18);
	       	    deCancel.setWidth(100);
	       	    deCancel.setHeight(20);
	       	    deCancel.setGravity(Gravity.CENTER);
	            deCancel.setOnClickListener(
	      	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {
								dialog.cancel();//ȡ���Ի���
								
								showDialog(SELECT_BOOKMARK);//��ʾѡ����ǩ�ĶԻ���
									
							}
	       	    		 }
	       	     );
	            lldeb.addView(deCancel);
	       	    lld.addView(lldeb);
	       	    dialog.setContentView(lld);
   		   break;
    	   case DELETE_ALL_BOOKMARK:
    		   
    		 //�Ի����Ӧ���ܴ�ֱ����LinearLayout
    		   	LinearLayout lla=new LinearLayout(ReaderActivity.this);
    			lla.setOrientation(LinearLayout.VERTICAL);		//���ó���	
    			lla.setGravity(Gravity.CENTER_HORIZONTAL);
    			lla.setBackgroundResource(R.drawable.dialog);
    			
    			//�����е�ˮƽLinearLayout
    			LinearLayout llat=new LinearLayout(ReaderActivity.this);
    			llat.setOrientation(LinearLayout.HORIZONTAL);		//���ó���	
    			llat.setGravity(Gravity.CENTER);//����
    			llat.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
    			
    			//�����е�����
    			TextView deaTitle=new TextView(ReaderActivity.this);
    			deaTitle.setText(R.string.delete_allbookmark);
    			deaTitle.setTextSize(20);//���������С
    			deaTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//����������ɫ
    			llat.addView(deaTitle);
    			
    			//����������ӵ���LinearLayout
    			lla.addView(llat);
    			
    			LinearLayout lldeab=new LinearLayout(ReaderActivity.this);
    			lldeab.setOrientation(LinearLayout.HORIZONTAL);//ˮƽ����
    			lldeab.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
    			lldeab.setGravity(Gravity.CENTER);

	       	    //ȷ����ť
	       	    Button deaok=new Button(ReaderActivity.this);//�����ǩ��ť
	       	    deaok.setText(R.string.ok);//���á���ť��������
	       	    deaok.setTextSize(18);//���������С
	       	    deaok.setWidth(100);
	       	    deaok.setHeight(20);
	       	    deaok.setGravity(Gravity.CENTER);
	       	    deaok.setOnClickListener(
	       	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {	
						
								try
								{//��յ�ǰ�Ȿ���ȫ����ǩ
									SQLDBUtil.deleteAllBookMark(Constant.FILE_PATH);
								}catch(Exception e)
								{
									e.printStackTrace();
								}

								dialog.cancel();//ȡ���Ի���
							} 
	       	    		 } 
	       	     );
	       	    lldeab.addView(deaok);//���뵽linearLayout��
	       	     //ȡ����ť
	       	    Button deaCancel=new Button(ReaderActivity.this);
	       	    deaCancel.setText(R.string.cancel);//���ð�ť������
	       	    deaCancel.setTextSize(18);
	       	    deaCancel.setWidth(100);
	       	    deaCancel.setHeight(20);
	       	    deaCancel.setGravity(Gravity.CENTER);
	            deaCancel.setOnClickListener(
	      	    		 new OnClickListener()
	       	    		 {
							public void onClick(View v) {
								dialog.cancel();//ȡ���Ի���
							}
	       	    		 }
	       	     );
	            lldeab.addView(deaCancel);
	       	    lla.addView(lldeab);
	       	    dialog.setContentView(lla);
    		   break;
    	   case BACKGROUND_MUSIC://���ֱ������ֶԻ���
    		   setBackgroundMusicDialog(dialog);
    		   break;
    	   case BACKGROUND_PIC://����ͼƬ
    		   setBackgroundPic(dialog);
    		   break;
    	   case SET_FONT_SIZE://�Լ����������С�Ի���
    		   setFontSize(dialog);//����setFontSize������ʾ���ֵ�dialog
    		   break;
    	   case SET_FONT_COLOR://�����������ɫ
    		   setFontColor(dialog);//�����������ɫ
    		   break;
    		   
    	}
    	
    }

	//������Ŀ
	public void searchBook()
	{

		goToSearchBooklist();
		
		lv=(ListView)ReaderActivity.this.findViewById(R.id.flist);//��ȡListView�ؼ�����
		root_b=(Button)findViewById(R.id.Button01);
	    back_b=(Button)findViewById(R.id.Button02);
		final File[] files=lvutills.getFiles(sdcardPath);//��ȡ���ڵ��ļ��б� 
		lvutills.intoListView(files,lv);//�������ļ���ӵ�ListView�б���
		root_b.setOnClickListener(
				//OnClickListenerΪView���ڲ��ӿڣ���ʵ���߸������������¼�
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
								"�Ѿ�����Ŀ¼��", 
								Toast.LENGTH_SHORT
							).show();
						}else
						{
								File cf=new File(lvutills.currentPath);//��ȡ��ǰ�ļ��б��·����Ӧ���ļ�
								cf=cf.getParentFile();//��ȡ��Ŀ¼�ļ�
								lvutills.currentPath=cf.getPath();//��¼��ǰ�ļ��б�·��
								lvutills.intoListView(lvutills.getFiles(lvutills.currentPath),lv);	
						}
						
					}});
	}

	public void setBackgroundMusicDialog(final Dialog dialog)
	{
		//�Ի����Ӧ���ܴ�ֱ����LinearLayout
	   	LinearLayout ll=new LinearLayout(ReaderActivity.this);
		ll.setOrientation(LinearLayout.VERTICAL);		//���ó���	
		ll.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.setBackgroundResource(R.drawable.dialog);
		
		//�����е�ˮƽLinearLayout
		LinearLayout lln=new LinearLayout(ReaderActivity.this);
		lln.setOrientation(LinearLayout.HORIZONTAL);		//���ó���	
		lln.setGravity(Gravity.CENTER);//����
		lln.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
		
		//�����е�����
		TextView tvTitle=new TextView(ReaderActivity.this);
		tvTitle.setText(R.string.music_name);
		tvTitle.setTextSize(20);//���������С
		tvTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//����������ɫ
		lln.addView(tvTitle);
		
		//����������ӵ���LinearLayout
		ll.addView(lln);		
	   	
	   	//Ϊ�Ի����е���ʷ��¼��Ŀ����ListView
	    //��ʼ��ListView
        ListView lv=new ListView(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
        
        //ΪListView׼������������
        BaseAdapter ba=new BaseAdapter()
        {
			@Override
			public int getCount() {
				return musicname.length;//�ܹ�����ѡ��
			}

			@Override
			public Object getItem(int arg0) { return null; }

			@Override
			public long getItemId(int arg0) { return 0; }

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				
			LinearLayout llb=new LinearLayout(ReaderActivity.this);
			llb.setOrientation(LinearLayout.HORIZONTAL);//���ó���	
			llb.setPadding(5,5,5,5);//������������
				
				//Ϊ��ǩ���ͼƬ
				ImageView image=new ImageView(ReaderActivity.this);
				image.setImageDrawable(ReaderActivity.this.getResources().getDrawable(R.drawable.sl_music));//�趨ͼƬ
				image.setScaleType(ImageView.ScaleType.FIT_XY);//����ԭͼ����
				image.setLayoutParams(new Gallery.LayoutParams(30, 30));
				llb.addView(image);
				
				//��̬����ÿ����ǩ��Ӧ��TextView
				TextView tv=new TextView(ReaderActivity.this);
				tv.setGravity(Gravity.LEFT);
				tv.setText(ReaderActivity.this.getResources().getString(musicname[arg0]));//��������
				tv.setTextSize(20);//���������С
				tv.setTextColor(ReaderActivity.this.getResources().getColor(R.color.white));//����������ɫ
				tv.setPadding(12,0,0,0);//������������
				llb.addView(tv);
				return llb;
			}        	
        };       
        lv.setAdapter(ba);//ΪListView��������������
        
        //����ѡ������ļ�����
        lv.setOnItemClickListener(
           new OnItemClickListener()
           {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {//��дѡ������¼��Ĵ�����
				//���ݵ�ǰ����ǩ�����֣��ҵ���Ӧ����ǩ��ҳ�ţ�����ҳ��ȷ���л�����һҳ

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
				dialog.cancel();//�رնԻ���

				//��ʼ������ǰ�ļ���Xҳ
				readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

				//������������ͼƬ
				readerView.bmLeft=readerView.drawPage(readerView.currRR);
				//readerView.bmRight=readerView.drawPage(readerView.currRR);
				readerView.repaint();
			}        	   
           }
        );
        //����ʷ��¼����ListView������LinearLayout
        ll.addView(lv);
        dialog.setContentView(ll); 	
		
	}
	//����������ɫ
	public void setFontSize(final Dialog dialog)
	{
		//�Ի����Ӧ���ܴ�ֱ����LinearLayout
	   	LinearLayout ll=new LinearLayout(ReaderActivity.this);
		ll.setOrientation(LinearLayout.VERTICAL);		//���ó���	
		ll.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.setBackgroundResource(R.drawable.dialog);
		
		//�����е�ˮƽLinearLayout
		LinearLayout lln=new LinearLayout(ReaderActivity.this);
		lln.setOrientation(LinearLayout.HORIZONTAL);		//���ó���	
		lln.setGravity(Gravity.CENTER);//����
		lln.setLayoutParams(new ViewGroup.LayoutParams(200, LayoutParams.WRAP_CONTENT));
		
		//�����е�����
		TextView tvTitle=new TextView(ReaderActivity.this);
		tvTitle.setText(R.string.font_size);
		tvTitle.setTextSize(20);//���������С
		tvTitle.setTextColor(ReaderActivity .this.getResources().getColor(R.color.white));//����������ɫ
		lln.addView(tvTitle);
		
		//����������ӵ���LinearLayout
		ll.addView(lln);		
	   	
	   	//Ϊ�Ի����е���ʷ��¼��Ŀ����ListView
	    //��ʼ��ListView
        ListView lv=new ListView(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
        
        //ΪListView׼������������
        BaseAdapter ba=new BaseAdapter()
        {
			@Override
			public int getCount() {
				return fontSizeIds.length;//�ܹ�����ѡ��
			}

			@Override
			public Object getItem(int arg0) { return null; }

			@Override
			public long getItemId(int arg0) { return 0; }

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				
			LinearLayout llb=new LinearLayout(ReaderActivity.this);
			llb.setOrientation(LinearLayout.HORIZONTAL);//���ó���	
			llb.setPadding(5,5,5,5);//������������
				
				//Ϊ��ǩ���ͼƬ
				ImageView image=new ImageView(ReaderActivity.this);
				image.setImageDrawable(ReaderActivity.this.getResources().getDrawable(fontSizeDrawable[arg0]));//�趨ͼƬ
				image.setScaleType(ImageView.ScaleType.FIT_XY);//����ԭͼ����
				image.setLayoutParams(new Gallery.LayoutParams(30, 30));
				llb.addView(image);
				
				//��̬����ÿ����ǩ��Ӧ��TextView
				TextView tv=new TextView(ReaderActivity.this);
				tv.setGravity(Gravity.LEFT);
				tv.setText(ReaderActivity.this.getResources().getString(fontSizeIds[arg0]));//��������
				tv.setTextSize(20);//���������С
				tv.setTextColor(ReaderActivity.this.getResources().getColor(R.color.white));//����������ɫ
				tv.setPadding(0,0,0,0);//������������
				llb.addView(tv);
				return llb;
			}        	
        };       
        lv.setAdapter(ba);//ΪListView��������������
        
        //����ѡ������ļ�����
        lv.setOnItemClickListener(
           new OnItemClickListener()
           {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {//��дѡ������¼��Ĵ�����
				
				switch(arg2)
				{
					case 0:
						if(Constant.TEXT_SIZE!=16)//�����ǰ�����С������Ҫ�����������С
						{
							//�任�����С
							Constant.TEXT_SIZE=16;
							Constant.TEXT_SPACE_BETWEEN_CN=16;
							Constant.TEXT_SPACE_BETWEEN_EN=8;							
							//���������е�����Ӧ��Ļ�ı������¸�ֵ
					        Constant.changeRatio();
							
					        updataBookMarkAndHashMap();//����hashMap						
						}else//������
						{
							//�����任
						}
						
						break;
					case 1:
						if(Constant.TEXT_SIZE!=24)//�����ǰ�����С������Ҫ�����������С
						{
							//�任�����С
							Constant.TEXT_SIZE=24;
							Constant.TEXT_SPACE_BETWEEN_CN=24;
							Constant.TEXT_SPACE_BETWEEN_EN=12;						
							//���������е�����Ӧ��Ļ�ı������¸�ֵ
					        Constant.changeRatio();
							
					        updataBookMarkAndHashMap();//����hashMap						
						}else//������
						{
							//�����任
						}
						break;
					case 2:
						if(Constant.TEXT_SIZE!=32)//�����ǰ�����С������Ҫ�����������С
						{
							//�任�����С
							Constant.TEXT_SIZE=32;
							Constant.TEXT_SPACE_BETWEEN_CN=32;
							Constant.TEXT_SPACE_BETWEEN_EN=16;
							
							//���������е�����Ӧ��Ļ�ı������¸�ֵ
					        Constant.changeRatio();
							
					        updataBookMarkAndHashMap();//����hashMap
					
						}else//������
						{
							//�����任
						}
						break;
				}
				dialog.cancel();//�رնԻ���


				//��ʼ������ǰ�ļ���Xҳ
				readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

				//������������ͼƬ
				readerView.bmLeft=readerView.drawPage(readerView.currRR);
				//readerView.bmRight=readerView.drawPage(readerView.currRR);
				readerView.repaint();
			}        	   
           }
        );
        //����ʷ��¼����ListView������LinearLayout
        ll.addView(lv);
        dialog.setContentView(ll); 
	
	}
	
	//����ͼƬ
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
					
					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//����Ӧ��Ļ�ı���ͼƬ
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);
					
					//��ʼ������ǰ�ļ���Xҳ
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);
					//������������ͼƬ
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
					
					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//����Ӧ��Ļ�ı���ͼƬ
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);
					
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//������������ͼƬ
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
					//�رնԻ���
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
					
					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//����Ӧ��Ļ�ı���ͼƬ
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);
					
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//������������ͼƬ
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
					
					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//����Ӧ��Ļ�ı���ͼƬ
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);
					
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//������������ͼƬ
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
					//�رնԻ���
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
					
					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//����Ӧ��Ļ�ı���ͼƬ
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);
					
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//������������ͼƬ
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

					readerView.bmBack=PicLoadUtil.LoadBitmap(readerView.getResources(), BITMAP);//����Ӧ��Ļ�ı���ͼƬ
					readerView.bmBack=PicLoadUtil.scaleToFit(readerView.bmBack, PAGE_WIDTH, PAGE_HEIGHT);

					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//������������ͼƬ
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
					//�رնԻ���
					dialog.cancel();					
				}        	  
	          }
	        );   	
	}
	
	
	
	//����������ɫ
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
					//��ʼ������ǰ�ļ���Xҳ
					readerView.currRR=new ReadRecord(CURRENT_LEFT_START,CURRENT_PAGE);

					//������������ͼƬ
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

					//������������ͼƬ
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();
					
					//�رնԻ���
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

					//������������ͼƬ
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

					//������������ͼƬ
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();

					//�رնԻ���
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

					//������������ͼƬ
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

					//������������ͼƬ
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();

					//�رնԻ���
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

					//������������ͼƬ
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

					//������������ͼƬ
					readerView.bmLeft=readerView.drawPage(readerView.currRR);
					//readerView.bmRight=readerView.drawPage(readerView.currRR);
					readerView.repaint();

					//�رնԻ���
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
							long arg3) {//��дѡ������¼��Ĵ�����
						String s=dl.txtName[arg2*2+1];
						dl.downFile(Constant.ADD_PRE+s,"/",s);
					} });
	}
	//��Handler������Ϣ�ķ���
    public void sendMessage(int what)
    {
    	Message msg = myHandler.obtainMessage(what); 
    	myHandler.sendMessage(msg);
    }  
    //�����������ת�ķ���
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
	public void goToWellcomView()//����"���ɿƼ�"����
	{
		if(wellcomView==null)
    	{
			wellcomView=new WellcomeSurfaceView(this);
    	}
		setContentView(wellcomView);
		curr=WhichView.WELLCOM_VIEW;
	}
	public void goToHiddenBar()//��ʾhaddenBar
	{

		
		setContentView(R.layout.hidden_bar);//guningyi add ����hiddenBar
		nextChapter = (Button)findViewById(R.id.nextChapter);
		prevChapter = (Button)findViewById(R.id.prevChapter);
		hiddenBar = (LinearLayout)findViewById(R.id.hiddenBar);
		
		
		
	}
	
	public void isWhichTime()//�ж��ǵڼ��δ����
	{
		sp=this.getSharedPreferences("read", Context.MODE_PRIVATE);//��ΪģʽΪ˽��ģʽ
		
		String isOneTime=sp.getString("isOneTime", null);//�ж��ǲ��ǵ�һ�δ����
        String lastTimePath=sp.getString("path", null);//�õ���һ�η��ʵ����·��
        String lastTimePage=sp.getString("page", null);//�õ���һ�η��������ҳ
        String lastTimeName=sp.getString("name", null);//�õ���һ�η����������
        String lastTimeFontSize=sp.getString("fontsize", null);//�õ���һ�η�����������С
        if(lastTimePath==null)//�����û��ѡ����(������˵�������˳�����͵�һ�δ����)
        {
        	Constant.FILE_PATH=null;//��ǰ·��Ϊ��
        	Constant.CURRENT_LEFT_START=0;//��ǰ��ҳ���Ϸ�������ֵΪ0
        	Constant.CURRENT_PAGE=0;//��ǰ��ҳΪ0	
        	
        	if(isOneTime==null)//����ǵ�һ�δ����
        	{//ʹ��Ĭ�������С���޶���
        	}else//���������˵�������˳������
        	{
        		//ȷ�������С
        		Constant.TEXT_SIZE=Integer.parseInt(lastTimeFontSize);//�õ���һ����˵������������С
        		Constant.TEXT_SPACE_BETWEEN_CN=Constant.TEXT_SIZE;
        		Constant.TEXT_SPACE_BETWEEN_EN=Constant.TEXT_SIZE/2;
        		//���������еĸ����������¸�ֵ
                Constant.changeRatio();//��������Ӧ��Ļ�ķ���
        	}        	
        	
        }else//���򣬻�ȡ��һ�δ���ġ�·�����롰ҳ����
        {
        	//ȷ������Ĵ�С
        	Constant.TEXT_SIZE=Integer.parseInt(lastTimeFontSize);//�õ���һ�������С
    		Constant.TEXT_SPACE_BETWEEN_CN=Constant.TEXT_SIZE;
    		Constant.TEXT_SPACE_BETWEEN_EN=Constant.TEXT_SIZE/2;
    		//���������еĸ����������¸�ֵ
            Constant.changeRatio();//��������Ӧ��Ļ�ķ���
        	//*****************************ȷ�������С����******************************************
        	Constant.TEXTNAME=lastTimeName;//��ȡ��һ�δ��ļ�������
        	
        	Constant.FILE_PATH=lastTimePath;//��ȡ��һ�δ��ļ���·��
        	
        	//����getCharacterCount�����������µ��ַ����ȣ���ֹ�������һҳ���Լ������¶���
        	Constant.CONTENTCOUNT=TextLoadUtil.getCharacterCount(Constant.FILE_PATH);  
        	
        	
        	int page=Integer.parseInt(lastTimePage);//�õ����ҳ����ת��Ϊint��
        	try{
        		//���ݵ�ǰ·���������ݿ��еĶ�ӦhashMap��byte������
        		byte[] data=SQLDBUtil.selectRecordData(Constant.FILE_PATH);
        		//ΪreaderView�е�hashMap
        		readerView.currBook=SQLDBUtil.fromBytesToListRowNodeList(data);//��byte������ת��ΪhashMap�͵�����      		
        		readerView.currRR=readerView.currBook.get(page);//����hashMap������ȡ��ReadRecord�Ķ��󣨼�¼��ǰ��ҳ�����ϵ�������
        		Constant.CURRENT_LEFT_START=readerView.currRR.start;//Ϊ��ǰ��ҳ����������ֵ
        		Constant.CURRENT_PAGE=readerView.currRR.pageNo;//Ϊ��ǰ��ҳ��page��ֵ      		
        	}catch(Exception e)
        	{
        		e.printStackTrace();
        	}        	
        }	
	}
	//������������¼�
	public void saveCurrentData()//�˳�����ͻ���ʱ��Ҫ�����ݿ�
	{
		if(Constant.FILE_PATH==null)//����ǵ�һ�δ����(��˵�������˳����)
		{
			//û�ж���
		}else//����ǵ�n��ѡ��
		{
			try
			{
				byte[] data=SQLDBUtil.fromListRowNodeListToBytes(readerView.currBook);//hashMapת��Ϊbyte
				SQLDBUtil.recordInsert(Constant.FILE_PATH,data);//����ǰ��·����hashMap��byte��ʽ�������ݿ�
				//��ǰ�����ҳ��Ϣ�������ݿ⣬�����´δ�ʱ���ǵ�ǰҳ
				SQLDBUtil.lastTimeInsert(Constant.FILE_PATH, Constant.CURRENT_PAGE,Constant.TEXT_SIZE);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public void savePreference()//��ǰ��ҳ�˳�ʱ������˳���ť�������ֳ�Preference
	{
		SharedPreferences.Editor editor=sp.edit();//�༭SharedPreferences
		if(Constant.FILE_PATH==null)//�������˵�������˳�
		{
			//û�ж�������
		}else//������ڶ�������˳��������ֳ�
		{
			//��ǰ���ļ�·�����ļ���page����preference
			int page=readerView.currRR.pageNo;//��ǰҳ��
			editor.putString("path", Constant.FILE_PATH);//��ǰ·������SharedPreferences
			editor.putString("page", String.valueOf(page));//����ǰҳ������preference(ת��ΪString��)
			editor.putString("name",Constant.TEXTNAME);//����ǰ������ַ���preference
		}
		editor.putString("isOneTime", "is");
		editor.putString("fontsize", String.valueOf(Constant.TEXT_SIZE));//�ѵ�ǰ�������preference
		editor.commit();//�ύ
		
	}
	//������仯��,������ǩ��HashMap�д�����ݵķ���
	public void updataBookMarkAndHashMap()
	{
		try
		{//�ж����ݿ����Ƿ���ڵ�ǰ�Ȿ�����ǩ
			haveBookMark=SQLDBUtil.judgeHaveBookMark(Constant.FILE_PATH);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(haveBookMark)//���������ǩ
		{
			/*
			 * ���������ǩ���ȸ�����ǩ�������ݿ���ȡ��������ǩ��ҳ�������֣��Ӷ��õ����е�left_start��ֵ����һ��������
			 * ����hashMap����ǩ�е�left_start����ֵ��Ȼ�����hashMap�����»��Ƶ����ڶ�����λ��
			 */
			
			String[] nameBookMark=null;//��ʱ�����ǩ������
			int[] pageBookMark = null;//��ʱ�����ǩ��ҳ��
			int[] leftStart=null;//��ʱ���ÿ����ǩ��Ӧ��leftStart
		   try
		   {
			   //�����ݿ���ȡ�����е���ǩ��¼
			   dataBaseBookMark=SQLDBUtil.getBookmarkList(Constant.FILE_PATH);				   
			   int i=0;
			   nameBookMark=new String[dataBaseBookMark.size()];//�����С
			   pageBookMark=new int[dataBaseBookMark.size()];//��ҳ
			   leftStart=new int[dataBaseBookMark.size()];//��Ӧ��leftStart

			   for(BookMark bm:dataBaseBookMark)
			   {
				   nameBookMark[i]=bm.bmname;//��ȡ������ǩ������
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
			   readerView.currRR=readerView.currBook.get(pageBookMark[m]);//��hashMap���ҵ���Ӧ��readerView.currRR����
			   leftStart[m]=readerView.currRR.start;//��ȡleftstart��ֵ  
		   }
		   int tempLeftStart=Constant.CURRENT_LEFT_START;//����ǰ��left_start��¼����
		   //���»��ƽ��棬���Ҵ���ǩ
		   for(int i=0;i<dataBaseBookMark.size();i++)
		   {			   
			   	Constant.CURRENT_LEFT_START=0;//��ΪҪ�ӵ�һҳ��ʼ������� ����ֵҪ����
		   		Constant.CURRENT_PAGE=0;
		   		Constant.nextPageStart=0;
		   		Constant.nextPageNo=0;
		   		
		   		readerView.currBook.clear();//���hashMap
		   		int m=i;
		   		while(Constant.CURRENT_LEFT_START<leftStart[m])
		   		{
		   			readerView.currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
		   			
		   			Constant.CURRENT_LEFT_START=readerView.currRR.start;//��¼��ǰ������leftstart��ֵ
		   			Constant.CURRENT_PAGE=readerView.currRR.pageNo;//��¼��ǰ��������page��ֵ

		   			readerView.currBook.put(readerView.currRR.pageNo, readerView.currRR);//��ǰҳ����Ϣ����hashMap
		   			
		   			//readerView.currRR.isLeft=true;
		   			readerView.drawVirtualPage(readerView.currRR);//�����������ҳ
		   			//readerView.drawVirtualPage(readerView.currRR);//�����ұ�����ҳ	
		   		}
		   		//��bookMark�д�����º������
		   		SQLDBUtil.bookMarkInsert(nameBookMark[m],Constant.CURRENT_PAGE);
		   }
		   //����hashMap�����»��Ƶ��������ڶ��Ľ���
		   
		   Constant.CURRENT_LEFT_START=0;//��ΪҪ�ӵ�һҳ��ʼ������� ����ֵҪ����
		   Constant.CURRENT_PAGE=0;
		   Constant.nextPageStart=0;
		   Constant.nextPageNo=0;
		   
		   readerView.currRR=new ReadRecord(0,0);
		   readerView.currBook.put(0, readerView.currRR);//����һҳ����hashMap��
		   
		   while(Constant.CURRENT_LEFT_START<tempLeftStart)
		   {
			   readerView.currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
	
			   Constant.CURRENT_LEFT_START=readerView.currRR.start;//��¼��ǰ������leftstart��ֵ
			   Constant.CURRENT_PAGE=readerView.currRR.pageNo;//��¼��ǰ��������page��ֵ

			   readerView.currBook.put(readerView.currRR.pageNo, readerView.currRR);//��ǰҳ����Ϣ����hashMap
	
			   //readerView.currRR.isLeft=true;
			   readerView.drawVirtualPage(readerView.currRR);//�����������ҳ
			   //readerView.drawVirtualPage(readerView.currRR);//�����ұ�����ҳ	
		   }	   	
		}else//�����������ǩ��ֻ���µ�ǰҳ����hashMap
		//���򣬸��ݵ�ǰҳ��Left_Start����
	   	{
	   		int tempLeftStart=Constant.CURRENT_LEFT_START;//����ǰ��left_start��¼����

	   		Constant.CURRENT_LEFT_START=0;//��ΪҪ�ӵ�һҳ��ʼ������� ����ֵҪ����
	   		Constant.CURRENT_PAGE=0;
	   		Constant.nextPageStart=0;
	   		Constant.nextPageNo=0;
	   		
	   		readerView.currBook.clear();//���hashMap
	   		
	   		readerView.currRR=new ReadRecord(0,0);
			readerView.currBook.put(0, readerView.currRR);//����һҳ����hashMap��
	   		
	   		while(Constant.CURRENT_LEFT_START<tempLeftStart)
	   		{
	   			readerView.currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
	   			
	   			Constant.CURRENT_LEFT_START=readerView.currRR.start;//��¼��ǰ������leftstart��ֵ
	   			Constant.CURRENT_PAGE=readerView.currRR.pageNo;//��¼��ǰ��������page��ֵ

	   			readerView.currBook.put(readerView.currRR.pageNo, readerView.currRR);//��ǰҳ����Ϣ����hashMap
	   			
	   			//readerView.currRR.isLeft=true;
	   			readerView.drawVirtualPage(readerView.currRR);//�����������ҳ
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