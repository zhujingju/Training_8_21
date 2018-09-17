package com.grasp.training.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.grasp.training.R;
import com.grasp.training.swipemenulistview.SwipeMenu;
import com.grasp.training.swipemenulistview.SwipeMenuCreator;
import com.grasp.training.swipemenulistview.SwipeMenuItem;
import com.grasp.training.swipemenulistview.SwipeMenuListView2;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.view.SlideSwitch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class TimingActivity extends BaseMqttActivity {

    private List<Goods> list;
    private myListViewAdapter adapter;
    private int posi = 0;
    private SwipeMenuListView2 listview;
    private Context context;
    private String sid="";
    private String type="";

    @Override
    public int setLayoutId() {
        return R.layout.timing_activity;
    }

    @Override
    public void initData() {
        context = getContext();
        sid=getIntent().getStringExtra("Timing_sid")+"";
        type=getIntent().getStringExtra("Timing_type")+"";
    }

    @Override
    public void initView() {
        initListview();
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

    @Override
    public String getMyTopic() {
        return "iotbroad/iot/"+type+"/"+sid;
    }

    @Override
    public String getMyTopicDing() {
        return "iotbroad/iot/"+type+"_ack/"+sid;
    }

    @Override
    public String getSid() {
        return sid;
    }

    @Override
    public void MyMessageArrived(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("qqq", "Timing messageArrived  message= " + message);
                try {
                    JSONObject jsonF;
                    Message me;
                    String js = "";
                    String channel_0 = "";
                    int var = 0;
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.getString("cmd");
                    String mSid = jsonObject.optString("sid", "");
                    if (!mSid.equals(sid)) {
                        return;
                    }

                    if(cmd.equals("wifi_"+type+"_read_timing_ack")){
                        list = new ArrayList<>();
                        JSONArray jsonArray=jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jso= (JSONObject) jsonArray.get(i);
                            Goods g1 = new Goods();
                            String time=jso.optString("time");
                            String tm[]=time.split(",");
                            String on_off="";
                            if(tm.length==4){
                                g1.setTime_on(tm[0]);
                                g1.setTime_off(tm[1]);
                                g1.setDay(tm[2]);
                                on_off=tm[3];
                            }

//                                g1.setTime_on(jso.getString("ontime"));
//                                g1.setTime_off(jso.getString("offtime"));
//                                g1.setDay(jso.getString("day"));
                            g1.setTimer(jso.getInt("timer"));
//                                String on_off=jso.getString("timer_state");
                            if(on_off.equals("on")){
                                g1.setZt(true);
                            }else{
                                g1.setZt(false);
                            }

                            list.add(g1);
                        }

                        myhandler.sendEmptyMessageDelayed(100, 500);
                    }else if(cmd.equals("wifi_"+type+"_del_timing_ack")){
                        myhandler.sendEmptyMessageDelayed(200, 500);
                    }else if(cmd.equals("wifi_"+type+"_timing_ack")){
                        myhandler.sendEmptyMessageDelayed(300, 500);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }

    public void fh(View view) {
        finish();
    }

    public void add(View view) {
        Intent in=new Intent(context,AddTimingActivity.class);
        in.putExtra("Timing_s","");
        in.putExtra("Timing_sid",sid);
        in.putExtra("Timing_type",type);
        startActivity(in);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataListview();
    }

    public String getJs(Goods g){

        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("day",g.getDay());
            jsonObject.put("ontime",g.getTime_on());
            jsonObject.put("offtime",g.getTime_off());
            jsonObject.put("timer",g.getTimer());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();

            return "";
        }

    }

    private void initListview() {
        listview = (SwipeMenuListView2) findViewById(R.id.timing_listview);
        listview.setCacheColorHint(0);
        adapter = new myListViewAdapter(context, list);

        listview.setAdapter(adapter);
        listview.setonRefreshListener(new SwipeMenuListView2.OnRefreshListener() { //刷新

            @Override
            public void onRefresh() {
                dataListview();

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("qqq","post="+position+"  list.s="+list.size()+" ");
                position=position-2;

                Intent in=new Intent(context,AddTimingActivity.class);
                in.putExtra("Timing_s", getJs(list.get(position)));
                in.putExtra("Timing_sid",sid);
                in.putExtra("Timing_type",type);
                startActivity(in);
            }
        });



        SwipeMenuCreator creator = new SwipeMenuCreator() {


            @Override
            public void create(SwipeMenu menu) {

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
        listview.setOnMenuItemClickListener(new SwipeMenuListView2.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Goods item = list.get(position);
                switch (index) {
                    case 0:

                        posi = position;

                        del();


                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        listview.setOnSwipeListener(new SwipeMenuListView2.OnSwipeListener() {

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
        String time="暂无";
        if(list.get(posi).getTime_on().equals("close")){

        }else{
            time=list.get(posi).getTime_on();
        }
        if(list.get(posi).getTime_off().equals("close")){
        }else{
            if(!time.equals("暂无")){
                time+=" —— "+list.get(posi).getTime_off();
            }else{
                time=list.get(posi).getTime_off();
            }
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle("是否删除" + "“"
                + time+ "”的定时设置");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                delData();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }



    private ProgressDialog dialog;
    public void showPro(){

        dialog = new ProgressDialog(this);
        dialog.setMessage("删除中...");
        dialog.setCancelable(true);
        dialog.show();
    }
    private void delData() { // del
        if (sid.equals("")) {
                    Toast.makeText(context, "没有设备", Toast.LENGTH_LONG).show();
            return;
        }

        showPro();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_"+type+"_del_timing");
                    jsonObject.put("sid", sid);
                    jsonObject.put("timer", list.get(posi).getTimer());
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }




    public void dataListview() {  //获取list数据
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (sid.equals("")) {
//                    Toast.makeText(context, "先添加设备", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "wifi_"+type+"_read_timing");
                    jsonObject.put("sid", sid);
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        myhandler.removeMessages(100);
        myhandler.removeMessages(200);
        myhandler.removeMessages(300);
    }

    Handler myhandler = new Handler() {
        String xx;

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {


                case 100:
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    listview.onRefreshComplete();
                    break;
                case 200:
                    if(dialog!=null){
                        dialog.cancel();
                    }
                    Toast.makeText(context,"删除成功", Toast.LENGTH_LONG).show();
                    dataListview();
                    break;
                case 300:
//                    if(dialog!=null){
//                        dialog.cancel();
//                    }
//                    Toast.makeText(context,"删除成功",Toast.LENGTH_LONG).show();
                    dataListview();
                    break;

            }
        }
    };



    public void push_save(String ontime, String offtime, String week, int timer, boolean timer_state) {
        if (sid.equals("")) {
            Toast.makeText(context, "先添加设备", Toast.LENGTH_LONG).show();
            return;
        }
        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "wifi_"+type+"_timing");
            jsonObject.put("day", week);
            jsonObject.put("sid", sid);
            if(timer_state){
                jsonObject.put("timer_state", "on");
            }else{
                jsonObject.put("timer_state", "off");
            }
            jsonObject.put("ontime", ontime);
            jsonObject.put("offtime", offtime);
            jsonObject.put("timer", timer);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
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
            onSlideSwitch onSlideSwitch;
            onclick onclick;
            if (convertView == null) {
                view = inflater.inflate(R.layout.items_timing, parent, false);
                too = new ListViewTool();
                too.time = (TextView) view.findViewById(R.id.items_timing_tv1);
                too.time1 = (TextView) view.findViewById(R.id.items_timing_tv2);
                too.slideSwitch= (SlideSwitch) view.findViewById(R.id.items_timing_ss);
                too.relativeLayout= (RelativeLayout) view.findViewById(R.id.items_timing_rel);
                view.setTag(too);
            } else {
                too = (ListViewTool) view.getTag();
            }
            if (camera == null) return null;
            too.relativeLayout.setTag(position);
            onclick=new onclick();
            too.relativeLayout.setOnTouchListener(onclick);

            too.slideSwitch.setTag(position);
            onSlideSwitch=new onSlideSwitch(too.slideSwitch);
            too.slideSwitch.setSlideListener(onSlideSwitch);
            String time="暂无";
            if(camera.getTime_on().equals("close")){

            }else{
                time=camera.getTime_on();
            }
            if(camera.getTime_off().equals("close")){
            }else{
                if(!time.equals("暂无")){
                    time+=" —— "+camera.getTime_off();
                }else{
                    time=camera.getTime_off();
                }
            }

            too.time.setText(time);
            too.time1.setText(setWeekView( camera.getDay()));
            if(camera.isZt()){
                too.slideSwitch.setState(true);
            }else{
                too.slideSwitch.setState(false);
            }

            return view;
        }

        class ListViewTool {

            public TextView time, time1;
            public SlideSwitch slideSwitch;
            public RelativeLayout relativeLayout;
        }

        class  onSlideSwitch implements SlideSwitch.SlideListener{
            SlideSwitch slideSwitch;
            public onSlideSwitch(SlideSwitch slideSwitch){
                this.slideSwitch=slideSwitch;
            }


            @Override
            public void open() {
                int post=((int)slideSwitch.getTag());
//                slideSwitch.setState(true);
                push_save(list.get(post).getTime_on(),list.get(post).getTime_off(),list.get(post).getDay(),list.get(post).getTimer(),true);
                Log.e("qqq","post="+post);
            }

            @Override
            public void close() {
                int post=((int)slideSwitch.getTag());
                push_save(list.get(post).getTime_on(),list.get(post).getTime_off(),list.get(post).getDay(),list.get(post).getTimer(),false);

//                slideSwitch.setState(false);
                Log.e("qqq","post="+post);
            }
        }

        class  onclick implements View.OnTouchListener{


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("qqq",motionEvent.getAction()+"  aaa");

                return true;
            }
        }

    }


    class Goods {
        private String time_on, time_off,day;  //开启时间 ，关闭时间，周几
        private boolean zt; //是否开启
        private int timer; //标识

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public int getTimer() {
            return timer;
        }

        public void setTimer(int timer) {
            this.timer = timer;
        }

        public String getTime_on() {
            return time_on;
        }

        public void setTime_on(String time_on) {
            this.time_on = time_on;
        }

        public String getTime_off() {
            return time_off;
        }

        public void setTime_off(String time_off) {
            this.time_off = time_off;
        }


        public boolean isZt() {
            return zt;
        }

        public void setZt(boolean zt) {
            this.zt = zt;
        }

    }



    private String setWeekView(String s) {
        boolean z1, z2, z3, z4, z5, z6, z7;
        String we = "永不";
        if (s.length() == 7) {
            we="";
            if (s.substring(0, 1).equals("1")) {
                z1 = true;
                we += "周一";
            } else {
                z1 = false;
            }

            if (s.substring(1, 2).equals("2")) {
                z2 = true;
                if (we.equals("")) {
                    we += "周二";
                } else {
                    we += "，周二";
                }
            } else {
                z2 = false;
            }

            if (s.substring(2, 3).equals("3")) {
                z3 = true;
                if (we.equals("")) {
                    we += "周三";
                } else {
                    we += "，周三";
                }
            } else {
                z3 = false;
            }

            if (s.substring(3, 4).equals("4")) {
                z4 = true;
                if (we.equals("")) {
                    we += "周四";
                } else {
                    we += "，周四";
                }
            } else {
                z4 = false;
            }
            if (s.substring(4, 5).equals("5")) {
                z5 = true;
                if (we.equals("")) {
                    we += "周五";
                } else {
                    we += "，周五";
                }
            } else {
                z5 = false;
            }
            if (s.substring(5, 6).equals("6")) {
                z6 = true;
                if (we.equals("")) {
                    we += "周六";
                } else {
                    we += "，周六";
                }
            } else {
                z6 = false;
            }
            if (s.substring(6, 7).equals("7")) {
                z7 = true;
                if (we.equals("")) {
                    we += "周日";
                } else {
                    we += "，周日";
                }
            } else {
                z7 = false;
            }
            if (z1 && z2 && z3 && z4 & z5 && z6 && z7) {
                we = "每天";
            } else if (!z1 && !z2 && !z3 && !z4 & !z5 && !z6 && !z7) {
                we = "永不";
            } else if (z1 && z2 && z3 && z4 & z5 && !z6 && !z7) {
                we = "工作日";
            } else if (!z1 && !z2 && !z3 && !z4 & !z5 && z6 && z7) {
                we = "周末";
            }

        }
        return we;
    }



}
