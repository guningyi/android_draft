package com.jj.drag;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/***
 * �Զ�������ListView
 * 
 * @author zhangjia
 * 
 */
public class DragListView extends ListView implements OnScrollListener,
		OnClickListener {
	// ����ListViewö������״̬
	private enum DListViewState {
		LV_NORMAL, // ��ͨ״̬
		LV_PULL_REFRESH, // ����״̬��Ϊ����mHeadViewHeight��

	}

	// ������ظ���ö������״̬
	private enum DListViewLoadingMore {
		LV_NORMAL, // ��ͨ״̬
		LV_PULL_REFRESH, // ����״̬��Ϊ����mHeadViewHeight��
	}

	private View mHeadView, mFootView;// ͷ��headView

	private int mHeadViewWidth; // headView�Ŀ�mHeadView��
	private int mHeadViewHeight;// headView�ĸߣ�mHeadView��

	private int mFirstItemIndex = -1;// ��ǰ��ͼ�ܿ����ĵ�һ���������

	private int mLastItemIndex = -1;// ��ǰ��ͼ���Ƿ������һ��.

	// ���ڱ�֤startY��ֵ��һ��������touch�¼���ֻ����¼һ��
	private boolean mIsRecord = false;// �������

	private boolean mIsRecord_B = false;// �������

	private int mStartY, mMoveY;// �����ǵ�y����,moveʱ��y����

	private DListViewState mlistViewState = DListViewState.LV_NORMAL;// ����״̬.(�Զ���ö��)

	private DListViewLoadingMore loadingMoreState = DListViewLoadingMore.LV_NORMAL;// ���ظ���Ĭ��״̬.

	private final static int RATIO = 2;// �������������.

	private boolean isScroller = true;// �Ƿ�����ListView������

	private MyAsynTask myAsynTask;// ����
	private final static int DRAG_UP = 1, DRAG_DOWN = 2;

	public DragListView(Context context) {
		super(context, null);
		initDragListView(context);
	}

	public DragListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDragListView(context);
	}

	/***
	 * ��ʼ��ListView
	 */
	public void initDragListView(Context context) {

		initHeadView(context);// ��ʼ����head.

		initFooterView(context);// ��ʼ��footer

		setOnScrollListener(this);// ListView��������
	}

	/***
	 * ��ʼ��ͷ��HeadView
	 * 
	 * @param context
	 *            ������
	 * @param time
	 *            �ϴθ���ʱ��
	 */
	public void initHeadView(Context context) {
		mHeadView = LayoutInflater.from(context).inflate(R.layout.head, null);
		measureView(mHeadView);
		// ��ȡ��͸�
		mHeadViewWidth = mHeadView.getMeasuredWidth();
		mHeadViewHeight = mHeadView.getMeasuredHeight();

		addHeaderView(mHeadView, null, false);// ����ʼ�õ�ListView add����קListView
		// ����������Ҫ����headView���õ���������ʾλ��.
		mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);

	}

	/***
	 * ��ʼ���ײ����ظ���ؼ�
	 */
	private void initFooterView(Context context) {
		mFootView = LayoutInflater.from(context).inflate(R.layout.head, null);
		addFooterView(mFootView, null, false);// ����ʼ�õ�ListView add����קListView
		// ����������Ҫ����FooterView���õ��ײ�����ʾλ��.
		mFootView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
	}

	/***
	 * ���ã����� headView�Ŀ�͸�.
	 * 
	 * @param child
	 */
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/***
	 * touch �¼�����
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		// ����
		case MotionEvent.ACTION_DOWN:
			doActionDown_B(ev);
			doActionDown(ev);
			break;
		// �ƶ�
		case MotionEvent.ACTION_MOVE:
			doActionMove_B(ev);
			doActionMove(ev);
			break;
		// ̧��
		case MotionEvent.ACTION_UP:
			doActionUp_B(ev);
			doActionUp(ev);
			break;
		default:
			break;
		}

		/***
		 * �����ListView�������������ô����true������ListView�������϶�.
		 * �������ListView����������ô���ø��෽���������Ϳ�������ִ��.
		 */
		if (isScroller) {
			return super.onTouchEvent(ev);
		} else {
			return true;
		}

	}

	/***
	 * ���²���
	 * 
	 * ���ã���ȡ�����ǵ�y����
	 * 
	 * @param event
	 */
	void doActionDown(MotionEvent event) {
		// ����ǵ�һ������һ��touch
		if (mIsRecord == false && mFirstItemIndex == 0) {
			mStartY = (int) event.getY();
			mIsRecord = true;
		}
	}

	/***
	 * ���²��� �ײ�
	 * 
	 * ���ã���ȡ�����ǵ�y����
	 */
	void doActionDown_B(MotionEvent event) {
		// ����ǵ�һ������һ��touch
		if (mIsRecord_B == false && mLastItemIndex == getCount()) {
			mStartY = (int) event.getY();
			mIsRecord_B = true;
		}
	}

	/***
	 * ��ק�ƶ�����
	 * 
	 * @param event
	 */
	void doActionMove(MotionEvent event) {

		// �ж��Ƿ��ǵ�һ�������ֱ�ӷ���
		mMoveY = (int) event.getY();// ��ȡʵʱ����y����

		// ����Ƿ���һ��touch�¼�.
		if (mIsRecord == false && mFirstItemIndex == 0) {
			mStartY = (int) event.getY();
			mIsRecord = true;
		}
		// ֱ�ӷ���˵�����ǵ�һ��
		if (mIsRecord == false)
			return;

		// ������headview�ƶ�����Ϊy�ƶ���һ��.���Ƚ��Ѻã�
		int offset = (mMoveY - mStartY) / RATIO;

		switch (mlistViewState) {
		// ��ͨ״̬
		case LV_NORMAL: {
			// ˵������
			if (offset > 0) {
				// ����headView��padding����.
				mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);
				mlistViewState = DListViewState.LV_PULL_REFRESH;// ����״̬
			}
		}
			break;
		// ����״̬
		case LV_PULL_REFRESH: {
			setSelection(0);// ʱʱ�����ڶ���.
			// ����headView��padding����.
			mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);
			if (offset < 0) {
				/***
				 * Ҫ����ΪʲôisScroller = false;
				 */
				isScroller = false;
				mlistViewState = mlistViewState.LV_NORMAL;
			}
		}
			break;
		default:
			return;
		}
	}

	void doActionMove_B(MotionEvent event) {
		mMoveY = (int) event.getY();// ��ȡʵʱ����y����
		// ����Ƿ���һ��touch�¼�.(��mFirstItemIndexΪ0��Ҫ��ʼ��mStartY)
		if (mIsRecord_B == false && mLastItemIndex == getCount()) {
			mStartY = (int) event.getY();
			mIsRecord_B = true;
		}
		// ֱ�ӷ���˵���������һ��
		if (mIsRecord_B == false)
			return;

		// ������headview�ƶ�����Ϊy�ƶ���һ��.���Ƚ��Ѻã�
		int offset = (mMoveY - mStartY) / RATIO;

		switch (loadingMoreState) {
		// ��ͨ״̬
		case LV_NORMAL: {
			// ˵������
			if (offset < 0) {
				int distance = Math.abs(offset);
				// ����headView��padding����.
				mFootView.setPadding(0, distance - mHeadViewHeight, 0, 0);
				loadingMoreState = loadingMoreState.LV_PULL_REFRESH;// ����״̬
			}
		}
			break;
		// ����״̬
		case LV_PULL_REFRESH: {
			setSelection(getCount() - 1);// ʱʱ������ײ�
			// ����headView��padding����.
			int distance = Math.abs(offset);
			mFootView.setPadding(0, distance - mHeadViewHeight, 0, 0);
			// ˵���»�
			if (offset > 0) {
				/***
				 * Ҫ����ΪʲôisScroller = false;
				 */
				isScroller = false;
				loadingMoreState = loadingMoreState.LV_NORMAL;
			}
		}
			break;
		default:
			return;
		}
	}

	/***
	 * ����̧�����
	 * 
	 * @param event
	 */
	public void doActionUp(MotionEvent event) {
		mIsRecord = false;// ��ʱ��touch�¼���ϣ�Ҫ�رա�
		mIsRecord_B = false; // ��ʱ��touch�¼���ϣ�Ҫ�رա�
		isScroller = true;// ListView����Scrooler����.
		mlistViewState = mlistViewState.LV_NORMAL;// ״̬Ҳ�ع����״̬

		// ִ����Ӧ����.
		myAsynTask = new MyAsynTask();
		myAsynTask.execute(DRAG_UP);

	}

	private void doActionUp_B(MotionEvent event) {
		mIsRecord = false;// ��ʱ��touch�¼���ϣ�Ҫ�رա�
		isScroller = true;// ListView����Scrooler����.

		loadingMoreState = loadingMoreState.LV_NORMAL;// ״̬Ҳ�ع����״̬

		// ִ����Ӧ����.
		myAsynTask = new MyAsynTask();
		myAsynTask.execute(DRAG_DOWN);
	}

	/***
	 * ListView ��������
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstItemIndex = firstVisibleItem;
		mLastItemIndex = firstVisibleItem + visibleItemCount;

	}

	@Override
	public void onClick(View v) {

	}

	/***
	 * ���ڲ�������
	 * 
	 * @author zhangjia
	 * 
	 */
	private class MyAsynTask extends AsyncTask<Integer, Integer, Void> {
		private final static int STEP = 30;// ����
		private final static int TIME = 5;// ����ʱ��
		private int distance;// ���루�þ���ָ���ǣ�mHeadView��PaddingTop+mHeadView�ĸ߶ȣ���Ĭ��λ��״̬.��
		private int number;// ѭ��ִ�д���.
		private int disPadding;// ʱʱpadding����.
		private int DRAG;

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				this.DRAG = params[0];
				if (params[0] == DRAG_UP) {
					// ��ȡ����.
					distance = mHeadView.getPaddingTop()
							+ Math.abs(mHeadViewHeight);
				} else {
					// ��ȡ����.
					distance = mFootView.getPaddingTop()
							+ Math.abs(mHeadViewHeight);
				}

				// ��ȡѭ������.
				if (distance % STEP == 0) {
					number = distance / STEP;
				} else {
					number = distance / STEP + 1;
				}
				// ����ѭ��.
				for (int i = 0; i < number; i++) {
					Thread.sleep(TIME);
					publishProgress(STEP);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);

			switch (DRAG) {
			case DRAG_UP:
				disPadding = Math.max(mHeadView.getPaddingTop() - STEP, -1
						* mHeadViewHeight);
				mHeadView.setPadding(0, disPadding, 0, 0);// �ع�.
				break;
			case DRAG_DOWN:
				disPadding = Math.max(mFootView.getPaddingTop() - STEP, -1
						* mHeadViewHeight);
				mFootView.setPadding(0, disPadding, 0, 0);// �ع�.
				break;
			default:
				break;
			}

		}

	}

}
