package com.example.ebook_v1;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button goToScrollBar = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		goToScrollBar = (Button)findViewById(R.id.goToScrollBar);
		
		goToScrollBar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
						
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ScrollBar.class);
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
