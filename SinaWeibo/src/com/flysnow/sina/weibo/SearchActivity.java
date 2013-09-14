/**
 * 
 */
package com.flysnow.sina.weibo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 鎼滅储Activity
 * @author 椋為洩鏃犳儏
 * @since 2011-3-8
 */
public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView=new TextView(this);
		textView.setText("杩欐槸鎼滅储锛�");
		setContentView(textView);
	}
	
}
