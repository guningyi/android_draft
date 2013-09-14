package com.example.helloworld;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddGroupActivity extends Activity {

	private Button add = null;
	private EditText title = null;
	private EditText location = null;
	private EditText description = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_group);
		
		add = (Button)findViewById(R.id.button1);
		title = (EditText)findViewById(R.id.editText1);
		location = (EditText)findViewById(R.id.editText2);
		description = (EditText)findViewById(R.id.editText3);
		
		add.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				String debug = title.getText().toString();
				/*Toast toast = Toast.makeText(AddGroupActivity.this, debug, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				
				toast.setText(location.getText().toString());
				toast.show();
				
				toast.setText(description.getText().toString());
				toast.show();*/
				
				// Add Group to database
				
				GroupDB groupDbConnector = new GroupDB(AddGroupActivity.this);
				SQLiteDatabase db;
				db = groupDbConnector.getWritableDatabase();
				
				ContentValues values = new ContentValues();
				values.put("title", title.getText().toString());
				values.put("location", location.getText().toString());
				values.put("description", description.getText().toString());
				db.insert("activity_group", null, values);
				Intent intent = new Intent();
				intent.setClass(AddGroupActivity.this, GroupListActivity.class);
				startActivity(intent);
				finish();
				
				
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_group, menu);
		return true;
	}

}
