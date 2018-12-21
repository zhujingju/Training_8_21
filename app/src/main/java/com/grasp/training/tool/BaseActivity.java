package com.grasp.training.tool;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.grasp.training.activity.SplashActivity;


public abstract class BaseActivity extends Activity {

	  private Context context;
	  protected Resources res; // 通用资源缩写
	  
	    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示标题
//			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        super.onCreate(savedInstanceState);
	        myActivityManage.addActivity(this);
	        res = getResources(); // 通用资源缩写  
	        
	        // 优化输入法模式  
	        int inputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
	        getWindow().setSoftInputMode(inputMode);  
//	        if (null != savedInstanceState) {
//	            //activity由系统加载的时候savedInstanceState不为空
//	            finish();
//	            return;
//	        }
	        setContentView(setLayoutId());
	        setContext(this);
	        initData();
	        initView();
	        initObject();
	        initListener() ;
	        init();
	    }


//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		// TODO Auto-generated method stub
//		super.onConfigurationChanged(newConfig);
//		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			setContentView(setLayoutId());
////			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
////			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//			setContentView(setLayoutId());
//		}
//	}

	public  abstract int setLayoutId();   //获得布局
	    public  abstract void initData() ;//初始化数据
	    public  abstract void initView();   // 这里初始化控件
	    public  abstract void initObject();  //初始化对象
	    public  abstract void initListener() ;//初始化监听
	    public  abstract void init() ;//写操作
	    /** 
	     * 检查字符串是否是空对象或空字符串 
	     *  
	     * @param str 
	     * @return 为空返回true,不为空返回false 
	     */  
	    public boolean isNull(String str) {
	        if (null == str || "".equals(str) || "null".equalsIgnoreCase(str)) {  
	            return true;  
	        } else {  
	            return false;  
	        }  
	    }  
	  
	    /** 
	     * 检查字符串是否是字符串 
	     *  
	     * @param str 
	     * @return 为空返回true,不为空返回false 
	     */  
	    public boolean isStr(String str) {
	        return !isNull(str);  
	    }  
	    
	    
	    /** 
	     * 从当前activity跳转到目标activity,<br> 
	     * 如果目标activity曾经打开过,就重新展现,<br> 
	     * 如果从来没打开过,就新建一个打开 
	     *  
	     * @param cls 
	     */  
	    public void gotoExistActivity(Class<?> cls) {
	        Intent intent;
	        intent = new Intent(this, cls);
	        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	        startActivity(intent);  
	    }  
	    
	    /** 
	     * 新建一个activity打开 
	     *  
	     * @param cls 
	     */  
	    public void gotoActivity(Class<?> cls) {
	        Intent intent;
	        intent = new Intent(this, cls);
	        startActivity(intent);  
	    }  
	  
	    /** 
	     * 通用消息提示 
	     *  
	     * @param resId 
	     */  
	    public void toast(int resId) {  
	        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	    }  
	  

	    public void toast(String msg) {
	        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	    }  
	    
	    /** 
	     * 从资源获取字符串 
	     *  
	     * @param resId 
	     * @return 
	     */  
	    public String getStr(int resId) {
	        return res.getString(resId);  
	    }  
	  
	    /** 
	     * 从EditText 获取字符串 
	     *  
	     * @param editText 
	     * @return 
	     */  
	    public String getStr(EditText editText) {
	        return editText.getText().toString();  
	    }  
	  
	

	    
	    public View getViewFin(int R){    //返回view实例
	    	return findViewById(R);
	    }
	    
	    
	    @Override
	    protected void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	myActivityManage.remove(this);
	    }
	    
	    @Override
	    protected void onStart() {
	    	// TODO Auto-generated method stub
	    	SplashActivity.activity=this;
	    	super.onStart();
//			Tool.setWindowStatusBarColor((Activity) getContext(),R.color.white);
	    }
		public Context getContext() {    //获取上下文
			return context;
		}
		public void setContext(Context context) {
			this.context = context;
		}
	    
	    
}
