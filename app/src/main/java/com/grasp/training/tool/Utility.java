package com.grasp.training.tool;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class Utility 
{
	//private static  List<MyTreeItem> LeafList;//存储叶子结点的数据结构
	/**
	 * 获取imsi
	 * 
	 * @param con
	 * @return
	 */
	public static String getImsi(Context con) {

		TelephonyManager mTelephonyMgr = (TelephonyManager) con
				.getSystemService(Context.TELEPHONY_SERVICE);
		String secureId = android.provider.Settings.Secure.getString(
				con.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		String subscriberId = mTelephonyMgr.getSubscriberId();
		String deviceId = mTelephonyMgr.getDeviceId();

		Log.w("imsi", "SubscriberId:" + subscriberId);
		Log.w("imsi", "secureId:" + secureId);
		Log.w("imsi", "DeviceId:" + deviceId);
		return secureId;

	}
	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	public static String getVersionName(Context context) {
		String version = "";
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "v" + version;
	}
	public static void Logout(Activity c)//注销行为
	{
		//Intent intent=new Intent(c,Settings.class);
		//intent.putExtra("logout", true);
		//c.startActivity(intent);
		c.finish();
	}
	private static class OnDialogBack implements DialogInterface.OnClickListener//确认对话框的确定按钮事件
	{
		private Activity context;
		public OnDialogBack(Activity c)
		{
			context=c;
		}
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			Utility.Logout(context);
		}
	}
/*	public static void ShowConfirmDialog(Activity c)//显示确认对话框
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(c);
		builder.setTitle(R.string.exit);
		builder.setMessage(R.string.exit_info);
		builder.setPositiveButton(R.string.positive, new OnDialogBack(c));
		builder.setNegativeButton(R.string.negative, null);
		Dialog dialog=builder.create();
		dialog.show();
	}*/
	public static String FormateTime(int time)//hh:mm:ss,time是毫秒级的时间
	{
		int hour=time/(1000*60*60);
		int minute=(time%(1000*60*60))/(1000*60);
		int second=((time%(1000*60*60))%(1000*60))/1000;
		String h,m,s;
		if(hour<10)
		{
			h="0"+hour;
		}
		else
		{
			h= String.valueOf(hour);
		}
		if(minute<10)
		{
			m="0"+minute;
		}
		else
		{
			m= String.valueOf(minute);
		}
		if(second<10)
		{
			s="0"+second;
		}
		else
		{
			s= String.valueOf(second);
		}
		String str=h+":"+m+":"+s;

		return str;
	}
	/**
	 * 检查输入的IP地址是否合法
	 * @param ip
	 * @return
	 */
	public static boolean isValidIP(String ip)
	{
		ip=ip.replace('.', '#');
		String[] numbers = ip.split("#");
		if (numbers.length != 4)
			return false;
		for (int i = 0; i < numbers.length; i++)
		{
			int number;
			try
			{
				number = Integer.parseInt(numbers[i]);
			}
			catch(Exception e)
			{
				return false;
			}
			if ((number < 0) || (number > 255))
				return false;
		}
		return true;
	}
	/**
	 * 检查是否有效数字
	 * @param n
	 * @return
	 */
	public static boolean isValidNumber(String n)
	{

		try
		{
			Integer.parseInt(n);
			return true;
		} catch (Exception e)
		{
			return false;
		}

	}
	/**
	 * 得到中文的当前时间，精确到秒
	 * @return
	 */
	public static String GetCurrentTime()
	{
   		Date date=new Date();
		Calendar c= Calendar.getInstance();
		c.setTime(date);
    	String s= c.get(Calendar.YEAR)+"年"+(c.get(Calendar.MONDAY)+1)+"月"+ c.get(Calendar.DAY_OF_MONTH)+"日"+c.get(Calendar.HOUR_OF_DAY)+"时"+c.get(Calendar.MINUTE)+"分"+c.get(Calendar.SECOND)+"秒";
    	return s;
	}
	/**
	 * 获取图片的小缩略图
	 * @param fileName 文件名字
	 * @param scale
	 * @return
	 */
	public static Bitmap GetThumbImage(String fileName, int w, int h)
	{
		Bitmap result=null;
		try
		{
			Options op=new Options();
		 	op.inJustDecodeBounds=true;
		 	Bitmap bmp= BitmapFactory.decodeFile(fileName, op);
		 	int x=(int)(op.outWidth/(w*1.0));
		 	int y=(int)(op.outHeight/(h*1.0));
		 	int scale=x>y?y:x;
		 	Options options=new Options();
			options.inSampleSize=scale;
			result= BitmapFactory.decodeFile(fileName, options);
			//System.out.println("scale:"+scale);
		}
		catch(RuntimeException e)
		{
			System.out.println("RuntimeException获取缩略图出错："+e.getMessage());
			e.printStackTrace();
			return null;
		}
		catch(Exception e)
		{
			System.out.println("获取缩略图出错："+e.getMessage());
			e.printStackTrace();
			return null;
		}

		return result;
	}
	/**
	 * 将值写入至配置文件
	 * @param c
	 * @param fileName
	 * @param key
	 * @param value
	 */
	public static void WriteLocal(Context c, String fileName, String key, String value)
	{
		SharedPreferences pref=c.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor=pref.edit();
		editor.putString(key, value);
		editor.commit();
	}
	/**
	 * 读取相应的值
	 * @param c
	 * @param fileName
	 * @param key
	 * @return
	 */
	public static String ReadLocal(Context c, String fileName, String key)
	{
		SharedPreferences pref=c.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return pref.getString(key, null);
	}
	/**
	 * 删除相应的键
	 * @param c
	 * @param fileName
	 * @param key
	 */
	public static void RemoveLocal(Context c, String fileName, String key)
	{
		SharedPreferences pref=c.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor=pref.edit();
		editor.remove(key);
		editor.commit();
	}
	/**
	 * 将用户名与密码及服务器名生成一个独一无二的文件名字
	 * @param server 服务器名
	 * @param userName 用户名
	 * @param password 密码
	 * @return 生成文件名
	 */
	public static String ToFileName(String server, String userName, String password)
	{
		String ser=server.replace(":", "").replace(".", "").replace("//", "");//将.与:去掉
		return ser+userName+password;
	}
	/**
	 * 内存卡是否可以使用
	 * @return 可以用为true,不可以用为false
	 */
	public static boolean isSDCardAvaible()
	{
		String state= Environment.getExternalStorageState();
		if(!state.equals(Environment.MEDIA_MOUNTED))
		{
			return false;
		}

		return true;
	}
	/**
	 * 检查更新，如果需要更新，则返回下载URL，否则返回null
	 * @return
	 */
	/*public static UpdateInfo IsUpdate(Context c)
	{
		UpdateInfo info=new UpdateInfo();
		String xml=GetUpdateXml(c);
		if(xml==null)
		{
			info.State=UpdateInfo.CONNECT_FAIL;
			return info;
		}
		ParseUpdate parse=new ParseUpdate(xml);
		System.out.println("服务器版本："+parse.getVersionCode()+"本地版本："+GetVersionCode(c));
		if(parse.getVersionCode()>GetVersionCode(c))
		{
			System.out.println("需要更新URL"+parse.getUrl());
			info.State=UpdateInfo.NEED_UPDATE;
			info.Url=parse.getUrl();
			info.Version=parse.getVersionCode();
			info.Log=parse.getLog();
			return info;
		}
		else
		{
			System.out.println("不需要更新URL");
			info.State=UpdateInfo.LATEST_VERSTION;
			info.Url=parse.getUrl();
			info.Version=parse.getVersionCode();
			info.Log=parse.getLog();
			return info;
		}
	}*/
	/**
	 * 得到版本的VersionCode
	 * @param c
	 * @return
	 */
	public static int GetVersionCode(Context c)
	{
		String pName = c.getPackageName();
		try
		{
			PackageInfo pinfo = c.getPackageManager().getPackageInfo(pName, PackageManager.GET_CONFIGURATIONS);
			return pinfo.versionCode;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return -1;
	}
	/**
	 * 得到版本的VersionCode
	 * @param c
	 * @return
	 */
	public static String GetVersionName(Context c)
	{
		String pName = c.getPackageName();
		try
		{
			PackageInfo pinfo = c.getPackageManager().getPackageInfo(pName, PackageManager.GET_CONFIGURATIONS);
			return pinfo.versionName;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return "3.0";
	}
	/**
	 * 得到更新的URL的XML文件
	 * @param c
	 * @return
	 */
	/*public static String GetUpdateXml(Context c)
	{
		String address=Config.UPDATE_URL+"&v="+GetVersionCode(c);
		System.out.println("请求地址："+address);
		String xml=null;
		URL url;
		HttpURLConnection http;
		byte[] buffer=new byte[1024];
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		InputStream in;
		try
		{
			url=new URL(address);
			http=(HttpURLConnection) url.openConnection();
			http.setReadTimeout(8000);
			http.setConnectTimeout(8000);
			in=http.getInputStream();
			int len=in.read(buffer);
			while(len!=-1)
			{
				out.write(buffer, 0, len);
				len=in.read(buffer);
			}
			xml=new String(out.toByteArray());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("获取更新XML失败");
			return null;
		}
		System.out.println("获取更新XML成功"+xml);
		return xml;
	}*/
	public static void OpenAPKFile(String fileName, Context context)
	{
		File file = new File(fileName);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/*private static void GetLeafNode(Vector<UnitItem> tree,UnitItem node)
	{
		if(node.isDir)//找到的目录
		{
			for(int i=0;i<tree.size();i++)//查找node下的直接子节点
			{
				UnitItem item=tree.get(i);
				if(item.isDir)//
				{
					if(item.Gitem.parentid.equals(node.Gitem.id))
					{
						GetLeafNode(tree,item);
					}
				}
				else
				{
					if(item.Titem.parentid.equals(node.Gitem.id))
					{
						item.Titem.route=item.route;
						LeafList.add(item.Titem);
					}
				}
			}
			
		}
		else
		{
			node.Titem.route=node.route;
			LeafList.add(node.Titem);
		}
	
	}
	private static List<MyTreeItem> GetAllLeaf(Vector<UnitItem> tree)
	{
		if(LeafList==null)
		{
			LeafList=new ArrayList<MyTreeItem>();
		}
		else
		{
			LeafList.clear();
		}
		for(int i=0;i<tree.size();i++)
		{
			if(!tree.get(i).isDir)
			{
				tree.get(i).Titem.route=tree.get(i).route;
				LeafList.add(tree.get(i).Titem);
				
			}
		}
		System.out.println("所有叶子节点数量:"+LeafList.size());
		return LeafList;
	}
	*//**
	 * 得到node结点下的所有间接或者直接的叶子结点
	 * @param tree 树
	 * @param node 为空表示要获取所有点
	 * @return
	 *//*
	public static List<MyTreeItem> GetLeafList(Vector<UnitItem> tree,UnitItem node)
	{
		if(node==null)//为空表示要获取所有点
		{
			return GetAllLeaf(tree);
		}
		if(LeafList==null)
		{
			LeafList=new ArrayList<MyTreeItem>();
		}
		else
		{
			LeafList.clear();
		}
		GetLeafNode(tree,node);
		return LeafList;
	}
	public static List<MyTreeItem> GetLeafList()
	{
		return LeafList;
	}*/
}
