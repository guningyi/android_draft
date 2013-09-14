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
	private String SDPATH;//���ڴ�SD�����ļ���·��
	private ReaderActivity ra;
	//��ȡ·��
	public FileUtils(ReaderActivity ra) {
		this.ra=ra;
		SDPATH=Environment.getExternalStorageDirectory()+"/";//��õ�ǰ�ⲿ�洢�豸��Ŀ¼(���ݰ汾��ͬ·����ͬ)
	}
	//�����ļ�
	public File createSDFile(String fileName) throws IOException{
		File file=new File(SDPATH+fileName);
		file.createNewFile();
		return file;
	}
	//�����ļ�Ŀ¼
	public File createSDDir(String dirName){
		File dir=new File(SDPATH+dirName);
		dir.mkdir();
		return dir;
	}
	//�ж��ļ��Ƿ����
	public boolean isFileExist(String fileName){
		File file=new File(SDPATH+fileName);
		return file.exists();
	}
	// ��inputStream���ļ�д��SD�� 
	//path ��Ŀ¼��  fileName �����ļ��� inputStream ͨ��URL��ȡ��������
	public File writeToSDFromInput(String path,String fileName,InputStream inputStream){
		File file=null;
		OutputStream output=null;
		try {
			file=createSDFile(path+fileName);//����Ŀ¼���ļ�
			output=new FileOutputStream(file);//��������� 
			byte buffer[]=new byte[4*1024];//һ�ζ�ȡ�ļ��ĳ���
			if((inputStream.read(buffer))!=-1){
				output.write(buffer);
			}else{
				Toast.makeText
				(
						ra, 
						"�Ѿ����ڸ��ļ���������"
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
				output.close();//�ر������
				Toast.makeText
				(
						ra, 
						"�������"
						,Toast.LENGTH_SHORT 
				).show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  
		return file;
	}
	public InputStream getInputStreamFromUrl(String urlStr) throws IOException{//��ȡ�����
		 URL url;
		 url=new URL(urlStr);
		 URLConnection urlCon=url.openConnection();
		 InputStream inputStream=urlCon.getInputStream();
		 return inputStream;
  }

}
