package com.grasp.training.tool;



import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class viewPagerAdapter extends PagerAdapter{   //viewPager的适配器

	private List<View> list;
	private Context co;
	public viewPagerAdapter(Context co,List<View> list){
		this.co=co;
		this.list=list;
	}
	
	@Override
		public void destroyItem(View container, int position, Object object) {  //销毁view
			// TODO Auto-generated method stub
		 ((ViewPager) container).removeView(list.get(position));
		}
	
	@Override
		public Object instantiateItem(View container, int position) {  //类似于getview
			// TODO Auto-generated method stub
		((ViewPager) container).addView(list.get(position));
		
			return list.get(position);
		}
	
	@Override
	public int getCount() {  //返回view数量
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {  //判断当前view是否为我们需要的对象
		
		
		return (arg0==arg1);
	}

}
