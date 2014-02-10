package com.jj.drag;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		DragListView.OnRefreshLoadingMoreListener {
	private DragListView dlv_main;// ����ListView
	private MyAdapter adapter;

	private final static int DRAG_INDEX = 1;// ����ˢ�±�ʶ
	
	private final static int LOADMORE_INDEX = 2;// ���ظ����ʶ

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		dlv_main = (DragListView) findViewById(R.id.dlv_main);
		adapter = new MyAdapter(this);
		dlv_main.setAdapter(adapter);
		dlv_main.setOnRefreshListener(this);

	}

	/***
	 * 
	 * @author jia
	 */

	class MyAdapter extends BaseAdapter {
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
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.item, null);
				holder.textView = (TextView) convertView
						.findViewById(R.id.tv_tripline_content_item_1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			return convertView;
		}
	}

	class ViewHolder {
		public TextView textView;
		public TextView textView2;
	}

	/***
	 * ִ���� �첽
	 * 
	 * @author zhangjia
	 * 
	 */
	class MyAsyncTask extends AsyncTask<Void, Void, Void> {
		private Context context;
		private int index;// �����ж�������ˢ�»��ǵ�����ظ���

		public MyAsyncTask(Context context, int index) {
			this.context = context;
			this.index = index;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (index == DRAG_INDEX)
				dlv_main.onRefreshComplete();
			else if (index == LOADMORE_INDEX)
				dlv_main.onLoadMoreComplete(false);
		}

	}

	/***
	 * ����ˢ��
	 */
	@Override
	public void onRefresh() {
		new MyAsyncTask(this, DRAG_INDEX).execute();
	}

	/***
	 * ������ظ���
	 */
	@Override
	public void onLoadMore() {
		new MyAsyncTask(this, LOADMORE_INDEX).execute();
	}

}