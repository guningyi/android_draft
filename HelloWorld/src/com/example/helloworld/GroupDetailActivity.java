package com.example.helloworld;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupDetailActivity extends Activity {

	private TextView title = null;
	private RelativeLayout layout = null;
	private TextView location = null;
	private TextView description = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addButton();
		setContentView(layout);
		String titleStr = this.getIntent().getStringExtra("title");
		title = (TextView)findViewById(R.id.detail_group_title);
		title.setText(titleStr);
		showGroupInfo(titleStr);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_detail, menu);
		return true;
	}
	
	private void addButton()
	{
		LayoutInflater inflater = getLayoutInflater();
		layout = (RelativeLayout)inflater.inflate(R.layout.activity_group_detail, null);
		Button bt1 = new Button(this);
        Button bt2 = new Button(this);
        RelativeLayout.LayoutParams bt1params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bt1params.topMargin = 140;
        bt1.setLayoutParams(bt1params);
        bt1.setText("Add");
        RelativeLayout.LayoutParams bt2params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bt2params.leftMargin = 40;
        bt2params.topMargin = 140;
        bt2.setLayoutParams(bt2params);
        bt2.setText("Back");
        bt2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent();
				intent.setClass(GroupDetailActivity.this, GroupListActivity.class);
				startActivity(intent);
				finish();
			}
			
		});
        layout.addView(bt1);
        layout.addView(bt2);
	}

	private void showGroupInfo(String title)
	{
		location = (TextView)findViewById(R.id.detail_group_location);
		description = (TextView)findViewById(R.id.detail_group_description);
		GroupDB groupDbConnector = new GroupDB(GroupDetailActivity.this);
		SQLiteDatabase db;
		db = groupDbConnector.getReadableDatabase();
		String SELECT_TABLE = "SELECT location,description from activity_group where title = '"+ title +"'";
		Cursor cursor = db.rawQuery(SELECT_TABLE, null);
		int rows_num = cursor.getCount();
		if (rows_num == 0) {
		// Cannot login
			String errorMessage = "Sorry, you haven't have any group yet";
		}
		else{
			if(rows_num != 0) {
				  cursor.moveToFirst();
				  for(int i=0; i<rows_num; i++) {
				   //String strCr = cursor.getString(0);
				   location.setText("Location:  " + cursor.getString(0));
				   description.setText(cursor.getString(1));
				   //Toast toast = Toast.makeText(GroupDetailActivity.this, "I'm here", Toast.LENGTH_LONG);
					//toast.setGravity(Gravity.CENTER, 0, 0);
					//toast.show();
				    
				   cursor.moveToNext();
				  }
		     }
		}
		cursor.close();
	}

}
