package com.grasp.training.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.grasp.training.R;


public class SlideSwitch extends View {

	public static final int SHAPE_RECT = 1;
	public static final int SHAPE_CIRCLE = 2;
	private static final int RIM_SIZE = 0;
	private static final int COLOR_THEME = Color.parseColor("#ffff5913");
	// 3 attributes
	private int color_theme;
	private boolean isOpen;
	private int shape;
	// varials of drawing
	private Paint paint;
	private Rect backRect;
	private Rect frontRect;
	private int alpha;
	private int max_left;
	private int min_left;
	private int frontRect_left;
	private int frontRect_left_begin = RIM_SIZE;
	private int eventStartX;
	private int eventLastX;
	private int diffX = 0;
	private boolean slideable = true;
	private SlideListener listener;
	private Context context;
	public interface SlideListener {
		public void open();

		public void close();
	}

	public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context=context;

		listener = null;
		paint = new Paint();
		paint.setAntiAlias(true);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.slideswitch);
		color_theme = a.getColor(R.styleable.slideswitch_themeColor,
				COLOR_THEME);
		isOpen = a.getBoolean(R.styleable.slideswitch_isOpen, false);
		shape = a.getInt(R.styleable.slideswitch_shape, SHAPE_RECT);
		shape=2;
		a.recycle();
	}

	public SlideSwitch(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideSwitch(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = measureDimension(280, widthMeasureSpec);
		int height = measureDimension(140, heightMeasureSpec);
		if (shape == SHAPE_CIRCLE) {
			if (width < height)
				width = height * 2;
		}
		setMeasuredDimension(width, height);
		initDrawingVal();
	}

	public void initDrawingVal() {
		int width = getMeasuredWidth()-10;
		int height = getMeasuredHeight();

		backRect = new Rect(0, 0, width, height);
		min_left = RIM_SIZE;
		if (shape == SHAPE_RECT)
			max_left = width / 2;
		else
			max_left = width - (height - 2 * RIM_SIZE) - RIM_SIZE;
		if (isOpen) {
			frontRect_left = max_left;
			alpha = 255;
		} else {
			frontRect_left = RIM_SIZE;
			alpha = 0;
		}
		frontRect_left_begin = frontRect_left;
		//Log.e("qqq","frontRect_left_begin="+frontRect_left_begin+" isOpen="+isOpen+" max_left="+max_left);

	}

	public int measureDimension(int defaultSize, int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = defaultSize; // UNSPECIFIED
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void onDraw(Canvas canvas) {
		if (shape == SHAPE_RECT) {

//			Log.e("fff","1 slideable="+slideable);
            if(slideable){

                paint.setColor(context.getColor(R.color.hui));
            }else{
                paint.setColor(context.getColor(R.color.hu2));
            }

			canvas.drawRect(backRect, paint);
			if(slideable){

				paint.setColor(color_theme);
			}else{
				paint.setColor(context.getColor(R.color.hu2));
			}

			paint.setAlpha(alpha);
			canvas.drawRect(backRect, paint);



			frontRect = new Rect(frontRect_left, RIM_SIZE, frontRect_left
					+ getMeasuredWidth() / 2 - RIM_SIZE, getMeasuredHeight()
					- RIM_SIZE);
			paint.setColor(Color.WHITE);
			canvas.drawRect(frontRect, paint);
		} else {
			int radius;
			radius = backRect.height() / 2 - RIM_SIZE;

			// ?????
//			int width = getMeasuredWidth()-10;
//			int height = getMeasuredHeight();
//			Rect backRect1 = new Rect(0, 0, width, height);
//			paint.setColor(Color.RED);
//			canvas.drawRoundRect (new RectF(backRect1), radius, radius, paint);
//			Log.e("fff","2 slideable="+slideable);
//            if(slideable){

                paint.setColor(context.getColor(R.color.hui));
//            }else{
//                paint.setColor(context.getColor(R.color.hu2));
//            }
//			paint.setColor(context.getColor(R.color.hui));
			canvas.drawRoundRect(new RectF(backRect), radius, radius, paint);
//            if(slideable){

                paint.setColor(color_theme);
//            }else{
//                paint.setColor(context.getColor(R.color.hu2));
//            }
			paint.setAlpha(alpha);
			canvas.drawRoundRect(new RectF(backRect), radius, radius, paint);
			frontRect = new Rect(frontRect_left+5, RIM_SIZE+5, frontRect_left
					+ backRect.height() - 2 * RIM_SIZE-5, backRect.height()-5
					- RIM_SIZE);

			if (slideable) {

				paint.setColor(Color.WHITE);
			} else {
				paint.setColor(context.getColor(R.color.hu2));
			}
			canvas.drawRoundRect(new RectF(frontRect), radius, radius, paint);



		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (slideable == false)
			return super.onTouchEvent(event);
		int action = MotionEventCompat.getActionMasked(event);

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				eventStartX = (int) event.getRawX();
				break;
			case MotionEvent.ACTION_MOVE:
//				eventLastX = (int) event.getRawX();
//				diffX = eventLastX - eventStartX;
//				int tempX = diffX + frontRect_left_begin;
//				tempX = (tempX > max_left ? max_left : tempX);
//				tempX = (tempX < min_left ? min_left : tempX);
//				if (tempX >= min_left && tempX <= max_left) {
//					frontRect_left = tempX;
//					alpha = (int) (255 * (float) tempX / (float) max_left);
//					invalidateView();
//				}
				break;
			case MotionEvent.ACTION_UP:
//				int wholeX = (int) (event.getRawX() - eventStartX);
//				frontRect_left_begin = frontRect_left;
				boolean toRight=false;
//				toRight = (frontRect_left_begin > max_left / 2 ? true : false);
//				if (Math.abs(wholeX) < 3) {
//					toRight = !toRight;
//				}

//				Log.e("qqq","wholeX="+wholeX+" frontRect_left_begin="+frontRect_left_begin+" Math.abs(wholeX)= "+Math.abs(wholeX));
				moveToDest(!isOpen);
				break;
			default:
				break;
		}
		return true;
	}

	/**
	 * draw again
	 */
	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}

	public void setSlideListener(SlideListener listener) {
		this.listener = listener;
	}

	private boolean zt=false;

	public void  moveToDest(final boolean toRight) {
//		Log.e("qqq","toRight ="+toRight);
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				if (msg.what == 1) {
                    setState(false);
					setSlideable(false);
					Myhandler.sendEmptyMessageDelayed(99,2000);
					listener.open();
				} else {
                    setState(true);
					setSlideable(false);
					Myhandler.sendEmptyMessageDelayed(99,2000);
					listener.close();
				}
			}

		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.e("Slide","toRight ="+toRight );
				if (toRight) {
					while (frontRect_left <= max_left) {
						alpha = (int) (255 * (float) frontRect_left / (float) max_left);
//						invalidateView();
						frontRect_left += 3;
						try {
							Thread.sleep(3);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					alpha = 255;
					frontRect_left = max_left;
					isOpen = true;
					if (listener != null) {
						handler.sendEmptyMessage(1);
					}
					frontRect_left_begin = max_left;

				} else {
					while (frontRect_left >= min_left) {
						alpha = (int) (255 * (float) frontRect_left / (float) max_left);
//						invalidateView();
						frontRect_left -= 3;
						try {
							Thread.sleep(3);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					alpha = 0;
					frontRect_left = min_left;
					isOpen = false;
					if (listener != null) {
						handler.sendEmptyMessage(0);
					}
					frontRect_left_begin = min_left;

				}
			}
		}).start();

	}

	Handler Myhandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 99:
//					Log.e("fff","fffffffffffffff");
					setSlideable(true);
					break;
			}
		}
	};

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Myhandler.removeMessages(99);
	}

	public void setState(boolean isOpen) {

		this.isOpen = isOpen;
		zt=false;
		initDrawingVal();
		invalidateView();
		Myhandler.removeMessages(99);
		setSlideable(true);
		if(zt){


//		if (listener != null)
//			if (isOpen == true) {
//				listener.open();
//			} else {
//				listener.close();
//			}

		}else{
			zt=true;
		}
	}

	public void setShapeType(int shapeType) {
		this.shape = shapeType;
	}

	public void setSlideable(boolean slideable) {
		this.slideable = slideable;

        invalidateView();



	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			this.isOpen = bundle.getBoolean("isOpen");
			state = bundle.getParcelable("instanceState");
		}
		super.onRestoreInstanceState(state);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable("instanceState", super.onSaveInstanceState());
		bundle.putBoolean("isOpen", this.isOpen);
		return bundle;
	}

}
