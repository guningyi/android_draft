package com.example.ui_list_view;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class View2 extends Activity {

	private Button goToMain = null;
	private Button goToView1 = null;
	private Button hideLayout = null;
	private LinearLayout buttonBox1 = null;
	private boolean hideLayout_flag = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view2);
		
		goToMain = (Button)findViewById(R.id.goToMain);
		goToView1 = (Button)findViewById(R.id.goToView1);
		hideLayout = (Button)findViewById(R.id.hideLayout);
		buttonBox1 = (LinearLayout)findViewById(R.id.buttonBox1);
		
		
		goToMain.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
						
			Intent intent = new Intent();
			intent.setClass(View2.this, MainActivity.class);
			startActivity(intent);
			finish();
			}
		});
		
		goToView1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
						
			Intent intent = new Intent();
			intent.setClass(View2.this, View1.class);
			startActivity(intent);
			finish();
			}
		});
		
		hideLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
						
		        if (hideLayout_flag == false){
		            buttonBox1.setVisibility(View.GONE);//隐藏linearlayout
		            hideLayout_flag = true;
		            hideLayout.setText("show");//改变按钮上的文字
		        }
		        else
		        {
		        	buttonBox1.getBackground().setAlpha(100);//guningyi 调试半透明效果
		        	buttonBox1.setVisibility(View.VISIBLE);//显示linearlayout
		        	hideLayout_flag = false;
		        	hideLayout.setText("hidden");//改变按钮上的文字
		        }
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view2, menu);
		return true;
	}

}
