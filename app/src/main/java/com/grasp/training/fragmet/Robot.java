package com.grasp.training.fragmet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.Umeye_sdk.AcSearchDevice;
import com.grasp.training.activity.ControlActivity;
import com.grasp.training.swipemenulistview.SwipeMenu;
import com.grasp.training.swipemenulistview.SwipeMenuCreator;
import com.grasp.training.swipemenulistview.SwipeMenuItem;
import com.grasp.training.swipemenulistview.SwipeMenuListView;
import com.grasp.training.tool.AddSQLiteHelper;
import com.grasp.training.tool.BaseMqttFragment;
import com.grasp.training.tool.Tool;
import com.zs.easy.mqtt.IEasyMqttCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Robot extends BaseMqttFragment {
    @BindView(R.id.jqr)
    Button jqr;
    @BindView(R.id.robot_add)
    Button robotAdd;
    Unbinder unbinder;
    @BindView(R.id.robot_listview)
    SwipeMenuListView toulistView;
    private List<Goods> toulist;
    private myListViewAdapter adapter;

    @Override
    public int getInflate() {
        return R.layout.robot;
    }

    @Override
    public void init(View v) {
        unbinder = ButterKnife.bind(this, v);
        initlistView();
    }

    private String myTopic = "iotbroad/iot/robotwithuser";

    @Override
    public String getMyTopic() {
        return myTopic;
    }

    @Override
    public String getMyTopicDing() {
        return myTopic;
    }

    @Override
    public void MyMessageArrived(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("qqq","Robote_add messs="+message);
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.getString("cmd");
                    String uname = jsonObject.optString("uname", "");  //
                    if (!uname.equals(MainActivity.NameUser)) {
                        return;
                    }
                    String clientid = jsonObject.optString("clientid", "");
                    if (!clientid.equals(Tool.getIMEI(getContext()))) {
                        return;
                    }
                    switch (cmd) {
                        case "queryrobotid_ok":
                            toulist = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String rid = jsonObject1.optString("rid", "");//id
                                Goods g = new Goods();
                                g.setName(rid);
                                toulist.add(g);
                            }
                            handler.sendEmptyMessageDelayed(999,0);
                            break;
                        case "deleterobot_ok":
                            handler.sendEmptyMessageDelayed(2000,0);
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    public void push_read() {  //获取机器人
//        Log.e("qqq","消息 push_read");
        try {
            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "queryrobotid");
            jsonObject.put("uname", MainActivity.NameUser);
            jsonObject.put("clientid", Tool.getIMEI(getContext()));
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick({R.id.jqr, R.id.robot_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.jqr:
                context.startActivity(new Intent(context, ControlActivity.class));
                break;
            case R.id.robot_add:
                context.startActivity(new Intent(context, AcSearchDevice.class));
                break;
        }
    }


    private void initlistView() {
        toulistView.setCacheColorHint(0);
        adapter = new myListViewAdapter(context, toulist);
//        dataListview();

        View la = LayoutInflater.from(context).inflate(R.layout.add_items, null);
        toulistView.addFooterView(la);
        toulistView.setAdapter(adapter);

        toulistView.setonRefreshListener(new SwipeMenuListView.OnRefreshListener() { //刷新

            @Override
            public void onRefresh() {
                dataListview();

            }
        });

        toulistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                i = i - 1;
                Log.e("qqq",i+"=i "+" toulist.s="+toulist.size());
                if (i == toulist.size()) {
                    context.startActivity(new Intent(context, AcSearchDevice.class));
                } else {
                    Intent in = new Intent(context, ControlActivity.class);
                    in.putExtra("uid", toulist.get(i).getName());
                    context.startActivity(in);
                }

            }
        });


        SwipeMenuCreator creator = new SwipeMenuCreator() {


            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        context);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(70));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        toulistView.setMenuCreator(creator);

        // step 2. listener item click event
        toulistView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //				Emp_layout_1.Goods item = toulist.get(position);
                switch (index) {
                    //				case 0:
                    //					// open
                    //					open(item);
                    //					break;
                    case 0:
                        // delete
                        //					delete(item);
                        //					list.remove(position);
                        //					adapter.notifyDataSetChanged();

                        posi = position;
                        handler.sendEmptyMessage(888);


                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        toulistView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 888:
                    del();
                    break;
                case 999:
                    adapter.setList(toulist);
                    toulistView.onRefreshComplete();
                    adapter.notifyDataSetChanged();
                    ss_zt = true;
                    break;
                case 2000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, R.string.progress_pergood, Toast.LENGTH_SHORT).show();
                    dataListview();
                    break;
                case 2001:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, R.string.progress_pergood2, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void dataListview() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                push_read();
//                toulist = new ArrayList<Goods>();
////
////                AddSQLiteHelper dbHelper = new AddSQLiteHelper(context, "add.db", null, 1);
////                SQLiteDatabase db = dbHelper.getWritableDatabase();
////                String s = "select * from AddEquipment ";
////                Cursor cursor = db.rawQuery(s, null);
////                while (cursor.moveToNext()) {
////
////                    String uid = cursor.getString(1);//获取第2列的值
////
////                    Goods g = new Goods();
////                    g.setName(uid);
////                    toulist.add(g);
////
////                }
////                ha.sendEmptyMessage(999);
            }
        }).start();


    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private int posi = 0;

    private void del() {   //删除

        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle(getString(R.string.shuru2) + "“"
                + toulist.get(posi).getName() + "”");
        builder.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                delData();
            }
        });

        builder.setNegativeButton(
                getString(R.string.alert_dialog_cancel), null);
        builder.show();
    }

    private boolean ss_zt = false;

    private void delData() {
//        AddSQLiteHelper dbHelper = new AddSQLiteHelper(context, "add.db", null, 1);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        String s = "delete from  AddEquipment    where uid='" + toulist.get(posi).getName() + "'";
//        db.execSQL(s);
//        Toast.makeText(context, R.string.progress_pergood, Toast.LENGTH_SHORT).show();
        push_del();
//        dataListview();
    }


    private ProgressDialog dialog;

    public void showPro() {

        dialog = new ProgressDialog(context);
        dialog.setMessage("删除中...");
        dialog.setCancelable(true);

        dialog.show();
    }

    public void push_del() {  //删除机器人
//        Log.e("qqq","消息 push_read");

        showPro();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "deleterobot");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("rid", toulist.get(posi).getName());
                    String js = jsonObject.toString();

                    publish_String(js);  //主题
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            dataListview();
        }
    }


    //	@Override
    //	protected void onNewIntent(Intent intent) {
    //		super.onNewIntent(intent);
    //		Log.d("qqq","onNewIntent");
    //		if(adapter!=null){
    //			dataListview();
    //		}
    //	}


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeMessages(888);
        handler.removeMessages(999);
        handler.removeMessages(2000);
        unbinder.unbind();
    }

    class myListViewAdapter extends BaseAdapter {

        LayoutInflater inflater = null;
        private Context context;
        private List<Goods> list = null;

        public myListViewAdapter(Context context, List<Goods> list) {
            this.context = context;
            this.list = list;
            inflater = ((Activity) (context)).getLayoutInflater();
        }


        public List<Goods> getList() {
            return list;
        }


        public void setList(List<Goods> list) {
            this.list = list;
        }


        @Override
        public int getCount() {
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        @Override
        public Goods getItem(int arg0) {
            if (list != null) {
                return list.get(arg0);
            }
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = convertView;
            final Goods camera = getItem(position);
            final ListViewTool too;
            if (convertView == null) {
                view = inflater.inflate(R.layout.sw_item, parent, false);
                too = new ListViewTool();

                too.name = (TextView) view.findViewById(R.id.ybd_tv);

                view.setTag(too);
            } else {
                too = (ListViewTool) view.getTag();
            }
            if (camera == null) return null;

            too.name.setText(camera.getName());
//            too.last.setBackgroundResource(R.drawable.d1);
//            if (list.size() > 1) {
//                if (position == list.size() - 1) {
//                    too.last.setBackgroundResource(R.drawable.d2);
//                } else {
//
//                }
//            } else {
//
//                too.last.setBackgroundResource(R.drawable.d2);
//
//            }

            //			if(camera.getName().equals(Uid)){
            //				too.ss.setState(true);
            //			}


            return view;
        }

        class ListViewTool {

            public LinearLayout last;
            public TextView name;
            public Button set;
        }
    }


    class Goods {
        private String name;
        private int num;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }
}
