 package com.grasp.training.Umeye_sdk;

 import android.app.Activity;
 import android.app.AlertDialog;
 import android.app.ProgressDialog;
 import android.content.Context;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.database.Cursor;
 import android.database.sqlite.SQLiteDatabase;
 import android.graphics.Color;
 import android.graphics.drawable.ColorDrawable;
 import android.os.AsyncTask;
 import android.os.Build;
 import android.os.Bundle;
 import android.os.Handler;
 import android.os.Message;
 import android.preference.PreferenceManager;
 import android.support.annotation.RequiresApi;
 import android.support.v4.app.Fragment;
 import android.util.Log;
 import android.util.TypedValue;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.View.OnClickListener;
 import android.view.ViewGroup;
 import android.widget.AdapterView;
 import android.widget.BaseAdapter;
 import android.widget.Button;
 import android.widget.ImageView;
 import android.widget.LinearLayout;
 import android.widget.TextView;
 import android.widget.Toast;

 import com.Player.Core.PlayerClient;
 import com.Player.Source.TSearchDev;
 import com.grasp.training.MainActivity;
 import com.grasp.training.R;
 import com.grasp.training.activity.Robote_add_activity;
 import com.grasp.training.swipemenulistview.SwipeMenuListView;
 import com.grasp.training.tool.BaseActivity;
 import com.grasp.training.tool.MyApplication;
 import com.grasp.training.tool.SharedPreferencesUtils;

 import java.util.ArrayList;
 import java.util.List;

 public class AcSearchDevice extends BaseActivity {
     public ShowProgress pd;
     private SwipeMenuListView listView;
//     private SwipeMenuListView toulistView;
     private SearchDeviceAdapter sAdapter;
     public static ArrayList<SearchDeviceInfo> list;

     private MyApplication appMain;

     public static  boolean AcSearchDevice_zt=false;

     private Context context;

     @Override
     public int setLayoutId() {
         return R.layout.ac_search_devce;
     }

     @Override
     public void initData() {
         context=getContext();
         AcSearchDevice_zt=true;
         initListView();
     }

     @Override
     public void initView() {

     }

     @Override
     public void initObject() {

     }

     @Override
     public void initListener() {

     }

     @Override
     public void init() {

     }



     public void initListView() {
         appMain = (MyApplication)((Activity) context).getApplication();

 //		sp= PreferenceManager.getDefaultSharedPreferences(context);
 //		Uid=sp.getString(DemoApplication.UID, "");

         listView = (SwipeMenuListView) findViewById(R.id.lvLive);
 //		listView.setVisibility(View.INVISIBLE);
         sAdapter = new SearchDeviceAdapter(context);
         View la= LayoutInflater.from(context).inflate(R.layout.list_qita, null);
         listView.addFooterView(la);
         listView.setAdapter(sAdapter);

         listView.setCacheColorHint(0);
         listView.setonRefreshListener(new SwipeMenuListView.OnRefreshListener() { //刷新

             @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
             @Override
             public void onRefresh() {

                 th2=new ThreadSearchDevice2();
                 th2.execute();

             }
         });



         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

             @Override
             public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                     long arg3) {
                 // TODO Auto-generated method stub
                 arg2-=1;
 //				Toast.makeText(AcSearchDevice.this,arg2+" ",Toast.LENGTH_SHORT).show();
                 if(arg2==list.size()){
 //					Toast.makeText(AcSearchDevice.this,"qt",Toast.LENGTH_SHORT).show();
 					startActivityForResult(new Intent(AcSearchDevice.this, Robote_add_activity.class),100);
                     finish();
                 }
             }
         });

 //		findViewById(R.id.menu_btn1).setOnClickListener(new OnClickListener() {
 //
 //			@Override
 //			public void onClick(View v) {
 //				// TODO Auto-generated method stub
 //				new ThreadSearchDevice().execute();
 //			}
 //		});
         th=new ThreadSearchDevice();
         th.execute();
     }



     private ThreadSearchDevice th;
     private ThreadSearchDevice2 th2;







     @Override
     public void onResume() {
         Log.d("qqq","onResume");
         if (sAdapter != null) {
             sAdapter.notifyDataSetChanged();
         }
 //		if(adapter!=null){
 //			dataListview();
 //		}
         super.onResume();

     }

 //	@Override
 //	protected void onNewIntent(Intent intent) {
 //		super.onNewIntent(intent);
 //		Log.d("qqq","onNewIntent");
 //		if(adapter!=null){
 //			dataListview();
 //		}
 //	}



     @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
     @Override
     protected   void onDestroy() {
         // TODO Auto-generated method stub
         super.onDestroy();
         AcSearchDevice_zt=false;
         if(th != null && th.getStatus() == AsyncTask.Status.RUNNING){
             th.cancel(true);
         }
         if(th2 != null && th2.getStatus() == AsyncTask.Status.RUNNING){
             th2.cancel(true);
         }
     }



     @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
     public class ThreadSearchDevice extends
             AsyncTask<Void, Void, List<SearchDeviceInfo>> {

         @Override
         protected List<SearchDeviceInfo> doInBackground(Void... params) {
             // TODO Auto-generated method stub
             list = new ArrayList<SearchDeviceInfo>();
             // String[] temp = StreamData.ServerAddress.split(":");
             // String address = temp[0];
             // int port = Integer.parseInt(temp[1]);
             // String userName = StreamData.UserName;
             // String password = StreamData.Password;
             // System.out.println(address + ":" + port + "  " + userName + "  "
             // + password);
             PlayerClient playerclient = appMain.getPlayerclient();
             int searchRet = playerclient.StartSearchDev(10);// 5代表等待多少秒
             for (int i = 0; i < searchRet; i++) {
                 TSearchDev tsearch = playerclient.SearchDevByIndex(i);

                 SearchDeviceInfo searchInfo = new SearchDeviceInfo(
                         tsearch.dwVendorId, tsearch.sDevName, tsearch.sDevId,
                         tsearch.sDevUserName, tsearch.bIfSetPwd,
                         tsearch.bIfEnableDhcp, tsearch.sAdapterName_1,
                         tsearch.sAdapterMac_1, tsearch.sIpaddr_1,
                         tsearch.sNetmask_1, tsearch.sGateway_1,
                         tsearch.usChNum, tsearch.iDevPort, tsearch.sDevModel,
                         tsearch.currentIp, tsearch.connectState,
                         tsearch.iSrvConnResult);
                 Log.w("searchRet", "UMId :" + searchInfo.toString());
                 list.add(searchInfo);

             }
             playerclient.StopSearchDev();
             return list;
         }

         @Override
         protected void onPostExecute(List<SearchDeviceInfo> flist) {
             // TODO Auto-generated method stub
             pd.dismiss();
             if (list.size() > 0) {
 //				listView.setVisibility(View.VISIBLE);
                 sAdapter.setNodeList(flist);
                 // listView.startLayoutAnimation();
             } else {
 //				listView.setVisibility(View.INVISIBLE);
 //				SearchDeviceInfo a=new SearchDeviceInfo(2,"aaaa","aaaa","aaaa",2,2,"aaaa","aaaa","aaaa","aaaa","aaaa",2,2,"aaa","aaaa",2,2);
 //				list = new ArrayList<SearchDeviceInfo>();
 //				list.add(a);
 //				list.add(a);
 //				list.add(a);
 //				list.add(a);
 //				list.add(a);
 //				list.add(a);
 //				list.add(a);
                 sAdapter.setNodeList(flist);
                 Show.toast(context, R.string.nodataerro);
             }

             super.onPostExecute(list);
         }

         @Override
         protected void onPreExecute() {
             // TODO Auto-generated method stub
             if (pd == null) {
                 pd = new ShowProgress(context);
                 pd.setMessage(AcSearchDevice.this.getResources().getString(
                         R.string.searching_device));
                 pd.setCanceledOnTouchOutside(true);
             }
             if(pd!=null){
                 pd.show();
             }

             super.onPreExecute();
         }
     }


     @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
     public class ThreadSearchDevice2 extends
             AsyncTask<Void, Void, List<SearchDeviceInfo>> {

         @Override
         protected List<SearchDeviceInfo> doInBackground(Void... params) {
             // TODO Auto-generated method stub
             list = new ArrayList<SearchDeviceInfo>();
             // String[] temp = StreamData.ServerAddress.split(":");
             // String address = temp[0];
             // int port = Integer.parseInt(temp[1]);
             // String userName = StreamData.UserName;
             // String password = StreamData.Password;
             // System.out.println(address + ":" + port + "  " + userName + "  "
             // + password);
             PlayerClient playerclient = appMain.getPlayerclient();
             int searchRet = playerclient.StartSearchDev(10);// 5代表等待多少秒
             for (int i = 0; i < searchRet; i++) {
                 TSearchDev tsearch = playerclient.SearchDevByIndex(i);

                 SearchDeviceInfo searchInfo = new SearchDeviceInfo(
                         tsearch.dwVendorId, tsearch.sDevName, tsearch.sDevId,
                         tsearch.sDevUserName, tsearch.bIfSetPwd,
                         tsearch.bIfEnableDhcp, tsearch.sAdapterName_1,
                         tsearch.sAdapterMac_1, tsearch.sIpaddr_1,
                         tsearch.sNetmask_1, tsearch.sGateway_1,
                         tsearch.usChNum, tsearch.iDevPort, tsearch.sDevModel,
                         tsearch.currentIp, tsearch.connectState,
                         tsearch.iSrvConnResult);
                 Log.w("searchRet", "UMId :" + searchInfo.toString());
                 list.add(searchInfo);

             }
             playerclient.StopSearchDev();
             return list;
         }

         @Override
         protected void onPostExecute(List<SearchDeviceInfo> flist) {
             // TODO Auto-generated method stub
 //			pd.dismiss();
             if (list.size() > 0) {
 //				listView.setVisibility(View.VISIBLE);
                 sAdapter.setNodeList(flist);
                 // listView.startLayoutAnimation();
             } else {
 //				listView.setVisibility(View.INVISIBLE);
                 sAdapter.setNodeList(flist);
                 Show.toast(context, R.string.nodataerro);
             }
             listView.onRefreshComplete();
             super.onPostExecute(list);
         }

         @Override
         protected void onPreExecute() {
             // TODO Auto-generated method stub
 //			if (pd == null) {
 //				pd = new ShowProgress(AcSearchDevice.this);
 //				pd.setMessage(AcSearchDevice.this.getResources().getString(
 //						R.string.searching_device));
 //				pd.setCanceledOnTouchOutside(false);
 //			}
 //			pd.show();
             super.onPreExecute();
         }
     }



     class SearchDeviceAdapter extends BaseAdapter {
         public static final int MODIFY_DIR_SUCCESS = 4;
         public static final int MODIFY_DIR_FIALED = 5;
         private List<SearchDeviceInfo> nodeList;
         private Context con;
         private LayoutInflater inflater;
         // View view;
         public TextView txtParameters, txtName, txtDelete;
         int currentPosition;
         public ProgressDialog progressDialog;
         public boolean parentIsDvr = false;
         private SharedPreferences sp;

         public SearchDeviceAdapter(Context con) {
             this.con = con;
             inflater = LayoutInflater.from(con);
             nodeList = new ArrayList<SearchDeviceInfo>();
             sp = PreferenceManager.getDefaultSharedPreferences(con);
             // editor = con.getSharedPreferences(FgFavorite.fileName,
             // Context.MODE_PRIVATE).edit();
         }

         public List<SearchDeviceInfo> getNodeList() {
             return nodeList;
         }

         public void setNodeList(List<SearchDeviceInfo> nodeList) {
             this.nodeList = nodeList;
             notifyDataSetChanged();
         }

         public boolean isParentIsDvr() {
             return parentIsDvr;
         }

         public void setParentIsDvr(boolean parentIsDvr) {
             this.parentIsDvr = parentIsDvr;
         }

         @Override
         public int getCount() {
             // TODO Auto-generated method stub
             return nodeList.size();
         }

         @Override
         public Object getItem(int position) {
             // TODO Auto-generated method stub
             return null;
         }

         @Override
         public long getItemId(int position) {
             // TODO Auto-generated method stub
             return position;
         }

         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
             // TODO Auto-generated method stub
             SearchDeviceInfo node = nodeList.get(position);
             ViewHolder vh = null;
             if (convertView == null) {
                 vh = new ViewHolder();
                 convertView = inflater.inflate(R.layout.search_device_item, null);
                 vh.tv = (TextView) convertView.findViewById(R.id.tvCaption);
                 vh.info = (TextView) convertView.findViewById(R.id.tvInfo);

                 vh.add = (Button) convertView.findViewById(R.id.btn_add);

                 convertView.setTag(vh);
             } else {
                 vh = (ViewHolder) convertView.getTag();
             }
 //			vh.tv.setText(node.currentIp + "  " + node.getsDevId() + "  "
 //					+ node.usChNum);
             vh.tv.setText(node.getsDevId());
             vh.info.setText(node.serverState);
             OnClickListstener clickListener = new OnClickListstener(node, position);
             // vh.imgaArrow.setOnClickListener(clickListener);
             vh.add.setOnClickListener(clickListener);
             return convertView;
         }

         class ViewHolder {
             TextView tv;
             TextView info;
             Button add;
         }

         public class OnClickListstener implements OnClickListener

         {
             SearchDeviceInfo node;
             int position;

             public OnClickListstener(SearchDeviceInfo node, int position) {
                 this.node = node;
                 this.position = position;
             }

             @Override
             public void onClick(View v) {
                 if (v.getId() == R.id.btn_add) {

 				Activity activity = (Activity) con;
 				Intent intent = new Intent(con, Robote_add_activity.class);
 					intent.putExtra("umid", node.getsDevId());
 				activity.startActivityForResult(intent,100);
 				finish();

                 }
             }
         }
     }




 }
