package com.example.helloworld;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.R.id;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RegisterActivity extends Activity {
	
	private Button register2 = null;
	private EditText userName = null;
	private EditText password = null;
	private EditText confirmPassword = null;
	private TextView debug = null;

	
	
	public boolean isUserExisted(String name){
		UserDB userDbConnector = new UserDB(RegisterActivity.this);
		SQLiteDatabase db;
		db = userDbConnector.getReadableDatabase();
		String SELECT_TABLE = "SELECT * FROM user WHERE username = '" + name + "'";
		Cursor cursor = db.rawQuery(SELECT_TABLE, null);
		int rows_num = cursor.getCount();
		if (rows_num == 0) {
			return false;
		}
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		register2 = (Button)findViewById(R.id.button1);
		userName = (EditText)findViewById(R.id.editText1);
		password = (EditText)findViewById(R.id.editText2);
		confirmPassword = (EditText)findViewById(R.id.editText3);
		debug = (TextView)findViewById(android.R.id.text1);

		
/*		show.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				UserDB userDbConnector = new UserDB(RegisterActivity.this);
				SQLiteDatabase db;
				db = userDbConnector.getReadableDatabase();
				String SELECT_TABLE = "SELECT * from user";
				Cursor cursor = db.rawQuery(SELECT_TABLE, null);
				int rows_num = cursor.getCount();
				
				/*Toast toast = Toast.makeText(RegisterActivity.this, "db selected" + String.valueOf(rows_num), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				
			}
			
		});*/
		
		//Trigger SQLITE3
		register2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//debug.setText("Login");	
				

				UserDB userDbConnector = new UserDB(RegisterActivity.this);
				SQLiteDatabase db;
				db = userDbConnector.getWritableDatabase();
				ContentValues values = new ContentValues();
				String passwordMD5 = null;
				
				if (userName.getText().toString() == null || userName.getText().toString().equals("")){
					String errorMessage = "User Name cannot be null";
					Toast toast = Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				
				if (isUserExisted(userName.getText().toString())) {
					String errorMessage = "The user name is already existed";
					Toast toast = Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					
				}
				if (password.getText().toString().equals(confirmPassword.getText().toString())){
					values.put("username", userName.getText().toString());
					byte[] bytesOfMessage = null;
					try {
						bytesOfMessage = password.toString().getBytes("UTF-8");
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
					
					passwordMD5 = thedigest.toString();
					values.put("password", password.getText().toString());
					db.insert("user", null, values);
					Intent intent = new Intent();
					intent.setClass(RegisterActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
					/*Toast toast = Toast.makeText(RegisterActivity.this, passwordMD5, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();*/
				}
				else {
					String errorMessage = "Please make sure your passwords are the same";
					Toast toast = Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					password.setText("");
					confirmPassword.setText("");
					
					
				}
				
				
				

				

			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

}
