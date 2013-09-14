package com.example.helloworld;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;;

public class MainActivity extends Activity {

	private Button register = null;
	private Button login = null;
	private TextView debug = null;
	private EditText userName = null;
	private EditText password = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		register = (Button)findViewById(R.id.button2);
		login = (Button)findViewById(R.id.button1);
		userName = (EditText)findViewById(R.id.editText1);
		password = (EditText)findViewById(R.id.editText2);
		
		debug = (TextView)findViewById(R.id.textView1);
		
		//Register
		register.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//debug.setText("Register");
				
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, RegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		//Login
		login.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//debug.setText("Login");
				
				// Check if the user can login
				UserDB userDbConnector = new UserDB(MainActivity.this);
				SQLiteDatabase db;
				db = userDbConnector.getReadableDatabase();
				byte[] bytesOfMessage = null;
				try {
					bytesOfMessage = password.getText().toString().getBytes("UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MessageDigest md = null;
				try {
					md = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] thedigest = md.digest(bytesOfMessage);
				
				String passwordMD5 = thedigest.toString();
				
				String SELECT_TABLE = "SELECT * from user where username = '" + userName.getText().toString()+"' and password = '" + password.getText().toString() +"'";
				//String SELECT_TABLE = "SELECT * from user where username = 'admin'";
				Cursor cursor = db.rawQuery(SELECT_TABLE, null);
				int rows_num = cursor.getCount();
				if (rows_num == 0) {
				// Cannot login
					String errorMessage = "Sorry, you're invalid to login" + passwordMD5;
					Toast toast = Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				else{
					if(rows_num != 0) {
						  cursor.moveToFirst();
						  for(int i=0; i<rows_num; i++) {
						   String strCr = cursor.getString(0);
						   Toast toast = Toast.makeText(MainActivity.this, "I'm here", Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						    
						   cursor.moveToNext();
						  }
						 }
						 cursor.close();
					
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, MyGroupActivity.class);
					startActivity(intent);
					finish();
					
				}
				

			}
			
		}); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
