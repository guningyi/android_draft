package com.example.helloworld;


import android.view.Menu;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;


public class GroupPhotoActivity extends Activity {

	private Button  Back = null;
	private Gallery photo = null; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_photo);
		
		Back = (Button)findViewById(R.id.button1);
		photo = (Gallery)findViewById(R.id.gallery1);
		
		photo.setAdapter(new ImageAdapter(this));
		
		
		Back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				
				Intent intent = new Intent();
				intent.setClass(GroupPhotoActivity.this, MyGroupActivity.class);
				startActivity(intent);
				finish();
			}
			
		});
		
		
		//Gallery func
		photo.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id){
				

			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_photo, menu);
		return true;
	}

}

