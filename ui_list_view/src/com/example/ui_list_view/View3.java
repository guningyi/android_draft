package com.example.ui_list_view;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class View3 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view3, menu);
		return true;
	}

}
