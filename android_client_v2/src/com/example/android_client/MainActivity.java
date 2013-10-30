package com.example.android_client;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;


import java.util.ArrayList;
import java.util.List;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    EditText inputName;
    private static String url_up = "http://10.0.2.2/php_server_1/communicate.php";
    private static final String TAG_MESSAGE = "message";


public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputName = (EditText) findViewById(R.id.inputName);
        Button btnCreateProduct = (Button) findViewById(R.id.btnCreateProduct);
        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {
                // creating new product in background thread
                if(validate()){                
                new Up().execute();            
            }
                }
        });
        
}
private boolean validate()
    {
        String description = inputName.getText().toString().trim();
        if (description.equals(""))
        {
            DialogUtil.showDialog(this, "您还没有填写建议", false);
            return false;
        }
        
        return true;
    }
class Up extends AsyncTask<String, String, String> {


        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("验证中..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String name = inputName.getText().toString();
            


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
           


            // getting JSON Object
            // Note that create product url accepts POST method
           try{
            JSONObject json = jsonParser.makeHttpRequest(url_up,
                    "POST", params);
            String message = json.getString(TAG_MESSAGE);
            return message;
           }catch(Exception e){
               e.printStackTrace(); 
               return "";          
           }
           // check for success tag
            
     
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String message) {                    
            pDialog.dismiss();
           //message 为接收doInbackground的返回值
            Toast.makeText(getApplicationContext(), "hehe", 1000).show();
            //server 没有给出反馈，所以这里的显示为空
            Toast.makeText(getApplicationContext(), message, 8000).show();        
            
        }
        }
}

