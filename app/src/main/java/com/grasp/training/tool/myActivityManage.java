package com.grasp.training.tool;


import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class myActivityManage {       //页面管理类
    public static List<Activity> ls=new ArrayList<Activity>();
    
    
    public static void addActivity(Activity a){   //添加
    	ls.remove(a);  //先删
    	ls.add(a);
    }
    
    public static void removeAll(){      //删除全部
    	for (int i = 0; i < ls.size(); i++) {
			ls.get(i).finish();
		}
    	ls.clear();
    }
    public static void remove(Activity a){          //删除当前
		for(Activity al:ls){
			if(a==al){
				a.finish();
				ls.remove(a);
				Log.e("qqq","myActivityManage");
				return;
			}
		}
    }

	public static void removeA(Activity a){          //删除当前
		ls.remove(a);
	}
}