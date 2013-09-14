package com.bn.reader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;    

public class DownLoad
{
	String []txtName;//存放目录列表中的名字
	String listName;//所读取目录的内容字符串
	ListView lv;//声明ListView的引用
	ReaderActivity ra;//Activity的引用
	
	public DownLoad(String path,ReaderActivity ra)//用来设置下载列表
	{
		this.ra=ra;//拿到Activity 的引用
		ra.goToBackground();
		lv=(ListView)ra.findViewById(R.id.background);
		listName=getTxtInfo(path);
		initListView(listName);//初始化文本目录列表
		
	}
	public String getTxtInfo(String name)
	{
		String result=null;
		try
		{
			URL url=new URL(Constant.ADD_PRE+name);//获取连接URL	
			URLConnection uc=url.openConnection();//开启连接			
			//HttpURLConnection uc = (HttpURLConnection)url.openConnection();
			
			/*Log.i("string getTxtInfo", "I am here");
			Log.i("net", "length =" + uc.getContentLength());
			Log.i("net", "respcode =" + uc.getResponseCode());
			Log.i("net", "ContentType =" + uc.getContentType());
			Log.i("net", "content =" + uc.getContent());*/
			
			InputStream is=uc.getInputStream();//通过连接打开输入流
			int ch=0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();//开启字节数组输出流
			
			while((ch=is.read())!=-1)//在读取结束之前，将每次读取的结果存入输出流中
		    {
		      	baos.write(ch);
		    }      
		    byte[] bb=baos.toByteArray();//将输出流的内容导入到bb字节数组中
		    baos.close();//关闭输出流
		    is.close();//关闭输入流 
		    result=new String(bb,"UTF-8");//将字节数组中的内容按照"UTF-8"编码
		    result=result.replaceAll("\\r\\n","\n");//并用换行符替换回车符
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	public void initListView(String s)
	{
		txtName=s.split("\\|");//给文件名字数组赋值
		final int i=txtName.length/2;
		
		BaseAdapter ba=new BaseAdapter()//创建适配器
		{
			@Override
			public int getCount() {
				return i;
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
        				
				//初始化LinearLayout
				LinearLayout ll=new LinearLayout(DownLoad.this.ra);//创建LinearLayout
				ll.setOrientation(LinearLayout.HORIZONTAL);//设置朝向	
				ll.setPadding(5,5,5,5);//设置四周留白

				//初始化ImageView
				ImageView  ii=new ImageView(DownLoad.this.ra);//创建ImageView
				ii.setImageDrawable(ra.getResources().getDrawable(R.drawable.sl_txt));//设置图片
				ii.setScaleType(ImageView.ScaleType.FIT_XY);//按照原图比例
				ii.setLayoutParams(new Gallery.LayoutParams(60,60));//Image的大小设置
				ll.addView(ii);//添加到LinearLayout中
						
				//初始化TextView
				TextView tv=new TextView(DownLoad.this.ra);//创建TextView
				tv.setText(txtName[arg0*2]);//添加文件名称
				tv.setTextSize(18);//设置字体大小
				tv.setTextColor(DownLoad.this.ra.getResources().getColor(R.color.white));//设置字体颜色
				tv.setPadding(5,5,5,5);//设置四周留白
				tv.setGravity(Gravity.RIGHT);
				ll.addView(tv);//添加到LinearLayout中
				
				return ll;
			}        	
		};
		lv.setAdapter(ba);//设置适配器
	
	}
	public void downFile(String urlStr,String path,String fileName){
		InputStream inputStream=null;//输入流引用
		FileUtils fileUtils=new FileUtils(ra);//创建文件下载工具类  
		try {
			if(fileUtils.isFileExist(path+fileName)){//如果存在就不再下载
				Toast.makeText
				(
						ra, 
						"已经存在该文件无需下载"
						,Toast.LENGTH_SHORT 
				).show();
			}else{
				inputStream=fileUtils.getInputStreamFromUrl(urlStr);//通过连接获取输入流
				File resultFile=fileUtils.writeToSDFromInput(path, fileName, inputStream);//把输入流中的文件写入SD卡
				try{
				inputStream.close();//完成后关闭输入流
				}catch(IOException e){
					e.printStackTrace();
				}
				if(resultFile==null){
					Toast.makeText//如果文本为空提示信息
					(
							ra, 
							"该文件为空"
							,Toast.LENGTH_SHORT 
					).show();
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}	
	}
}