package com.grasp.training.activity;

import java.util.ArrayList;
import java.util.List;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.viewPagerAdapter;


public class SplashActivity extends Activity implements OnPageChangeListener{//引导页

	
	    private TextView js;
	    private ViewPager vp;
		private viewPagerAdapter vAdaper;
		private List<View> list;
		private int []id={R.id.iv1,R.id.iv2,R.id.iv3};
		private ImageView []im;
		private ImageView b;
		private RelativeLayout rlayout;
		private SharedPreferences sp;
		private final String SPLASH="SPLASH";
		private final String SPLASH_TOGo="Go";
		private final String SPLASH_versionName="versionName";
		public static Activity activity;
		/**当前Activity渲染的视图View**/
		private View mContextView = null;

//		static {
//			System.loadLibrary("aacDecode");
//		}

		@Override
			protected void onCreate(Bundle savedInstanceState) {
				// TODO Auto-generated method stub
			requestWindowFeature(Window.FEATURE_NO_TITLE);
//			getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
			super.onCreate(savedInstanceState);
				
				//设置渲染视图View
				mContextView = LayoutInflater.from(this).inflate(R.layout.splash_activity, null);
				setContentView(mContextView);
//			mContextView.setSystemUiVisibility(View.INVISIBLE);

//			Tool.setWindowStatusBarColor(SplashActivity.this,R.color.white);
				activity=this;
				init();
				playIntent();
			}
		
		private void init() {
			// TODO Auto-generated method stub
	       LayoutInflater inflater1=LayoutInflater.from(this);
	       list=new ArrayList<View>();
	       list.add(inflater1.inflate(R.layout.l1, null));  //添加引导页
	       list.add(inflater1.inflate(R.layout.l2, null));
	       list.add(inflater1.inflate(R.layout.l3, null));
	       vAdaper=new viewPagerAdapter(this, list);
	       vp=(ViewPager) findViewById(R.id.viewPager);
	       vp.setAdapter(vAdaper);
	       vp.setOnPageChangeListener(this);//添加监听
	       rlayout=(RelativeLayout) findViewById(R.id.splashactivaty_layout);
	       sp=PreferenceManager.getDefaultSharedPreferences(this);
	       js=(TextView) findViewById(R.id.sp_js);
//			setpp();
		}

	public void setpp(){
		//<dimen name="dip_48">48dip</dimen>
		double a[]={459.0,53.0,429.0,85.0,20.5,240.0,130.0,95.0,257.0,170.0};
		String s="";
		for (int i=0;i<a.length;i++){
			//Log.d("qqq","<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5+"dip</dimen>");
			s+="<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5+"dip</dimen>"+"  ";
		}
		Log.d("splash",s);
		Log.d("splash","a[i]*0.5*1.5************");
		s="";
		for (int i=0;i<a.length;i++){
			//Log.d("qqq","<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5*1.5+"dip</dimen>");
			s+="<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5*1.5+"dip</dimen>"+"  ";
		}
		Log.d("splash",s);
		Log.d("splash","************");
		s="";
		for (int i=0;i<a.length;i++){
			//Log.d("qqq","<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5*2+"dip</dimen>");
			s+="<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5*2+"dip</dimen>"+"  ";
		}
		Log.d("splash",s);
		Log.d("splash","a[i]*0.5*3************");
		s="";
		for (int i=0;i<a.length;i++){
			//Log.d("qqq","<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5*3+"dip</dimen>");
			s+="<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5*3+"dip</dimen>"+"  ";
		}
		Log.d("splash",s);
		Log.d("splash","i*0.5*3*1.2************");
		s="";
		for (int i=0;i<a.length;i++){
			//Log.d("qqq","<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5*3*1.2+"dip</dimen>");
			s+="<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5*3*1.2+"dip</dimen>"+"  ";
		}
		Log.d("splash",s);
		Log.d("splash","i*0.5*8*2************");
		s="";
		for (int i=0;i<a.length;i++){
			//Log.d("qqq","<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5*9+"dip</dimen>");
			s+="<dimen name=\"dip_"+a[i]+"\">"+a[i]*0.5*8*2+"dip</dimen>"+"  ";
		}
		Log.d("splash",s);
//		Log.d("qqq","<dimen name=\"dip_"+i+"\">"+i*0.5+"dip</dimen>");
//		Log.d("qqq","<dimen name=\"dip_"+i+"\">"+i*0.5*1.5 +"dip</dimen>");
//		Log.d("qqq","<dimen name=\"dip_"+i+"\">"+i*0.5*2 +"dip</dimen>");
//		Log.d("qqq","<dimen name=\"dip_"+i+"\">"+i*0.5*3 +"dip</dimen>");
//		Log.d("qqq","<dimen name=\"dip_"+i+"\">"+i*0.5*3*1.2 +"dip</dimen>");
//		Log.d("qqq","<dimen name=\"dip_"+i+"\">"+i*0.5*9 +"dip</dimen>");
	}


		
		public void playIntent(){
			String ss=sp.getString(SPLASH, "");
			String versionName=sp.getString(SPLASH_versionName, "");
//			if(ss.equals(SPLASH_TOGo)&&versionName.equals(getVersion())){  //开启引导页
				rlayout.setVisibility(View.GONE);
				gotoView(mContextView);
//			}else{
//				initim();
//			}
			
		}






		 /**
		   * 获取版本号
		   * @return 当前应用的版本号
		   */
		  public String getVersion() {
		      try {
		          PackageManager manager = this.getPackageManager();
		         PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
		          String version = info.versionName;
		         return   version;
		     } catch (Exception e) {
		         e.printStackTrace();
		         return null;
		    }
		 }
		
		
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public void gotoView(View view) {  //动画
		Intent intent = new Intent();
		intent.setClass(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
		//淡入淡出效果
//		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//			//添加动画效果
//			AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
//			animation.setDuration(2000);
//			animation.setAnimationListener(new Animation.AnimationListener() {
//
//				@Override
//				public void onAnimationStart(Animation animation) {
//					Log.d("qqq","onAnimationStart");
//				}
//
//				@Override
//				public void onAnimationRepeat(Animation animation) {
//					Log.d("qqq","onAnimationRepeat");
//				}
//
//				@Override
//				public void onAnimationEnd(Animation animation) {
//					//跳转界面
//					Log.d("qqq","onAnimationEnd");
//					Intent intent = new Intent();
//		            intent.setClass(SplashActivity.this, LoginActivity.class);
//		            startActivity(intent);
//					finish();
//					//淡入淡出效果
//					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//				}
//			});
//			view.setAnimation(animation);
		}
		
		
		
		
		private void initim() {  //点点
			// TODO Auto-generated method stub
			im=new ImageView [list.size()];
			for (int i = 0; i < list.size(); i++) {
				im[i]=(ImageView) findViewById(id[i]);
			}
			
			
		}

		
		
		
		@Override
		public void onPageScrollStateChanged(int arg0) {  //滑动状态进行改变
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {//一面被滑动
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {//当前新的页面被选中时
			for (int i = 0; i < list.size(); i++) {
				if(arg0==i){
					im[i].setImageResource(R.drawable.fyf_on);
				}
				else{
					im[i].setImageResource(R.drawable.fyf_off);
				}
			}
			if(arg0==list.size()-1){
				ha.sendEmptyMessageDelayed(111, 0);  //跳转
			}
		}
		int ha_num=3;
		Handler ha=new Handler(){

			@RequiresApi(api = Build.VERSION_CODES.ECLAIR)
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				switch (msg.what) {
				case 666:
					 Intent in=new Intent(SplashActivity.this,SplashActivity.class);
					   startActivity(in);
					 //右往左推出效果
						overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
					   SharedPreferences.Editor eds=sp.edit();
					   eds.putString(SPLASH, SPLASH_TOGo);   // 记入下跳转过
					   eds.putString(SPLASH_versionName, getVersion());
					   eds.commit();
					   finish();

					   
					break;
				case 111:
					js.setText(ha_num+"");
					if(ha_num==0){
						ha.sendEmptyMessageDelayed(666, 0);	
					}else{
						ha.sendEmptyMessageDelayed(111, 1000);
					}
					ha_num--;
					break;

				case 222:
					gotoView(mContextView);
					break;
				default:
					break;
				}
			}
			
		};
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			ha.removeMessages(666);
			ha.removeMessages(111);
			ha.removeMessages(222);
		}
}
