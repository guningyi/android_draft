package com.example.ui_list_view;

import com.example.ui_list_view.MainActivity;
import com.example.ui_list_view.View1;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button goToView1 = null;
	private Button goToView2 = null;
	private Button goToView3 = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		goToView1 = (Button)findViewById(R.id.goToView1);
		
		goToView1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
						
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, View1.class);
			startActivity(intent);
			finish();
			}
		});
		
		goToView2 = (Button)findViewById(R.id.goToView2);
		goToView2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
						
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, View2.class);
			startActivity(intent);
			finish();
			}
		});
		
		goToView3 = (Button)findViewById(R.id.goToView3);
		goToView3.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
						
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, View3.class);
			startActivity(intent);
			finish();
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
