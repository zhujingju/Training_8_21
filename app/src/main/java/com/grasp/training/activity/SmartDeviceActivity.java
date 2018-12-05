package com.grasp.training.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.fragmet.SmartHomeMain;
import com.grasp.training.service.MqttService;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.DataStatus;
import com.grasp.training.tool.Goods;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;
import com.grasp.training.tool.myActivityManage;
import com.grasp.training.view.MyGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SmartDeviceActivity extends BaseMqttActivity {  //设备列表
    @BindView(R.id.smart_device_fh)
    ImageView smartDeviceFh;
    @BindView(R.id.smart_device_save)
    TextView smartDeviceSave;
    @BindView(R.id.smart_device_head)
    LinearLayout head;
    @BindView(R.id.smart_device_MyGridView)
    MyGridView gridView;
    private Context context;
    private List<Goods> list;
    private List<Goods> oldlist;
    private myListViewAdapter adapter;
    private DataStatus dataStatus;
    private boolean if_else;
    private int num;

    public static void startActivity(Context context,DataStatus dataStatus,int num){
        Intent in=new Intent(context,SmartDeviceActivity.class);
        in.putExtra("SmartDeviceActivity",dataStatus);
        in.putExtra("SmartDeviceActivity2",num);
        context.startActivity(in);

    }

    @Override
    public String getMyTopic() {
        return MqttService.myTopicDevice;
    }

    @Override
    public String getMyTopicDing() {
        return MqttService.myTopicDevice;
    }

    @Override
    public String getSid() {
        return "";
    }


    @Override
    public int setLayoutId() {
        return R.layout.smart_device_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context=getContext();
        dataStatus=(DataStatus) getIntent().getSerializableExtra("SmartDeviceActivity");
        if(dataStatus==null){
            if_else=false;
            dataStatus=new DataStatus();
        }else{
            if_else=true;
        }
        num=getIntent().getIntExtra("SmartDeviceActivity2",-1);
    }

    @Override
    public void initView() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        //获取PullToRefreshGridView里面的head布局
        head.addView(gridView.getView(), p);
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
    public void MyMessageArrived(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.e("qqq", "home messs=" + message);
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
                    switch (cmd) {
                        case "querydevicebyuser_ok":
//                            Log.e("qqq", "home messs=" + "querydevicebyuser_ok");
                            SharedPreferencesUtils.setParam(context, MainActivity.NameUser, message);
                            setJson(message);
                            break;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    private void initListview() {
        gridView.setCacheColorHint(0);
        adapter = new myListViewAdapter(context, list);
        dataL();
        handler.sendEmptyMessageDelayed(123, 0);

        gridView.setAdapter(adapter);
        gridView.setonRefreshListener(new MyGridView.OnRefreshListener() { //刷新

            @Override
            public void onRefresh() {
                handler.removeMessages(123);
                handler.sendEmptyMessageDelayed(123, 0);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("qqq","gridView name="+list.get(i).getName());
                Goods g=list.get(i);
                if(if_else){
//                    dataStatus.setIf_add(true);
                    dataStatus.setElse_left("设备-"+g.getName());
                    dataStatus.setElse_num(1);
                    dataStatus.setElse_num1_name(g.getSid());
                    dataStatus.setElse_num1_type(g.getType());
                    dataStatus.setElse_num1_name(g.getName());

                    DeviceStatusActivity.startActivity(getContext(),if_else,dataStatus,num);
                }else{

                    DataStatus dataStatus=new DataStatus();
                    dataStatus.setIf_add(true);
                    dataStatus.setIf_left("设备-"+g.getName());
                    dataStatus.setIf_num(4);
                    dataStatus.setNum4_sid(g.getSid());
                    dataStatus.setNum4_type(g.getType());
                    dataStatus.setNum4_name(g.getName());
                    DeviceStatusActivity.startActivity(getContext(),if_else,dataStatus,num);
                }

            }
        });


    }

    @OnClick({R.id.smart_device_fh, R.id.smart_device_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.smart_device_fh:
                finish();
                break;
            case R.id.smart_device_save:
                RoomActivity.startActivity(context,null,fj);
                break;
        }
    }

    private String fj="";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    fj=data.getStringExtra("name");
                    ArrayList<Goods> mlist=new ArrayList<>();
                    for(Goods g:oldlist){
                        Log.e("qqq","-------- fj="+fj+"  room="+g.getRoom());
                        String room=g.getRoom();
                        if(fj.equals(room)){

                            mlist.add(g);
                        }
                    }
                    list=mlist;
                    adapter.setList(list);
                    handler.sendEmptyMessageDelayed(1000,0);
                }
                break;
        }

    }


    public void dataListview() {  //获取list数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                push_read();
            }
        }).start();

    }

    public void push_read() {  //获取设备列表
//        Log.e("qqq","消息 push_read");
        try {
            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "querydevicebyuser");
            jsonObject.put("uname", MainActivity.NameUser);
            jsonObject.put("clientid", Tool.getIMEI(getContext()));
            String js = jsonObject.toString();

            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 123:
                    Log.e("qqq", "home=" + 123);
                    handler.removeMessages(123);
                    Log.e("qqq", "home=" + isConnected());
                    if (isConnected()) {
                        dataListview();
                    } else {
                        handler.sendEmptyMessageDelayed(123, 1000);
                    }


                    break;

                case 1000:
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    gridView.onRefreshComplete();

                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1000);
        handler.removeMessages(123);
    }

    private void dataL() {
        String message = SharedPreferencesUtils.getParam(context, MainActivity.NameUser, "").toString();
        setJson(message);


    }

    private void setJson(String json) {
        try {
            list = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String sid = jsonObject1.optString("sid", "");//名称
                String type = jsonObject1.optString("type", "");//类型
                String thumbnail = jsonObject1.optString("thumbnail", ""); //预览图
                String state = jsonObject1.optString("state", "");
                String roomid = jsonObject1.optString("roomid", "");
                String alive = jsonObject1.optString("alive", "0");
                String dname = jsonObject1.optString("dname", "");
                String weburl = jsonObject1.optString("weburl", "");
                String roomname=jsonObject1.optString("roomname", "");
                if (roomid.equals("null")) {
                    roomid = "";
                }
                if (thumbnail.equals("null")) {
                    thumbnail = "";
                }
                if (state.equals("null")) {
                    state = "";
                }

                Goods goods = new Goods();
                goods.setRoom(roomname);
                goods.setSid(sid);
                goods.setWz(roomid);
                goods.setIm_url(thumbnail);
                goods.setAdd_zt(false);
                goods.setType(type);
                goods.setWeburl(weburl);
                if (state.equals("on")) {
                    goods.setDy(true);
                } else {
                    goods.setDy(false);
                }

                goods.setName(dname);
//                                Log.e("qqq","oldMap="+newMap);

                list.add(goods);
            }
            oldlist=list;
            handler.sendEmptyMessageDelayed(1000, 0);
        } catch (JSONException e) {
            e.printStackTrace();
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
            if (convertView == null) {
                view = inflater.inflate(R.layout.items_smart_device, parent, false);
                too = new ListViewTool();
//
                too.name = (TextView) view.findViewById(R.id.items_smart_device_tv1);
                too.im = (ImageView) view.findViewById(R.id.items_smart_device_im);

                view.setTag(too);
            } else {
                too = (ListViewTool) view.getTag();
            }
            if (camera == null) return null;
            // too.del.setOnClickListener(click);

            //  too.up.setTag(position);
                too.name.setText(camera.getName());
                if (camera.getIm_url()==null||camera.getIm_url().equals("")) {
                    too.im.setBackgroundResource(R.color.white);
                } else {
                    ImageLoader.getInstance().displayImage(camera.getIm_url(), too.im, MyApplication.options);
                }


            return view;
        }

        class ListViewTool {

            public TextView name;
            public ImageView im;
        }
    }

}
