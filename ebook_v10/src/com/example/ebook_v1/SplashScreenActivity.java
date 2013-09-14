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
		//��������һ��splashҳ��
		new Handler().postDelayed(new Runnable(){ // Ϊ�˼��ٴ���ʹ������Handler����һ����ʱ�ĵ���
			    public void run() {  
			          Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);   
			          //ͨ��Intent������������������Main���Activity
			          SplashScreenActivity.this.startActivity(i);    //����Main����
			          SplashScreenActivity.this.finish();    //�ر��Լ����������
			        }  
			    }, 5000);   //5�룬�����˰�
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}