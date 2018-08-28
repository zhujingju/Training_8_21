package com.grasp.training.view;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grasp.training.R;


@SuppressLint("NewApi")
public class Head_Hurdle_view extends LinearLayout{

	private Context co;
	private TextView tv1;
	public Head_Hurdle_view(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Head_Hurdle_view(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public Head_Hurdle_view(Context context, AttributeSet attrs) {
		super(context, attrs);
		co=context;
		LayoutInflater.from(context).inflate(R.layout.head_hurdle_layout, this);
		TypedArray ty=context.obtainStyledAttributes(attrs, R.styleable.butt);
		String s=ty.getString(R.styleable.butt_myString);
		//Color color=ty.getColor(R.styleable.butt_myColor);
		ImageView iv1=(ImageView)findViewById(R.id.hh_im_im);
	 	tv1=(TextView) findViewById(R.id.hh_tv);
	    tv1.setText(s);
	    iv1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					((Activity)co).finish();
				}
			} );
		ty.recycle();//刷新
	}

	public void setTextString(String s){  //改变中间文字
		tv1.setText(s);
	}
}
