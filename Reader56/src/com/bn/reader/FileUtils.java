package com.bn.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import android.os.Environment;
import android.widget.Toast;

public class FileUtils{
	private String SDPATH;//用于存SD卡的文件的路径
	private ReaderActivity ra;
	//获取路径
	public FileUtils(ReaderActivity ra) {
		this.ra=ra;
		SDPATH=Environment.getExternalStorageDirectory()+"/";//获得当前外部存储设备的目录(根据版本不同路径不同)
	}
	//创建文件
	public File createSDFile(String fileName) throws IOException{
		File file=new File(SDPATH+fileName);
		file.createNewFile();
		return file;
	}
	//创建文件目录
	public File createSDDir(String dirName){
		File dir=new File(SDPATH+dirName);
		dir.mkdir();
		return dir;
	}
	//判断文件是否存在
	public boolean isFileExist(String fileName){
		File file=new File(SDPATH+fileName);
		return file.exists();
	}
	// 将inputStream中文件写入SD卡 
	//path 子目录名  fileName 保存文件名 inputStream 通过URL获取的输入流
	public File writeToSDFromInput(String path,String fileName,InputStream inputStream){
		File file=null;
		OutputStream output=null;
		try {
			file=createSDFile(path+fileName);//创建目录和文件
			output=new FileOutputStream(file);//创建输出流 
			byte buffer[]=new byte[4*1024];//一次读取文件的长度
			if((inputStream.read(buffer))!=-1){
				output.write(buffer);
			}else{
				Toast.makeText
				(
						ra, 
						"已经存在该文件无需下载"
						,Toast.LENGTH_SHORT 
				).show();
			}
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				output.close();//关闭输出流
				Toast.makeText
				(
						ra, 
						"下载完成"
						,Toast.LENGTH_SHORT 
				).show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  
		return file;
	}
	public InputStream getInputStreamFromUrl(String urlStr) throws IOException{//获取输出流
		 URL url;
		 url=new URL(urlStr);
		 URLConnection urlCon=url.openConnection();
		 InputStream inputStream=urlCon.getInputStream();
		 return inputStream;
  }

}
