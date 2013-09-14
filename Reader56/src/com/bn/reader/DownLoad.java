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
	String []txtName;//���Ŀ¼�б��е�����
	String listName;//����ȡĿ¼�������ַ���
	ListView lv;//����ListView������
	ReaderActivity ra;//Activity������
	
	public DownLoad(String path,ReaderActivity ra)//�������������б�
	{
		this.ra=ra;//�õ�Activity ������
		ra.goToBackground();
		lv=(ListView)ra.findViewById(R.id.background);
		listName=getTxtInfo(path);
		initListView(listName);//��ʼ���ı�Ŀ¼�б�
		
	}
	public String getTxtInfo(String name)
	{
		String result=null;
		try
		{
			URL url=new URL(Constant.ADD_PRE+name);//��ȡ����URL	
			URLConnection uc=url.openConnection();//��������			
			//HttpURLConnection uc = (HttpURLConnection)url.openConnection();
			
			/*Log.i("string getTxtInfo", "I am here");
			Log.i("net", "length =" + uc.getContentLength());
			Log.i("net", "respcode =" + uc.getResponseCode());
			Log.i("net", "ContentType =" + uc.getContentType());
			Log.i("net", "content =" + uc.getContent());*/
			
			InputStream is=uc.getInputStream();//ͨ�����Ӵ�������
			int ch=0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();//�����ֽ����������
			
			while((ch=is.read())!=-1)//�ڶ�ȡ����֮ǰ����ÿ�ζ�ȡ�Ľ�������������
		    {
		      	baos.write(ch);
		    }      
		    byte[] bb=baos.toByteArray();//������������ݵ��뵽bb�ֽ�������
		    baos.close();//�ر������
		    is.close();//�ر������� 
		    result=new String(bb,"UTF-8");//���ֽ������е����ݰ���"UTF-8"����
		    result=result.replaceAll("\\r\\n","\n");//���û��з��滻�س���
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	public void initListView(String s)
	{
		txtName=s.split("\\|");//���ļ��������鸳ֵ
		final int i=txtName.length/2;
		
		BaseAdapter ba=new BaseAdapter()//����������
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
        				
				//��ʼ��LinearLayout
				LinearLayout ll=new LinearLayout(DownLoad.this.ra);//����LinearLayout
				ll.setOrientation(LinearLayout.HORIZONTAL);//���ó���	
				ll.setPadding(5,5,5,5);//������������

				//��ʼ��ImageView
				ImageView  ii=new ImageView(DownLoad.this.ra);//����ImageView
				ii.setImageDrawable(ra.getResources().getDrawable(R.drawable.sl_txt));//����ͼƬ
				ii.setScaleType(ImageView.ScaleType.FIT_XY);//����ԭͼ����
				ii.setLayoutParams(new Gallery.LayoutParams(60,60));//Image�Ĵ�С����
				ll.addView(ii);//��ӵ�LinearLayout��
						
				//��ʼ��TextView
				TextView tv=new TextView(DownLoad.this.ra);//����TextView
				tv.setText(txtName[arg0*2]);//����ļ�����
				tv.setTextSize(18);//���������С
				tv.setTextColor(DownLoad.this.ra.getResources().getColor(R.color.white));//����������ɫ
				tv.setPadding(5,5,5,5);//������������
				tv.setGravity(Gravity.RIGHT);
				ll.addView(tv);//��ӵ�LinearLayout��
				
				return ll;
			}        	
		};
		lv.setAdapter(ba);//����������
	
	}
	public void downFile(String urlStr,String path,String fileName){
		InputStream inputStream=null;//����������
		FileUtils fileUtils=new FileUtils(ra);//�����ļ����ع�����  
		try {
			if(fileUtils.isFileExist(path+fileName)){//������ھͲ�������
				Toast.makeText
				(
						ra, 
						"�Ѿ����ڸ��ļ���������"
						,Toast.LENGTH_SHORT 
				).show();
			}else{
				inputStream=fileUtils.getInputStreamFromUrl(urlStr);//ͨ�����ӻ�ȡ������
				File resultFile=fileUtils.writeToSDFromInput(path, fileName, inputStream);//���������е��ļ�д��SD��
				try{
				inputStream.close();//��ɺ�ر�������
				}catch(IOException e){
					e.printStackTrace();
				}
				if(resultFile==null){
					Toast.makeText//����ı�Ϊ����ʾ��Ϣ
					(
							ra, 
							"���ļ�Ϊ��"
							,Toast.LENGTH_SHORT 
					).show();
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}	
	}
}