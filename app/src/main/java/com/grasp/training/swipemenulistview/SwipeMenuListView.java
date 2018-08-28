package com.grasp.training.swipemenulistview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.grasp.training.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author baoyz
 * @date 2014-8-18
 * 
 */
public class SwipeMenuListView extends ListView implements OnScrollListener {

	private static final int TOUCH_STATE_NONE = 0;
	private static final int TOUCH_STATE_X = 1;
	private static final int TOUCH_STATE_Y = 2;

	private int MAX_Y = 5;
	private int MAX_X = 3;
	private float mDownX;
	private float mDownY;
	private int mTouchState;
	private int mTouchPosition;
	private SwipeMenuLayout mTouchView;
	private OnSwipeListener mOnSwipeListener;

	private SwipeMenuCreator mMenuCreator;
	private OnMenuItemClickListener mOnMenuItemClickListener;
	private Interpolator mCloseInterpolator;
	private Interpolator mOpenInterpolator;

	public SwipeMenuListView(Context context) {
		super(context);
		init();
		init(context);
	}

	public SwipeMenuListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		init(context);
	}

	public SwipeMenuListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		init(context);
	}

	private void init() {
		MAX_X = dp2px(MAX_X);
		MAX_Y = dp2px(MAX_Y);
		mTouchState = TOUCH_STATE_NONE;
	}

	public void setAdapter(BaseAdapter adapter) {
		 SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
	       String date=format.format(new Date());
	        lastUpdatedTextView.setText(R.string.news);
	        lastUpdatedTextView.append(date);
		super.setAdapter(new SwipeMenuAdapter(getContext(), adapter) {
			@Override
			public void createMenu(SwipeMenu menu) {
				if (mMenuCreator != null) {
					mMenuCreator.create(menu);
				}
			}

			@Override
			public void onItemClick(SwipeMenuView view, SwipeMenu menu,
					int index) {
				boolean flag = false;
				if (mOnMenuItemClickListener != null) {
					flag = mOnMenuItemClickListener.onMenuItemClick(
							view.getPosition(), menu, index);
				}
				if (mTouchView != null && !flag) {
					mTouchView.smoothCloseMenu();
				}
			}
		});
		
       
	}

	public void setCloseInterpolator(Interpolator interpolator) {
		mCloseInterpolator = interpolator;
	}

	public void setOpenInterpolator(Interpolator interpolator) {
		mOpenInterpolator = interpolator;
	}

	public Interpolator getOpenInterpolator() {
		return mOpenInterpolator;
	}

	public Interpolator getCloseInterpolator() {
		return mCloseInterpolator;
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		return super.onInterceptTouchEvent(ev);
//	}

//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		MotionEvent event=ev;
//		if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
//			return super.onTouchEvent(ev);
//		int action = MotionEventCompat.getActionMasked(ev);
//		action = ev.getAction();
//		switch (action) {
//		case MotionEvent.ACTION_DOWN:
//			int oldPos = mTouchPosition;
//			mDownX = ev.getX();
//			mDownY = ev.getY();
//			mTouchState = TOUCH_STATE_NONE;
//
//			mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
//
//			if (mTouchPosition == oldPos && mTouchView != null
//					&& mTouchView.isOpen()) {
//				mTouchState = TOUCH_STATE_X;
//				mTouchView.onSwipe(ev);
//				return true;
//			}
//
//			View view = getChildAt(mTouchPosition - getFirstVisiblePosition());
//
//			if (mTouchView != null && mTouchView.isOpen()) {
//				mTouchView.smoothCloseMenu();
//				mTouchView = null;
//				// return super.onTouchEvent(ev);
//				// try to cancel the touch event
//				MotionEvent cancelEvent = MotionEvent.obtain(ev);  
//				cancelEvent.setAction(MotionEvent.ACTION_CANCEL);    
//				onTouchEvent(cancelEvent);
//				return true;
//			}
//			if (view instanceof SwipeMenuLayout) {
//				mTouchView = (SwipeMenuLayout) view;
//			}
//			if (mTouchView != null) {
//				mTouchView.onSwipe(ev);
//			}
//			break;
//		case MotionEvent.ACTION_MOVE:
//			float dy = Math.abs((ev.getY() - mDownY));
//			float dx = Math.abs((ev.getX() - mDownX));
//			if (mTouchState == TOUCH_STATE_X) {
//				if (mTouchView != null) {
//					mTouchView.onSwipe(ev);
//				}
//				getSelector().setState(new int[] { 0 });
//				ev.setAction(MotionEvent.ACTION_CANCEL);
//				super.onTouchEvent(ev);
//				return true;
//			} else if (mTouchState == TOUCH_STATE_NONE) {
//				if (Math.abs(dy) > MAX_Y) {
//					mTouchState = TOUCH_STATE_Y;
//				} else if (dx > MAX_X) {
//					mTouchState = TOUCH_STATE_X;
//					if (mOnSwipeListener != null) {
//						mOnSwipeListener.onSwipeStart(mTouchPosition);
//					}
//				}
//			}
//			break;
//		case MotionEvent.ACTION_UP:
//			if (mTouchState == TOUCH_STATE_X) {
//				if (mTouchView != null) {
//					mTouchView.onSwipe(ev);
//					if (!mTouchView.isOpen()) {
//						mTouchPosition = -1;
//						mTouchView = null;
//					}
//				}
//				if (mOnSwipeListener != null) {
//					mOnSwipeListener.onSwipeEnd(mTouchPosition);
//				}
//				ev.setAction(MotionEvent.ACTION_CANCEL);
//				super.onTouchEvent(ev);
//				return true;
//			}
//			break;
//		}
//		
//		if (isRefreshable) {
//			switch (event.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//				if (firstItemIndex == 0 && !isRecored) {
//					isRecored = true;
//					startY = (int) event.getY();
////					Log.v("this is", "在down时候记录当前位置");
//				}
//				break;
//			case MotionEvent.ACTION_UP:
//				if (state != REFRESHING && state != LOADING) {
//					if (state == DONE) {
//					}
//					if (state == PULL_To_REFRESH) {
//						state = DONE;
//						changeHeaderViewByState();
////						Log.v("this is", "由下拉刷新状态，到done状态");
//					}
//					if (state == RELEASE_To_REFRESH) {
//						state = REFRESHING;
//						changeHeaderViewByState();
//						onRefresh();
////						Log.v("this is", "由松开刷新状态，到done状态");
//					}
//				}
//				isRecored = false;
//				isBack = false;
//				break;
//			case MotionEvent.ACTION_MOVE:
//				int tempY = (int) event.getY();
//				if (!isRecored && firstItemIndex == 0) {
//					isRecored = true;
//					startY = tempY;
////					Log.v("this is", "在move时候记录下位置");
//				}
//				// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
//				if (state != REFRESHING && isRecored && state != LOADING) {
//
//					// 可以松手去刷新了
//					if (state == RELEASE_To_REFRESH) {
//						setSelection(0);
//						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
//						if (((tempY - startY) / RATIO < headContentHeight)
//								&& (tempY - startY) > 0) {
//							state = PULL_To_REFRESH;
//							changeHeaderViewByState();
////							Log.v("this is", "由松开刷新状态转变到下拉刷新状态");
//
//						}
//						// 一下子推到顶了
//						else if (tempY - startY <= 0) {
//							state = DONE;
//							changeHeaderViewByState();
////							Log.v("this is", "由松开刷新状态转变到done状态");
//
//						}
//						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
//						else {
//							// 不用进行特别的操作，只用更新paddingTop的值就行了
//						}
//					}
//					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
//					if (state == PULL_To_REFRESH) {
//						setSelection(0);
//						// 下拉到可以进入RELEASE_TO_REFRESH的状态
//						if ((tempY - startY) / RATIO >= headContentHeight) {
//							state = RELEASE_To_REFRESH;
//							isBack = true;
//							changeHeaderViewByState();
////							Log.v("this is", "由done或者下拉刷新状态转变到松开刷新");
//
//						}
//						// 上推到顶了
//						else if (tempY - startY <= 0) {
//							state = DONE;
//							changeHeaderViewByState();
////							Log.v("this is", "由DOne或者下拉刷新状态转变到done状态");
//
//						}
//					}
//					// done状态下
//
//					if (state == DONE) {
//						if (tempY - startY > 0) {
//							state = PULL_To_REFRESH;
//							changeHeaderViewByState();
//						}
//					}
//					// 更新headView的size
//
//					if (state == PULL_To_REFRESH) {
//						headView.setPadding(0, -1 * headContentHeight
//								+ (tempY - startY) / RATIO, 0, 0);
//					}
//					// 更新headView的paddingTop
//
//					if (state == RELEASE_To_REFRESH) {
//						headView.setPadding(0, (tempY - startY) / RATIO
//								- headContentHeight, 0, 0);
//					}
//				}
//				break;
//			}
//		}
//		
//		return super.onTouchEvent(ev);
//	}

	public void smoothOpenMenu(int position) {
		if (position >= getFirstVisiblePosition()
				&& position <= getLastVisiblePosition()) {
			View view = getChildAt(position - getFirstVisiblePosition());
			if (view instanceof SwipeMenuLayout) {
				mTouchPosition = position;
				if (mTouchView != null && mTouchView.isOpen()) {
					mTouchView.smoothCloseMenu();
				}
				mTouchView = (SwipeMenuLayout) view;
				mTouchView.smoothOpenMenu();
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getContext().getResources().getDisplayMetrics());
	}

	public void setMenuCreator(SwipeMenuCreator menuCreator) {
		this.mMenuCreator = menuCreator;
	}

	public void setOnMenuItemClickListener(
			OnMenuItemClickListener onMenuItemClickListener) {
		this.mOnMenuItemClickListener = onMenuItemClickListener;
	}

	public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
		this.mOnSwipeListener = onSwipeListener;
	}

	public static interface OnMenuItemClickListener {
		boolean onMenuItemClick(int position, SwipeMenu menu, int index);
	}

	public static interface OnSwipeListener {
		void onSwipeStart(int position);

		void onSwipeEnd(int position);
	}
	
	
	private final static int RELEASE_To_REFRESH = 0;// 发布刷新
	private final static int PULL_To_REFRESH = 1;// 下拉刷新
	// 正在刷新
	private final static int REFRESHING = 2;// 刷新
	// 刷新完成
	private final static int DONE = 3;// enxia
	private final static int LOADING = 4;// 加载
	// 实际的padding的距离与界面上偏移的距离的比例
	private final static int RATIO = 3;
	private LayoutInflater inflater;
	private LinearLayout headView;
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private boolean isRecored;// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private int headContentWidth;
	private int headContentHeight;
	private int startY;
	public int firstItemIndex;
	private int state;
	private boolean isBack;
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;

	private void init(Context context) {
		// 自定义listview要设置背景为#0000000防止滚动时出现黑色
		setCacheColorHint(0x000000);
		inflater = LayoutInflater.from(context);
		headView = (LinearLayout) inflater.inflate(R.layout.head, null);
		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		// 刷新view
		headView.invalidate();
		Log.v("this is", "width:" + headContentWidth + " height:"
				+ headContentHeight);
		// 添加头文件不被 selected
		addHeaderView(headView, null, false);
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
                         int arg3) {
		firstItemIndex = firstVisiableItem;
		if(im!=null){
		if(arg3 > arg2){
			//存在第二页
			im.setVisibility(View.VISIBLE);
			}else{
				im.setVisibility(View.GONE);
			}
		}
	}

	private ImageView im=null;   //滚动到顶点
	

	public void setIm(ImageView im) {
		this.im = im;
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}
	
	
	
	// 当状态改变时候，调用该方法，以更新界面

		private void changeHeaderViewByState() {
			switch (state) {
			case RELEASE_To_REFRESH:
				arrowImageView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				tipsTextview.setVisibility(View.VISIBLE);
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(animation);
				tipsTextview.setText(R.string.news2);
				break;
			case PULL_To_REFRESH:
				progressBar.setVisibility(View.GONE);
				tipsTextview.setVisibility(View.VISIBLE);
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.VISIBLE);
				// 是由RELEASE_To_REFRESH状态转变来的

				if (isBack) {
					isBack = false;
					arrowImageView.clearAnimation();
					arrowImageView.startAnimation(reverseAnimation);
					tipsTextview.setText(R.string.head_xl);//下拉刷新
				} else {
					tipsTextview.setText(R.string.head_xl);//下拉刷新
				}
				break;

			case REFRESHING:
				headView.setPadding(0, 0, 0, 0);
				progressBar.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.GONE);
				tipsTextview.setText(R.string.head_zz);
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				break;
			case DONE:
				headView.setPadding(0, -1 * headContentHeight, 0, 0);
				progressBar.setVisibility(View.GONE);
				arrowImageView.clearAnimation();
				arrowImageView.setImageResource(R.drawable.arrow);
				tipsTextview.setText(R.string.head_wb);
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				break;
			}
		}

		public void setonRefreshListener(OnRefreshListener refreshListener) {
			this.refreshListener = refreshListener;
			isRefreshable = true;
		}

		// 自定义接口(方便调用里面的方法)
		public interface OnRefreshListener {
			public void onRefresh();
			// Object refreshing(); //加载数据
			// void refreshed(Object obj); //外部可扩展加载完成后的操作
		}

		// 注入接口
		public void onRefreshComplete() {
			state = DONE;
			setTime();
			changeHeaderViewByState();
		}

		public void setTime(){ //刷新时间
			SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
	        String date=format.format(new Date());
	       lastUpdatedTextView.setText("最近更新:" + date);
		}
		private void onRefresh() {
			if (refreshListener != null) {
				refreshListener.onRefresh();
			}
		}

		// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height

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
				// MeasureSpec.UNSPECIFIED,
				// 未指定尺寸这种情况不多，一般都是父控件是AdapterView，通过measure方法传入的模式
				// MeasureSpec.EXACTLY,精确尺寸
				// MeasureSpec.AT_MOST最大尺寸
				childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
						MeasureSpec.EXACTLY);
			} else {
				childHeightSpec = MeasureSpec.makeMeasureSpec(0,
						MeasureSpec.UNSPECIFIED);
			}
			child.measure(childWidthSpec, childHeightSpec);
		}
//		public void setAdapter(BaseAdapter adapter) { 
//	        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日  HH:mm"); 
//	       String date=format.format(new Date()); 
//	        lastUpdatedTextView.setText(R.string.news); 
//	        lastUpdatedTextView.append(date);
//	       super.setAdapter(adapter); 
//	     } 
		
		
		
		// 滑动距离及坐标
	    private float xDistance, yDistance, xLast, yLast;
		@Override
	    public boolean onInterceptTouchEvent(MotionEvent ev) {   //滑动拦截
	            switch (ev.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                    xDistance = yDistance = 0f;
	                    xLast = ev.getX();
	                    yLast = ev.getY();
	                    break;
	            case MotionEvent.ACTION_MOVE:
	                    final float curX = ev.getX();
	                    final float curY = ev.getY();

	                    xDistance += Math.abs(curX - xLast);
	                    yDistance += Math.abs(curY - yLast);
	                    xLast = curX;
	                    yLast = curY;

	                    if (xDistance > yDistance) {
	                            return false;   //表示向下传递事件
	                    }
	            }

	            return super.onInterceptTouchEvent(ev);
	    }
		
		public boolean onTouchEvent(MotionEvent event) {
			if (isRefreshable) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (firstItemIndex == 0 && !isRecored) {
						isRecored = true;
						startY = (int) event.getY();
//						Log.v("this is", "在down时候记录当前位置");
					}
					break;
				case MotionEvent.ACTION_UP:
					if (state != REFRESHING && state != LOADING) {
						if (state == DONE) {
						}
						if (state == PULL_To_REFRESH) {
							state = DONE;
							changeHeaderViewByState();
//							Log.v("this is", "由下拉刷新状态，到done状态");
						}
						if (state == RELEASE_To_REFRESH) {
							state = REFRESHING;
							changeHeaderViewByState();
							onRefresh();
//							Log.v("this is", "由松开刷新状态，到done状态");
						}
					}
					isRecored = false;
					isBack = false;
					break;
				case MotionEvent.ACTION_MOVE:
					int tempY = (int) event.getY();
					if (!isRecored && firstItemIndex == 0) {
						isRecored = true;
						startY = tempY;
//						Log.v("this is", "在move时候记录下位置");
					}
					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
					if (state != REFRESHING && isRecored && state != LOADING) {

						// 可以松手去刷新了
						if (state == RELEASE_To_REFRESH) {
							setSelection(0);
							// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
							if (((tempY - startY) / RATIO < headContentHeight)
									&& (tempY - startY) > 0) {
								state = PULL_To_REFRESH;
								changeHeaderViewByState();
//								Log.v("this is", "由松开刷新状态转变到下拉刷新状态");

							}
							// 一下子推到顶了
							else if (tempY - startY <= 0) {
								state = DONE;
								changeHeaderViewByState();
//								Log.v("this is", "由松开刷新状态转变到done状态");

							}
							// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
							else {
								// 不用进行特别的操作，只用更新paddingTop的值就行了
							}
						}
						// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
						if (state == PULL_To_REFRESH) {
							setSelection(0);
							// 下拉到可以进入RELEASE_TO_REFRESH的状态
							if ((tempY - startY) / RATIO >= headContentHeight) {
								state = RELEASE_To_REFRESH;
								isBack = true;
								changeHeaderViewByState();
//								Log.v("this is", "由done或者下拉刷新状态转变到松开刷新");

							}
							// 上推到顶了
							else if (tempY - startY <= 0) {
								state = DONE;
								changeHeaderViewByState();
//								Log.v("this is", "由DOne或者下拉刷新状态转变到done状态");

							}
						}
						// done状态下

						if (state == DONE) {
							if (tempY - startY > 0) {
								state = PULL_To_REFRESH;
								changeHeaderViewByState();
							}
						}
						// 更新headView的size

						if (state == PULL_To_REFRESH) {
							headView.setPadding(0, -1 * headContentHeight
									+ (tempY - startY) / RATIO, 0, 0);
						}
						// 更新headView的paddingTop

						if (state == RELEASE_To_REFRESH) {
							headView.setPadding(0, (tempY - startY) / RATIO
									- headContentHeight, 0, 0);
						}
					}
					break;
				}
			}
			MotionEvent ev=event;
			if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
				return super.onTouchEvent(ev);
			int action = MotionEventCompat.getActionMasked(ev);
			action = ev.getAction();
			switch (action) {
			
			case MotionEvent.ACTION_DOWN:
				int oldPos = mTouchPosition;
				mDownX = ev.getX();
				mDownY = ev.getY();
				mTouchState = TOUCH_STATE_NONE;

				mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

				if (mTouchPosition == oldPos && mTouchView != null
						&& mTouchView.isOpen()) {
					mTouchState = TOUCH_STATE_X;
					mTouchView.onSwipe(ev);
					return true;
				}

				View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

				if (mTouchView != null && mTouchView.isOpen()) {
					mTouchView.smoothCloseMenu();
					mTouchView = null;
					// return super.onTouchEvent(ev);
					// try to cancel the touch event
					MotionEvent cancelEvent = MotionEvent.obtain(ev);
					cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
					onTouchEvent(cancelEvent);
					return true;
				}
				if (view instanceof SwipeMenuLayout) {
					mTouchView = (SwipeMenuLayout) view;
				}
				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				float dy = Math.abs((ev.getY() - mDownY));
				float dx = Math.abs((ev.getX() - mDownX));
				if (mTouchState == TOUCH_STATE_X) {
					if (mTouchView != null) {
						mTouchView.onSwipe(ev);
					}
					getSelector().setState(new int[] { 0 });
					ev.setAction(MotionEvent.ACTION_CANCEL);
					super.onTouchEvent(ev);
					return true;
				} else if (mTouchState == TOUCH_STATE_NONE) {
					if (Math.abs(dy) > MAX_Y) {
						mTouchState = TOUCH_STATE_Y;
					} else if (dx > MAX_X) {
						mTouchState = TOUCH_STATE_X;
						if (mOnSwipeListener != null) {
							mOnSwipeListener.onSwipeStart(mTouchPosition);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mTouchState == TOUCH_STATE_X) {
					if (mTouchView != null) {
						mTouchView.onSwipe(ev);
						if (!mTouchView.isOpen()) {
							mTouchPosition = -1;
							mTouchView = null;
						}
					}
					if (mOnSwipeListener != null) {
						mOnSwipeListener.onSwipeEnd(mTouchPosition);
					}
					ev.setAction(MotionEvent.ACTION_CANCEL);
					super.onTouchEvent(ev);
					return true;
				}
				break;
			}
			return super.onTouchEvent(ev);
			
//			return super.onTouchEvent(event);
			}
}
