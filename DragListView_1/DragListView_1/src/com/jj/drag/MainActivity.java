package com.jj.drag;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private DragListView dlv_main;// 下拉ListView
	private MyAdapter adapter;

	private final static int DRAG_INDEX = 1;// 下拉刷新标识

	private final static int LOADMORE_INDEX = 2;// 加载更多标识

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		dlv_main = (DragListView) findViewById(R.id.dlv_main);
		adapter = new MyAdapter(this);
		dlv_main.setAdapter(adapter);

	}

	/***
	 * @author jia
	 */

	static class MyAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			super();
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View convertView_1 = null, convertView_2 = null;
			holder = new ViewHolder();
			if (position % 2 == 0) {
				if (convertView_1 == null) {
					convertView_1 = inflater
							.inflate(R.layout.chat_item_1, null);
					convertView = convertView_1;
				}
			} else {
				if (convertView_2 == null) {
					convertView_2 = inflater
							.inflate(R.layout.chat_item_2, null);
					convertView = convertView_2;
				}
			}

			return convertView;
		}
	}

	static class ViewHolder {
		public ImageView imageView;
		public TextView textView;
		public TextView textView2;
	}

}