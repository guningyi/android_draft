package com.example.ebook_v1;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	//Button相关
	private Button startRead = null;
	private Button searchBook = null;
	private Button setting = null;
	private Button exit = null;
	
	//buttonCotainer相关变量
	private LinearLayout buttonCotainer = null;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private int left = 0;
	private int right = 0;
	private int top = 0;
	private int buttom = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);//加载本activity的布局文件
		
		screenWidth  = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight(); 
		
		buttonCotainer = (LinearLayout)findViewById(R.id.buttonCotainer);
		//动态的获取设备的screen的高和宽，确保虽然是滚动屏幕，但是底部导航栏始终是显示在
		//屏幕的可视范围的底端而不会被遮盖掉
		right = left = (int)(screenWidth *0.1);
		buttom = (int) (screenHeight* 0.0001);
		top = (int) (screenHeight * 0.3);
		buttonCotainer.setPadding(left,top,right,buttom);	
		
		//开始按钮的响应函数
		startRead = (Button)findViewById(R.id.startRead);	
		startRead.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
						
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ScrollBar.class);
			startActivity(intent);
			finish();
			}
		});
		
		setting = (Button)findViewById(R.id.setting);
		setting.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
						
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SettingBook.class);
			startActivity(intent);
			finish();
			}
		});
		
		//退出按钮的响应函数
		exit = (Button)findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
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
