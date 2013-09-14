/**
 * 
 */
package com.flysnow.sina.weibo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 更多Activity
 * @author 飞雪无情
 * @since 2011-3-8
 */
public class MoreActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView=new TextView(this);
		textView.setText("这是更多�");
		setContentView(textView);
	}
	
}
