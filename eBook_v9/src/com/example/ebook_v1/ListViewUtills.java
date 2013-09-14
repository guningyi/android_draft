package com.example.ebook_v1;


import java.io.File;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListViewUtills
{
	String currentPath;//��ǰ·��
	ScrollBar reader;//Activity������	
	Boolean judgeTimes=false;//�ж��Ƿ��ǵ�һ�δ�����
	
	public ListViewUtills(ScrollBar reader)
	{
		this.reader=reader;//�õ�����
	}
	//������ȡ·���µ��ļ�����
	public File[] getFiles(String filePath)
	{
		File[] files=new File(filePath).listFiles();//��ȡ��ǰĿ¼�µ��ļ�    	
		return files;
	}
	//������ �ļ��б����ӵ�ListView��
	public void intoListView(final File[] files,final ListView lv)
	{
		if(files!=null)//���ļ��б���Ϊ��ʱ
		{
			if(files.length==0)//��ǰĿ¼Ϊ��
			{				
				File cf=new File(currentPath);//��ȡ��ǰ�ļ��б���·����Ӧ���ļ�
				cf=cf.getParentFile();//��ȡ��Ŀ¼�ļ�
				currentPath=cf.getPath();//��¼��ǰ�ļ��б�·��
				Toast.makeText
				(
						reader, 
						"���ļ���Ϊ�գ���", 
						Toast.LENGTH_SHORT
				).show();  
			}
			else
			{
				BaseAdapter ba=new BaseAdapter()//����������
				{
					@Override
					public int getCount() {
						return files.length;
					}
					@Override
					public Object getItem(int position) {
						return null;
					}
	
					@Override
					public long getItemId(int position) {				
						return 0;
					}
		        			
					@Override
					public View getView(int arg0, View arg1, ViewGroup arg2) {
		        				
						//��ʼ��LinearLayout
						LinearLayout ll=new LinearLayout(reader);
						ll.setOrientation(LinearLayout.HORIZONTAL);//���ó���	
						ll.setPadding(5,5,5,5);//������������
	
						//��ʼ��ImageView
						ImageView  ii=new ImageView(reader);
						String s=files[arg0].getPath();
						File f=new File(s);//����ļ�����
						char c[]=s.toCharArray();
						int i=s.length();
						if(f.isDirectory())//���ڷ�֧
						{
							ii.setImageDrawable(reader.getResources().getDrawable(R.drawable.sl_file));//����ͼƬ
						}else if(c[i-1]=='t'&&c[i-2]=='x'&&c[i-3]=='t')
						{
							ii.setImageDrawable(reader.getResources().getDrawable(R.drawable.sl_txt));
						}
						else
						{
							ii.setImageDrawable(reader.getResources().getDrawable(R.drawable.sl_else));
						}
						ii.setScaleType(ImageView.ScaleType.FIT_XY);//����ԭͼ����
						ii.setLayoutParams(new Gallery.LayoutParams(60,60));//ͼƬ�Ĵ�С����
						ll.addView(ii);//���ӵ�LinearLayout��
								
						//��ʼ��TextView
						TextView tv=new TextView(reader);
						tv.setText(files[arg0].getName());//�����ļ�����
						tv.setTextSize(18);//���������С
						tv.setTextColor(reader.getResources().getColor(R.color.white));//����������ɫ
						tv.setPadding(5,5,5,5);//������������
						tv.setGravity(Gravity.RIGHT);
						ll.addView(tv);//���ӵ�LinearLayout��				
						return ll;
					}        	
				};
				lv.setAdapter(ba);//����������
		            	
				lv.setOnItemClickListener//����ѡ�в˵��ļ�����
				(
					new OnItemClickListener()
					{
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
					
							File f=new File(files[arg2].getPath());//��õ�ǰ������ļ�����
							if(f.isDirectory())//���ڷ�֧
							{
								currentPath=files[arg2].getPath();//��ȡ·��
								File[] fs=getFiles(currentPath);//��ȡ��ǰ·�����������ļ�
								intoListView(fs,lv);//�����ļ��б�����ListView��
							}
							else 
							{
								//reader.saveCurrentData();//����֮ǰ������Constant.FILE_PATH�仯֮ǰ��������ǰ���ȫ����Ϣ�������ݿ�
								
								String s=files[arg2].getPath();//��ȡ·��
								char c[]=s.toCharArray();
		        				int i=s.length();
		        				if(c[i-1]=='t'&&c[i-2]=='x'&&c[i-3]=='t')
		        				{
		        					
		        					Constant.FILE_PATH=files[arg2].getPath();//��ȡ·��
									Constant.TEXTNAME=files[arg2].getName();//��ȡ�ļ�������
		        					Constant.CONTENTCOUNT=TextLoadUtil.getCharacterCount(Constant.FILE_PATH);//����getCharacterCount����   					
		        					//��ѡ����һ����ʱ�����ݿ��в��ң��Ƿ��Ѿ������Ȿ��ļ�¼
		        					try
		        					{
		        						judgeTimes=SQLDBUtil.judgeIsWhichTime(Constant.FILE_PATH);
		        						Log.v("guningyi->","filename="+Constant.FILE_PATH);
		        						Log.v("guningyi->","judgeTimes="+judgeTimes);
		        						//System.out.println("judgeTimes="+judgeTimes+"####");
		        					}catch(Exception e)
		        					{
		        						e.printStackTrace();
		        					}
		        					
		        					if(judgeTimes)//����ǵ�һ�δ��Ȿ�飬
		        					{
		        						//reader.saveCurrentData();//����֮�󣬵�ǰ��Ϣ�������ݿ�BookRecord�������޷�����ǩ(û�а༶����������ѧ��)
		        						Constant.CURRENT_LEFT_START=0;
			        					Constant.CURRENT_PAGE=0;
			        				    Constant.CURRENT_START = 0;
			        					String x = reader.read_text(reader.currRR);
			        					reader.showContent(x);
			        					//reader.goToReaderView();
		        					}
		        					else//���������ݿ���ȡ������
		        					{
		        						//int x = 0;
		        						//if (x > 1)
		        						{
		        						try
		        						{
		        							//�����ݿ���ȡ��hashMap
		        							byte[] data=SQLDBUtil.selectRecordData(Constant.FILE_PATH);
		        							Log.e("guningyi->","Constant.FILE_PATH="+Constant.FILE_PATH);
		        							
		        							//������һ������
		        							reader.currRR = new ReadRecord(0,0);
		        							//ΪreaderView�е�hashMap
		        							if (reader.currBook == null)
		        							{
		        								Log.e("guningyi->","reader.currBook=null");
		        							}
		        							reader.currBook=SQLDBUtil.fromBytesToListRowNodeList(data);//��byte������ת��ΪhashMap�͵�����
		        							Log.e("guningyi->","reader.currBook="+reader.currBook);
		        							int page=SQLDBUtil.getLastTimePage(Constant.FILE_PATH);//�õ��Ȿ����һ�ζ�����λ��
		        							Log.e("guningyi->","page="+page);
		        							int fontsize=SQLDBUtil.getLastTimeFontSize(Constant.FILE_PATH);//�õ���һ�ε������С
		        							if(fontsize!=Constant.TEXT_SIZE)//����˴δ�����һ�δ�ʱ�������С��ͬ
		        							{
		        								//����汾��֧���޸������С
		        								//reader.updataBookMarkAndHashMap();//������ǩ������hashMap
		        								
		        							}
                                            //reader.currRR.start = 6001;
                                            //reader.currRR.pageNo = 2;
		        							reader.currRR=reader.currBook.get(page);//����hashMap������ȡ��ReadRecord�Ķ��󣨼�¼��ǰ��ҳ�����ϵ�������	        							
		        			        		if (reader.currRR == null)
		        			        		{
		        			        			Log.e("guningyi->","is null");
		        			        		}
		        							Constant.CURRENT_LEFT_START=reader.currRR.start;//Ϊ��ǰ��ҳ����������ֵ
		        			        		Constant.CURRENT_PAGE=reader.currRR.pageNo;//Ϊ��ǰ��ҳ��page��ֵ     
		        			        		String x = reader.read_text(reader.currRR);
				        					reader.showContent(x);
		        			        		
		        						}catch(Exception e)
		        						{
		        							e.printStackTrace();
		        						}
		        						}//end of x>1
		        						
			        					//reader.goToReaderView();
		        					}
		        				}else
		        				{
		        					Toast.makeText
		        					(
		        						reader, 
		        						"���.txt�ļ�", 
		        						Toast.LENGTH_SHORT
		        					).show();
		        				}
		        			}        						
						}
					}
				);
			}    
		}
		else
		{
			File cf=new File(currentPath);//��ȡ��ǰ�ļ��б���·����Ӧ���ļ�
			cf=cf.getParentFile();//��ȡ��Ŀ¼�ļ�
			currentPath=cf.getPath();//��¼��ǰ�ļ��б�·��
			Toast.makeText
			(
				reader, 
				"�Ѿ�����Ŀ¼��", 
				Toast.LENGTH_SHORT
			).show();
		}
	}
}