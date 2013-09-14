package com.example.ui_scroll_view_1;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ScrollView;
import android.widget.TextView;

public class ScrollBar extends Activity {

	//ScrollView sc = (ScrollView)findViewById(R.id.scroll_view);
	String content = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_scroll_bar);
		TextView text = (TextView)findViewById(R.id.text); 
		//text.setTextSize(14);
		content = "During the life of an activity, " +
				"the system calls a core set of lifecycle methods " +
				"in a sequence similar to a step pyramid. That is, " +
				"each stage of the activity lifecycle is a separate " +
				"step on the pyramid. As the system creates a new " +
				"activity instance, each callback method moves the " +
				"activity state one step toward the top. The top of " +
				"the pyramid is the point at which the activity is " +
				"running in the foreground and the user can interact " +
				"with it.As the user begins to leave the activity, the" +
				" system calls other methods that move the activity " +
				"state back down the pyramid in order to dismantle the" +
				" activity. In some cases, the activity will move only " +
				"part way down the pyramid and wait (such as when the " +
				"user switches to another app), from which point the " +
				"activity can move back to the top (if the user returns" +
				" to the activity) and resume where the user left off.";
		text.setText(content);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scroll_bar, menu);
		return true;
	}

}
