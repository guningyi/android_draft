package com.example.adnroid_client_2;





import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import android.app.Activity;
import android.text.InputType;
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
}
