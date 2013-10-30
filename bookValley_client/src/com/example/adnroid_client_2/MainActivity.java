package com.example.adnroid_client_2;





import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		Button send = (Button) findViewById(R.id.send);
		send.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	        	new Send().execute();
	          
	            }
	    });
		
		Button register = (Button) findViewById(R.id.register);
		register.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	        	new Register().execute();
	          
	            }
	    });
		
		Button login = (Button) findViewById(R.id.login);
		login.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	        	new Login().execute();
	          
	            }
	    });
		
		Button search = (Button) findViewById(R.id.search);
		search.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	        	new Search().execute();
	          
	            }
	    });
		
		Button download = (Button) findViewById(R.id.download);
		download.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	        	new Download().execute();
	          
	            }
	    });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class Send extends AsyncTask<String, String, String> {
		  
		  String result = "";
		  protected String doInBackground(String... args) {
			  
			   final TextView t = (TextView)findViewById(R.id.result);
			  // creating new product in background thread
	    		String strUrl = "http://10.0.2.2/bookValley_Server/communicate2.php";
	    		URL url = null;
	    		HttpPost httpRequest = new HttpPost(strUrl);
	    		List <NameValuePair> params = new ArrayList<NameValuePair>();
	    		params.add(new BasicNameValuePair("message", "周六去踢球"));
	    		try{
	    			httpRequest.setEntity((HttpEntity) new UrlEncodedFormEntity(params,HTTP.UTF_8));
	    			HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
	    			//若状态码为200 ok 
	    		    if(httpResponse.getStatusLine().getStatusCode()==200){
	    		      //取出回应字串
	    		      result=EntityUtils.toString(httpResponse.getEntity(),"utf-8");
	    		     }else{
	    		      result = "Error Response"+httpResponse.getStatusLine().toString();
	    		     }
	    			 
	    		}catch(ClientProtocolException e){
	    		     
	    		     e.printStackTrace();
	    		    } catch (UnsupportedEncodingException e) {
	    		     
	    		     e.printStackTrace();
	    		    } catch (IOException e) {
	    		     
	    		     e.printStackTrace();
	    		    }
	        return result;
	        }
	
	        protected void onPostExecute(String message) {                    
	           //message 为接收doInbackground的返回值
	            Toast.makeText(getApplicationContext(), "hehe", 1000).show();
	            //server 没有给出反馈，所以这里的显示为空
	            Toast.makeText(getApplicationContext(), result, 8000).show();        
	            
	        }
	
	}


	class Register extends AsyncTask<String, String, String> {
		  
		  String result = "";
		  protected String doInBackground(String... args) {
			  
			    EditText UserName = (EditText)findViewById(R.id.userName);
			    String userName = UserName.getText().toString();
			    
			    EditText PassWord = (EditText)findViewById(R.id.passWord); 
			    String passWord = PassWord.getText().toString();
			    // creating new product in background thread
	    		String strUrl = "http://10.0.2.2/bookValley_Server/register_bookValley.php";
	    		URL url = null;
	    		HttpPost httpRequest = new HttpPost(strUrl);
	    		List <NameValuePair> params = new ArrayList<NameValuePair>();
	    		params.add(new BasicNameValuePair("userName", userName));
	    		params.add(new BasicNameValuePair("passWord", passWord));
	    		//HashMap<String, String> params = new HashMap<String, String>();
	    		//params.put("userName",userName);
	    		//params.put("passWord",passWord);
	    		try{
	    			httpRequest.setEntity((HttpEntity) new UrlEncodedFormEntity(params,HTTP.UTF_8));
	    			HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
	    			//若状态码为200 ok 
	    		    if(httpResponse.getStatusLine().getStatusCode()==200){
	    		        //取出回应字串
	    		        result=EntityUtils.toString(httpResponse.getEntity(),"utf-8");
	    		     }
	    		     else
	    		     {
	    		         result = "Error Response"+httpResponse.getStatusLine().toString();
	    		     }
	    			 
	    		}catch(ClientProtocolException e)
	    		{     
	    		    e.printStackTrace();
	    		} 
	    		catch (UnsupportedEncodingException e)
	    		{	    		     
	    		    e.printStackTrace();
	    		} 
	    		catch (IOException e)
	    		{     
	    		    e.printStackTrace();
	    		}
	    		if (result == "success")
	    		{
	    			result = "注册成功";
	                return result;
	    		}
	    		else if(result == "failed")
	    		{
	    			result = "注册失败";
	                return result;
	    		}
	    		else
	    		{
	    			return result;
	    		}
	    			
	        }
	
	        protected void onPostExecute(String message) {                    
	           //message 为接收doInbackground的返回值
	            Toast.makeText(getApplicationContext(), result, 8000).show();        
	            
	        }
	
	}
	
	class Search extends AsyncTask<String, String, String> {
		  
		  String result = "";
		  protected String doInBackground(String... args) {
			  
			    EditText BookName = (EditText)findViewById(R.id.searchBookName);
			    String bookName = BookName.getText().toString();
			  // creating new product in background thread
	    		String strUrl = "http://10.0.2.2/bookValley_Server/search_bookValley.php";
	    		URL url = null;
	    		HttpPost httpRequest = new HttpPost(strUrl);
	    		List <NameValuePair> params = new ArrayList<NameValuePair>();
	    		params.add(new BasicNameValuePair("bookName", bookName));
	    		try{
	    			httpRequest.setEntity((HttpEntity) new UrlEncodedFormEntity(params,HTTP.UTF_8));
	    			HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
	    			//若状态码为200 ok 
	    		    if(httpResponse.getStatusLine().getStatusCode()==200){
	    		        //取出回应字串
	    		        result=EntityUtils.toString(httpResponse.getEntity(),"utf-8");
	    		     }
	    		     else
	    		     {
	    		         result = "Error Response"+httpResponse.getStatusLine().toString();
	    		     }
	    			 
	    		}catch(ClientProtocolException e)
	    		{     
	    		    e.printStackTrace();
	    		} 
	    		catch (UnsupportedEncodingException e)
	    		{	    		     
	    		    e.printStackTrace();
	    		} 
	    		catch (IOException e)
	    		{     
	    		    e.printStackTrace();
	    		}
	    		/*
	    		if (result == "success")
	    		{
	    			result = "注册成功";
	                return result;
	    		}
	    		else if(result == "failed")
	    		{
	    			result = "注册失败";
	                return result;
	    		}
	    		else
	    		{
	    			return result;
	    		}
	    		*/
	    		return result;
	    			
	        }
	
	        protected void onPostExecute(String message) {                    
	           //message 为接收doInbackground的返回值
	            Toast.makeText(getApplicationContext(), result, 8000).show();        
	            
	        }
	
	}
	
	class Login extends AsyncTask<String, String, String> {
		  
		  String result = "";
		  protected String doInBackground(String... args) {
		
		  //设置密码框的属性，实现遮盖密码
		  int inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
		               | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
		               | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE | InputType.TYPE_TEXT_VARIATION_PASSWORD;
		  
			  
			    EditText UserName = (EditText)findViewById(R.id.userNameLogin);
			    String userName = UserName.getText().toString();
			    
			    EditText PassWord = (EditText)findViewById(R.id.passWordLogin);
			    PassWord.setInputType(inputType);
			    String passWord = PassWord.getText().toString();
			    // creating new product in background thread
	    		String strUrl = "http://10.0.2.2/bookValley_Server/login_bookValley.php";
	    		URL url = null;
	    		HttpPost httpRequest = new HttpPost(strUrl);
	    		List <NameValuePair> params = new ArrayList<NameValuePair>();
	    		params.add(new BasicNameValuePair("userNameLogin", userName));
	    		params.add(new BasicNameValuePair("passWordLogin", passWord));
	    		//HashMap<String, String> params = new HashMap<String, String>();
	    		//params.put("userName",userName);
	    		//params.put("passWord",passWord);
	    		try{
	    			httpRequest.setEntity((HttpEntity) new UrlEncodedFormEntity(params,HTTP.UTF_8));
	    			HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
	    			//若状态码为200 ok 
	    		    if(httpResponse.getStatusLine().getStatusCode()==200){
	    		        //取出回应字串
	    		        result=EntityUtils.toString(httpResponse.getEntity(),"utf-8");
	    		     }
	    		     else
	    		     {
	    		         result = "Error Response"+httpResponse.getStatusLine().toString();
	    		     }
	    			 
	    		}catch(ClientProtocolException e)
	    		{     
	    		    e.printStackTrace();
	    		} 
	    		catch (UnsupportedEncodingException e)
	    		{	    		     
	    		    e.printStackTrace();
	    		} 
	    		catch (IOException e)
	    		{     
	    		    e.printStackTrace();
	    		}
	    		if (result == "success")
	    		{
	    			result = "注册成功";
	                return result;
	    		}
	    		else if(result == "failed")
	    		{
	    			result = "注册失败";
	                return result;
	    		}
	    		else
	    		{
	    			return result;
	    		}
	    			
	        }
	
	        protected void onPostExecute(String message) {                    
	           //message 为接收doInbackground的返回值
	            Toast.makeText(getApplicationContext(), result, 8000).show();        
	            
	        }
	
	}
	
//	class Download extends AsyncTask<String, String, String> {
//		  
//		  String result = ""; 
//		  
//		  protected String doInBackground(String... args) {
//			     
//			    EditText DownloadBookName = (EditText)findViewById(R.id.downloadBookName);
//			    String downloadBookName = DownloadBookName.getText().toString();
//			 
//			
//			    
//			    // creating new product in background thread
//	    		String strUrl = "http://10.0.2.2/bookValley_Server/download_bookValley.php";
//	    		URL url = null;
//	    		HttpPost httpRequest = new HttpPost(strUrl);
//	    		List <NameValuePair> params = new ArrayList<NameValuePair>();
//	    		params.add(new BasicNameValuePair("downloadBookName", downloadBookName));
//	    		
//	    		try{
//	    			httpRequest.setEntity((HttpEntity) new UrlEncodedFormEntity(params,HTTP.UTF_8));
//	    			HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
//	    			//若状态码为200 ok 
//	    		    if(httpResponse.getStatusLine().getStatusCode()==200){
//	    		        //取出回应字串
//	    		        result=EntityUtils.toString(httpResponse.getEntity(),"utf-8");
//	    		        if(result != null)
//	    		        {
//	    		        	String path = Environment.getExternalStorageDirectory() + "/myDownloadBook/";
//	    		        	//创建文件夹
//	    		        	File dir = new File(path);  
//	    		            dir.mkdir();
//	    		            //在文件夹下创建文件
//	    		        	File file = new File(path + downloadBookName); 
//	    		        	//OutputStream output = new FileOutputStream(file);  
//	    		            //byte buffer [] = new byte[4 * 1024];  
//	    		        }
//	    		     }
//	    		     else
//	    		     {
//	    		         result = "Error Response"+httpResponse.getStatusLine().toString();
//	    		     }
//	    			 
//	    		}catch(ClientProtocolException e)
//	    		{     
//	    		    e.printStackTrace();
//	    		} 
//	    		catch (UnsupportedEncodingException e)
//	    		{	    		     
//	    		    e.printStackTrace();
//	    		} 
//	    		catch (IOException e)
//	    		{     
//	    		    e.printStackTrace();
//	    		}  
//
//
//	    	    return result;
//	    		
//	    			
//	        }
//	
//	        protected void onPostExecute(String message) {                    
//	           //message 为接收doInbackground的返回值
//	            Toast.makeText(getApplicationContext(), result, 8000).show();        
//	            
//	        }
//	
//	}
	
	/*
	 * 使用HttpURLConnection进行Post方式提交
	 * 服务端需要check以post方式提交的书名，进行检索。
	 * 这里展现的是提交多个参数的方式
	 * */
	class Download extends AsyncTask<String, String, String> {
		  
		  String result = ""; 
		  InputStream input=null;//用于接收Server传来的数据  
		  String SDPATH = Environment.getExternalStorageDirectory() + "/myDownloadBook/";
		  OutputStream output = null; 
		  
		  protected String doInBackground(String... args) {
			     
			    EditText DownloadBookName = (EditText)findViewById(R.id.downloadBookName);
			    String downloadBookName = DownloadBookName.getText().toString();
			 
			 
			    // creating new product in background thread
	    		String strUrl = "http://10.0.2.2/bookValley_Server/download_bookValley.php";	    		
	    		HttpURLConnection httpurlconnection = null;		
	    		try{
	    			URL url = new URL(strUrl);
		    	    httpurlconnection = (HttpURLConnection)url.openConnection();
		    	    httpurlconnection.setDoOutput(true);
		    	    httpurlconnection.setRequestMethod("POST");
		    	    
		    	    /*
		    	     * 提交多个参数
		    	     * */
		            StringBuffer params = new StringBuffer();
		            params.append("downloadBookName").append("=").append(downloadBookName).append("&")
		                  .append("downloadUserName").append("=").append("guningyi");//写死用户名
		            byte[] bypes = params.toString().getBytes();
		            httpurlconnection.getOutputStream().write(bypes);// 输入参数

		    	    
		            //被注释掉的代码是提交单个参数的方式
		    	    //String bookName="downloadBookName="+downloadBookName;
		    	    //httpurlconnection.getOutputStream().write(bookName.getBytes());
		            
		            
		    	    int code = httpurlconnection.getResponseCode();
		    	    if (code == 200)
		    	    {   
		    	    	try{
		    	    	    input = httpurlconnection.getInputStream(); 
		    	        }catch (MalformedURLException e) {  
		                    e.printStackTrace();  
		                } 
		    	        
	    	            //在sd卡上创建目录
	    	            File dir = new File(SDPATH);  
	                    dir.mkdir();
	                    
                        //判断文件是否存在
	                    File fileTest = new File(SDPATH + downloadBookName);  
	                    if (fileTest.exists())
	                    {
	                    	result = "文件已经存在了！";
	                    	return result;
	                    }
		                
	                    //在sd卡上创建文件
		                File file = new File(SDPATH + downloadBookName);  
		                //若反复抛异常，请先检查permisson 是否打开，再检查真机与模拟器是否遇到同样的问题，
		                //如果只有模拟器有，则可能是没有配置sd卡。
		                file.createNewFile();
		                
		                //开始将读入的文件写到sd上的文件中去
		                output = new FileOutputStream(file);  
		                byte buffer [] = new byte[4 * 1024];  
		                while((input.read(buffer)) != -1)
		                {  
		                    output.write(buffer);  
		                }  
		                output.flush();  
		    	    }
		    	    //System.out.println("code   " + code);
		    	    httpurlconnection.getOutputStream().flush();
		    	    httpurlconnection.getOutputStream().close();
		    	    result = "code"+code;
		    	    
	    			 
	    		}catch(ClientProtocolException e)
	    		{     
	    		    e.printStackTrace();
	    		} 
	    		catch (UnsupportedEncodingException e)
	    		{	    		     
	    		    e.printStackTrace();
	    		} 
	    		catch (IOException e)
	    		{     
	    		    e.printStackTrace();
	    		}  


	    	    return result;
	    		
	    			
	        }
	
	        protected void onPostExecute(String message) {                    
	           //message 为接收doInbackground的返回值
	            Toast.makeText(getApplicationContext(), result, 8000).show();        
	            
	        }
	
	}
	
//	class Download extends AsyncTask<String, String, String> {
//		  
//		  String result = ""; 
//		  
//		  protected String doInBackground(String... args) {
//			     
//			    EditText DownloadBookName = (EditText)findViewById(R.id.downloadBookName);
//			    String downloadBookName = DownloadBookName.getText().toString();		    
//			    // creating new product in background thread
//	    		String strUrl = "http://10.0.2.2/bookValley_Server/bookStore";
//	    		String path = "";
//	    		
//	    		 HttpDownloader httpDownLoader=new HttpDownloader();  
//                 int result1=httpDownLoader.downfile(strUrl, path, downloadBookName);  
//                 if(result1==0)  
//                 {  
//                     result = "下载成功！";
//                 }  
//                 else if(result1==1) 
//                 {  
//                     result = "已有文件！";
//                 }  
//                 else if(result1==-1)
//                 {  
//                     result = "下载失败！";
//                 }   
//	    		 return result;
//	    			
//	        }
//	
//	        protected void onPostExecute(String message) {                    
//	           //message 为接收doInbackground的返回值
//	            Toast.makeText(getApplicationContext(), result, 8000).show();        
//	            
//	        }
//	
//	}
}
