package com.example.helloworld;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MyGroupActivity extends Activity {

	private Button toSearch = null;
	private Button addGroup = null;
	private Button showAll = null;
	private Button showPic = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_group);
		toSearch = (Button)findViewById(R.id.button1);
		addGroup = (Button)findViewById(R.id.button2);
		showAll = (Button)findViewById(R.id.button3);
		showPic = (Button)findViewById(R.id.button4);
		
		//To search page
		toSearch.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//debug.setText("Login");
				
				Intent intent = new Intent();
				intent.setClass(MyGroupActivity.this, SearchActivity.class);
				startActivity(intent);
				finish();
			}
			
		});
		
		showAll.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent();
				intent.setClass(MyGroupActivity.this, GroupListActivity.class);
				startActivity(intent);
				finish();
				
			}
			
		});
		addGroup.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent();
				intent.setClass(MyGroupActivity.this, AddGroupActivity.class);
				startActivity(intent);
				finish();
			}
			
		});
		
		showPic.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent();
				intent.setClass(MyGroupActivity.this, GroupPhotoActivity.class);
				startActivity(intent);
				finish();
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_group, menu);
		return true;
		
	}

}
