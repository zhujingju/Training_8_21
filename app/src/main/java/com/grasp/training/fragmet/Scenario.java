package com.grasp.training.fragmet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.activity.ConditionActivity;
import com.grasp.training.activity.NewIntelligentActivity;
import com.grasp.training.service.MqttService;
import com.grasp.training.swipemenulistview.SwipeMenu;
import com.grasp.training.swipemenulistview.SwipeMenuCreator;
import com.grasp.training.swipemenulistview.SwipeMenuItem;
import com.grasp.training.swipemenulistview.SwipeMenuListView;
import com.grasp.training.tool.BaseMqttFragment;
import com.grasp.training.tool.DataStatus;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Scenario extends BaseMqttFragment {
    @BindView(R.id.scenario_add)
    ImageView scenarioAdd;
    @BindView(R.id.scenario_listview)
    SwipeMenuListView listview;
    Unbinder unbinder;

    private List<Goods> list;
    private myListViewAdapter adapter;
    private int posi = 0;

    @Override
    public void onStart() {
        super.onStart();
        dataListview();
    }

    @Override
    public int getInflate() {
        return R.layout.scenario;
    }

    @Override
    public void init(View v) {
        unbinder = ButterKnife.bind(this, v);
        initListview();
    }

    @Override
    public String getMyTopic() {
        return MqttService.myTopicLogic;
    }

    @Override
    public String getMyTopicDing() {
        return MqttService.myTopicLogic;
    }

    @Override
    public void MyMessageArrived(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.getString("cmd");
                    String uname = jsonObject.optString("uname", "");  //
                    if (!uname.equals(MainActivity.NameUser)) {
                        return;
                    }
                    String clientid = jsonObject.optString("clientid", "");
                    if (!clientid.equals(Tool.getIMEI(getContext()))) {
                        Log.e("qqq", "home clientid=" + clientid);
                        return;
                    }
                    Message m;
                    String err;
                    switch (cmd) {
                        case "querylogic_ok":
                            SharedPreferencesUtils.setParam(context,"Scenario",message);
                            initData(message);
                            break;
                        case "querylogic_failed":
                            err = jsonObject.optString("err", "");
                            m = new Message();
                            m.obj = err;
                            m.what = 1001;
                            handler.sendMessageDelayed(m, 500);
                            break;

                        case "deletelogic_ok":
                            handler.sendEmptyMessageDelayed(2000,500);
                            break;

                        case "deletelogic_failed":
                            err = jsonObject.optString("err", "");
                            m = new Message();
                            m.obj = err;
                            m.what = 2001;
                            handler.sendMessageDelayed(m, 500);
                            break;

                        case "carryoutlogic_ok":
                            handler.sendEmptyMessageDelayed(3000,500);
                            break;

                        case "carryoutlogic_failed":
                            err = jsonObject.optString("err", "");
                            m = new Message();
                            m.obj = err;
                            m.what = 3001;
                            handler.sendMessageDelayed(m, 500);
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void initData(String s) {

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.optJSONArray("logics");
            list = new ArrayList<>();
            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String loname = jsonObject1.optString("loname", "");
                    int loid = jsonObject1.optInt("loid", -1);
                    JSONArray jsonArray1=jsonObject1.optJSONArray("lopres");

                    boolean l_zt=false;
                    if(jsonArray1!=null&&jsonArray1.length()==1){
                        JSONObject jso=jsonArray1.getJSONObject(0);
                        int pretype=jso.optInt("pretype",-1);
                        if(pretype==1){
                            l_zt=true;
                        }
                    }

                    Goods g = new Goods();
                    g.setName(loname);
                    g.setId(loid);
                    g.setJson(jsonObject1.toString());
                    g.setZt(l_zt);

                    list.add(g);
                }
                adapter.setList(list);
                handler.sendEmptyMessageDelayed(1000, 500);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private ProgressDialog dialog;

    public void showPro1() {

        dialog = new ProgressDialog(context);
        dialog.setMessage("删除中...");
        dialog.setCancelable(true);

        dialog.show();
    }
    public void showPro3() {

        dialog = new ProgressDialog(context);
        dialog.setMessage("发送中...");
        dialog.setCancelable(true);

        dialog.show();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    adapter.notifyDataSetChanged();
                    listview.onRefreshComplete();
                    break;
                case 1001:
                    Toast.makeText(context, "获取失败，" + msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;

                case 2000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "删除成功", Toast.LENGTH_LONG).show();
                    list.remove(posi);
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    listview.onRefreshComplete();
                    break;

                case 2001:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "删除失败，" + msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;

                case 3000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "发送成功", Toast.LENGTH_LONG).show();
                    break;

                case 3001:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "发送失败，" + msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        handler.removeMessages(1000);
        handler.removeMessages(1001);
        handler.removeMessages(2000);
        handler.removeMessages(2001);
        super.onDestroyView();
        unbinder.unbind();

    }

    @OnClick(R.id.scenario_add)
    public void onViewClicked() {
        context.startActivity(new Intent(context, ConditionActivity.class));
    }


    private void initListview() {
        listview.setCacheColorHint(0);
        adapter = new myListViewAdapter(context, list);
        String sl=(String)SharedPreferencesUtils.getParam(context,"Scenario","");
        if(!sl.equals("")){
            initData(sl);
        }
        listview.setAdapter(adapter);
        listview.setonRefreshListener(new SwipeMenuListView.OnRefreshListener() { //刷新

            @Override
            public void onRefresh() {
                dataListview();

            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                arg2--;
                NewIntelligentActivity.startActivity(context, new DataStatus(), list.get(arg2).getJson());
            }
        });


        SwipeMenuCreator creator = new SwipeMenuCreator() {


            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
//				SwipeMenuItem openItem = new SwipeMenuItem(
//						getApplicationContext());
                // set item background
//				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//						0xCE)));
//				// set item width
//				openItem.setWidth(dp2px(90));
//				// set item title
//				openItem.setTitle("Open");
//				// set item title fontsize
//				openItem.setTitleSize(18);
//				// set item title font color
//				openItem.setTitleColor(Color.WHITE);
//				// add to menu
//				menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
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
        listview.setMenuCreator(creator);

        // step 2. listener item click event
        listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Goods item = list.get(position);
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

                        del();


                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        listview.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void del() {   //删除

        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle(getString(R.string.shuru2) + "“"
                + list.get(posi).getName() + "”");
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

    private void delData() { // del
        showPro1();
        push_del();
    }


    public void dataListview() {  //获取list数据
        push_read();

    }

    public void push_read() {  //获取状态
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "querylogic");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public void push_del() {  //删除
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "deletelogic");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("loid", list.get(posi).getId());
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();


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
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = convertView;
            Goods camera = getItem(position);
            ListViewTool too;
            onClick onClick = null;
            if (convertView == null) {
                view = inflater.inflate(R.layout.items_scenario, parent, false);
                too = new ListViewTool();
//
                too.name = (TextView) view.findViewById(R.id.items_scenario_tv);
                too.zx = (TextView) view.findViewById(R.id.items_scenario_tv2);


                view.setTag(too);
            } else {
                too = (ListViewTool) view.getTag();
            }
            if (camera == null) return null;
            // too.del.setOnClickListener(click);

            //  too.up.setTag(position);
            onClick = new onClick();
            too.zx.setTag(position);
            too.zx.setOnClickListener(onClick);
            too.name.setText(camera.getName());
            if (camera.isZt()) {
                too.zx.setVisibility(View.VISIBLE);
            } else {
                too.zx.setVisibility(View.GONE);
            }

            return view;
        }

        class ListViewTool {

            public TextView name, zx;
        }

        class onClick implements View.OnClickListener {


            @Override
            public void onClick(View v) {
                int position;
                position = (Integer) v.getTag();
//                Log.e("qqq", "执行=" + list.get(position).getName());
//                Toast.makeText(context,"发送成功",Toast.LENGTH_LONG).show();
                push_song(list.get(position).getId());
                showPro3();
            }
        }
    }


    class Goods {
        private String name;
        private boolean zt;
        private int id;
        private String json;

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isZt() {
            return zt;
        }

        public void setZt(boolean zt) {
            this.zt = zt;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public void push_song(final int loid) {  //
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "carryoutlogic");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("loid",loid);
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
