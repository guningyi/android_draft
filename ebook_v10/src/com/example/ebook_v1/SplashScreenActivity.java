package com.example.ebook_v1;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SplashScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		//这里添加一个splash页面
		new Handler().postDelayed(new Runnable(){ // 为了减少代码使用匿名Handler创建一个延时的调用
			    public void run() {  
			          Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);   
			          //通过Intent打开最终真正的主界面Main这个Activity
			          SplashScreenActivity.this.startActivity(i);    //启动Main界面
			          SplashScreenActivity.this.finish();    //关闭自己这个开场屏
			        }  
			    }, 5000);   //5秒，够用了吧
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
