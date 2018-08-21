package com.grasp.training.tool;


import android.app.Activity;

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
    	a.finish();
    	ls.remove(a);
    }
}