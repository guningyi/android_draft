package com.example.ebook_v1;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SettingBook extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_book);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting_book, menu);
		return true;
	}

}
