package com.example.ebook_v1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;
import android.widget.Toast;


public class TextLoadUtil 
{	
	//guningyi add:判断编码格式并自动进行转码的函数
	public static String readFragment(int begin,int len, String str_filepath) 
	{ // 转码                 
	   File file = new File(str_filepath);               
	   BufferedReader reader;                 
	   String text = "";                 
	   try {                      
	           FileInputStream fis = new FileInputStream(file);
	           BufferedInputStream in = new BufferedInputStream(fis); 
	           in.mark(4); 
	           byte[] first3bytes = new byte[3]; 
	           in.read(first3bytes);//找到文档的前三个字节并自动判断文档类型。 
	           in.reset(); 
	           if (first3bytes[0] == (byte)0xEF 
		              && first3bytes[1] == (byte)0xBB 
	                  && first3bytes[2] == (byte) 0xBF) 
	           {// utf-8 
	        	   //Log.e("guningyi->","utf-8");
	               reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
	           }
	           else if (first3bytes[0] == (byte)0xFF
	          	           && first3bytes[1] == (byte)0xFE)
	           {
	        	   //Log.e("guningyi->","unicode");
	               reader = new BufferedReader(new InputStreamReader(in, "unicode")); 
	           }
	           else if(first3bytes[0] == (byte)0xFE
		                   && first3bytes[1] == (byte)0xFF) 
	           {
	        	   //Log.e("guningyi->","utf-16be");
	               reader = new BufferedReader(new InputStreamReader(in, "utf-16be"));                         
	           }
	           else if(first3bytes[0] == (byte)0xFF
	           	           && first3bytes[1] == (byte)0xFF)
	           {
	        	   //Log.e("guningyi->","utf-16le");
	               reader = new BufferedReader(new InputStreamReader(in, "utf-16le"));                         
	           }
	           else
	           {
	        	   //Log.e("guningyi->","GBK");
	               reader = new BufferedReader(new InputStreamReader(in, "GBK"));
	           } 
	           
	           reader.skip(begin);
			   char[] ca=new char[len];//读取并放入ca[]中
			   reader.read(ca);
			   text=new String(ca);
			   text=text.replaceAll("\\r\\n","\n");
	           reader.close(); 
	        }catch(FileNotFoundException e){ 
	        //TODO Auto-generated catch block                         
		        e.printStackTrace();
		    }catch(IOException e){
		    	e.printStackTrace();
		    } 
	        return text;
	} 


	
	//从指定的开始位置加载指定长度的内容字符串
	public static String readFragment_backup(int begin,int len,String path)
	{
		String result=null;
		
		try
		{ 
			FileReader fr=new FileReader(path);	
			BufferedReader br=new BufferedReader(fr);
			br.skip(begin);
			char[] ca=new char[len];//读取并放入ca[]中
			br.read(ca);
			result=new String(ca);
			result=result.replaceAll("\\r\\n","\n");
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static int getCharacterCount(String path)
	{
		int count = 0;
		try
		{
			FileReader fl = new FileReader(path);
			BufferedReader bf = new BufferedReader(fl);
			String content = null;
			while((content = bf.readLine()) != null)
			{
				count += content.length();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}
	
	public static String loadFromSDFile(ScrollBar readerView,int begin,int len,String fname)//读取APK中文件的工具类!!!!!!!!!!!!!!!!!!!!
	    {
	    	String result=null;    	
	    	try
	    	{
	    		InputStream in=readerView.getResources().getAssets().open(fname);
	    		InputStreamReader isr=new InputStreamReader(in); //将字节流转化为字符流
	    		BufferedReader br=new BufferedReader(isr);//用一个处理流增加一些功能
	    		br.skip(begin);
				char[] ca=new char[len];
				br.read(ca);
				result=new String(ca);
				result=result.replaceAll("\\r\\n","\n");
	    	}
	    	catch(Exception e)
	    	{
	    		//Toast.makeText(readerView.getContext(), "对不起，没有找到指定文件！", Toast.LENGTH_SHORT).show();
	    	}    	
	    	return result;
	    }
	 
	 public static int getCharacterCountApk(ScrollBar readerView,String fname)
		{
			int count = 0;
			try
			{
				InputStream in=readerView.getResources().getAssets().open(fname);
		    		InputStreamReader isr=new InputStreamReader(in); //将字节流转化为字符流
		    		BufferedReader br=new BufferedReader(isr);//用一个处理流增加一些功能
					String content = null;
					while((content = br.readLine()) != null)
					{
						count += content.length();
					}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
				return count;
		}
}
