package com.example.adnroid_client_2;





import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
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
	    		String strUrl = "http://10.0.2.2/php_server_1/communicate2.php";
	    		URL url = null;	
	    		try{
	    			url = new URL(strUrl);
	    			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
	    			urlConn.setDoInput(true);
	    			urlConn.setDoOutput(true);
	    			urlConn.setRequestMethod("POST");
	    			urlConn.setUseCaches(false);
	    			urlConn.setRequestProperty("Content-Type", "application/octet-stream");
	    			urlConn.setRequestProperty("Connection", "Keep-Alive");
	    			urlConn.addRequestProperty("Charset", "utf-8");
	    			
	    			String message = URLEncoder.encode("周六去踢球", "utf-8");
	    			urlConn.setRequestProperty("message",message);
	    			urlConn.connect();
	    			int responseCode = urlConn.getResponseCode();
	    			if (HttpURLConnection.HTTP_OK==responseCode) 
	    			{	    			    
		    			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));			
		    			String readLine = null;
		    			while((readLine = bufferReader.readLine()) != null)
		    			{
		    				result += readLine;
		    			}
		    			
		    			bufferReader.close();
		    			urlConn.disconnect();
	    			
	    			}
	    			
	    		}catch(MalformedURLException e){
	    			e.printStackTrace();
	    			return result;
	    		}catch (IOException e){
	    			e.printStackTrace();
	    			return result;
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


}
