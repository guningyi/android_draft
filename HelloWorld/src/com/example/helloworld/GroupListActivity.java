package com.example.helloworld;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupListActivity extends Activity implements OnItemClickListener{

	private ListView listView;
	private Button back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listView = new ListView(this);
		
		//listView  = (ListView)findViewById(R.layout.activity_group_list);
		listView.setClickable(true);
		listView.setOnItemClickListener(this);
		listView.setAdapter(new ArrayAdapter<String>(this, R.layout.activity_group_list_item, getData()));
		setContentView(listView);
		/*
		back = (Button)findViewById(R.id.back);
		
		back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//debug.setText("Login");
				
				Intent intent = new Intent();
				intent.setClass(GroupListActivity.this, MyGroupActivity.class);
				startActivity(intent);
				finish();
			}
		});
		*/
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_list, menu);
		return true;
	}
	
	
	private List<String>getData(){
		
		//Get Data from DB
		List<String>data = new ArrayList<String>();
		/*data.add("title1");
		data.add("title2");
		data.add("title3");*/
		
		GroupDB groupDbConnector = new GroupDB(GroupListActivity.this);
		SQLiteDatabase db;
		db = groupDbConnector.getReadableDatabase();
		
		String SELECT_TABLE = "SELECT id, title, location, description from activity_group";
		Cursor cursor = db.rawQuery(SELECT_TABLE, null);
		int rows_num = cursor.getCount();
		
		/*   Toast toast2 = Toast.makeText(GroupListActivity.this, String.valueOf(rows_num), Toast.LENGTH_LONG);
			toast2.setGravity(Gravity.CENTER, 0, 0);
			toast2.show();
		*/
		if(rows_num != 0) {
			  cursor.moveToFirst();
			  for(int i=0; i<rows_num; i++) {
			   String strCr = cursor.getString(1);
			   data.add(strCr);
			    
			   cursor.moveToNext();
			  }
			 }
			 cursor.close();
		
		return data;
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		TextView listItem = (TextView) v;
	    //TextView clickedItemView = (TextView) listItem.findViewById(R.layout.activity_group_list_item);
	    String clickedItemString = listItem.getText().toString();
	    
	    
		   /*Toast toast2 = Toast.makeText(GroupListActivity.this, clickedItemString, Toast.LENGTH_LONG);
			toast2.setGravity(Gravity.CENTER, 0, 0);
			toast2.show();*/
	    Intent intent = new Intent();
	    intent.putExtra("title", clickedItemString);
		intent.setClass(GroupListActivity.this, GroupDetailActivity.class);
		startActivity(intent);
		finish();
	    
		
	}

}
