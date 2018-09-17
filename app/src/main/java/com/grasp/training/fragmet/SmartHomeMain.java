package com.grasp.training.fragmet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.grasp.training.activity.EquipmentActivity;
import com.grasp.training.activity.SearchActivity;
import com.grasp.training.activity.SockeActivity;
import com.grasp.training.tool.BaseMqttFragment;
import com.grasp.training.tool.EquipmentData;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;
import com.grasp.training.view.MyGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SmartHomeMain extends BaseMqttFragment {

    Unbinder unbinder;
    @BindView(R.id.smart_tv_wd)
    TextView smartTvWd;
    @BindView(R.id.smart_tv_sd)
    TextView smartTvSd;
    @BindView(R.id.smart_tv_pm)
    TextView smartTvPm;
    @BindView(R.id.smart_home_MyGridView)
    MyGridView gridView;
    Unbinder unbinder1;
    private String myTopic = "iotbroad/iot/device";
    private Context context;
    private List<Goods> list ;
    private myListViewAdapter adapter;
    private int posi=0;
    private boolean del_zt=false;

    @Override
    public int getInflate() {
        return R.layout.smart_home_main;
    }

    @Override
    public void init(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        context = getActivity();

        head = (LinearLayout) rootView.findViewById(R.id.head);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        //获取PullToRefreshGridView里面的head布局
        head.addView(gridView.getView(), p);
        initListview();
    }

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
                Log.e("qqq","home messs="+message);
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
                    switch (cmd){
                        case "querydevicebyuser_ok":
                            list=new ArrayList<>();
                            JSONArray jsonArray=jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String sid = jsonObject1.optString("sid", "");//名称
                                String type = jsonObject1.optString("type", "");//类型
                                String thumbnail = jsonObject1.optString("thumbnail", ""); //预览图
                                String state = jsonObject1.optString("state", "");
                                String roomid = jsonObject1.optString("roomid", "");
                                String alive = jsonObject1.optString("alive", "0");
                                if(roomid.equals("null")){
                                    roomid="";
                                }
                                if(thumbnail.equals("null")){
                                    thumbnail="";
                                }
                                if(state.equals("null")){
                                    state="";
                                }

                                Goods goods=new Goods();
                                goods.setSid(sid);
                                goods.setWz(roomid);
                                goods.setIm_url(thumbnail);
                                goods.setAdd_zt(false);
                                goods.setType(type);
                                if(state.equals("on")){
                                    goods.setDy(true);
                                }else{
                                    goods.setDy(false);
                                }
                                if(eMap.get(type)!=null){
                                    goods.setName(eMap.get(type));
                                }else{
                                    goods.setName("");
                                }
                                if(alive.equals("1")){
                                    goods.setJh_zt(true);
                                }else{
                                    goods.setJh_zt(false);
                                }
                                list.add(goods);

                            }
                            Goods goods=new Goods();
                            goods.setAdd_zt(true);
                            list.add(goods);
                            handler.sendEmptyMessageDelayed(1000,500);
                            break;

                        case "deletedevice_ok":
                            handler.sendEmptyMessageDelayed(2000,500);

                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void add(ArrayList<String> slist) {
        if (MainActivity.NameUser.equals("")) {
            Toast.makeText(context, getString(R.string.uid_no), Toast.LENGTH_LONG).show();
            return;
        }
        Intent in = new Intent(context, SearchActivity.class);
        in.putStringArrayListExtra("sid_List", slist);
        context.startActivity(in);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeMessages(1000);
        handler.removeMessages(2000);
        unbinder.unbind();
    }

    @OnClick(R.id.smart_hone_main_add)
    public void onViewClicked() {
        add(new ArrayList<String>());
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
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        setEquipmentData();
        dataListview();
    }

    private LinearLayout head;
    private void initListview() {
        gridView.setCacheColorHint(0);
        adapter=new myListViewAdapter(context, list);
//        dataListview();


        gridView.setAdapter(adapter);
        gridView.setonRefreshListener(new MyGridView.OnRefreshListener() { //刷新

            @Override
            public void onRefresh() {
                dataListview();

            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("qqq","i="+i);
                //删除设备
                if(i+1==list.size()) {

                }else{
                    posi=i;
                    del();

                }
                return true;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(i+1==list.size()){
                    ArrayList<String> arrayList=new ArrayList();
                    for(int j=0;j<list.size();j++){
                        arrayList.add(list.get(i).getSid());
                    }
                    add(arrayList);
                }else{
                    if(list.get(i).getType().equals("socket")){
                        SockeActivity.starstSockeActivity(context,list.get(i).getSid());
                    }else{
                        EquipmentActivity.starstEquipmentActivity(context,list.get(i).getSid(),list.get(i).getType());
                    }
                }
            }
        });



    }





    public void dataListview() {  //获取list数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                push_read();
            }
        }).start();
    }

    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:

                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    gridView.onRefreshComplete();
                    break;
                case 2000:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    Toast.makeText(context,getString(R.string.progress_pergood),Toast.LENGTH_LONG).show();
                    dataListview();
                    break;
            }
        }
    };



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


    private void delData() {
        push_del();
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
                    jsonObject.put("cmd", "deletedevice");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("sid", list.get(posi).getSid());
                    String js = jsonObject.toString();

                    publish_String(js);  //主题
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }

    class myListViewAdapter extends BaseAdapter {

        LayoutInflater inflater = null;
        private Context context;
        private List<Goods> list=null;

        public myListViewAdapter(Context context,List<Goods> list) {
            this.context=context;
            this.list=list;
            inflater = ((Activity)(context)).getLayoutInflater();
        }


        public List<Goods> getList() {
            return list;
        }



        public void setList(List<Goods> list) {
            this.list = list;
        }



        @Override
        public int getCount() {
            if(list!=null){
                return list.size();
            }
            return 0;
        }

        @Override
        public Goods getItem(int arg0) {
            if(list!=null){
                return list.get(arg0);
            }
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView( int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view=convertView;
            Goods camera= getItem(position);
            ListViewTool too;
            if(convertView==null){
                view = inflater.inflate(R.layout.items_smart_home, parent, false);
                too=new ListViewTool();
//
                too.name=(TextView) view.findViewById(R.id.items_smart_home_tv1);
                too.wz=(TextView) view.findViewById(R.id.items_smart_home_tv2);
                too.dy=(TextView) view.findViewById(R.id.items_smart_home_tv3);
                too.im=(ImageView) view.findViewById(R.id.items_smart_home_im);
                too.layout2=(LinearLayout) view.findViewById(R.id.items_smart_home_rel2);
                too.layout=(RelativeLayout) view.findViewById(R.id.items_smart_home_rel);

                view.setTag(too);
            }
            else {
                too = (ListViewTool) view.getTag();
            }
            if(camera == null) return null;
            // too.del.setOnClickListener(click);

            //  too.up.setTag(position);
            if(camera.isAdd_zt()){
                too.layout.setVisibility(View.GONE);
                too.layout2.setVisibility(View.VISIBLE);
            }else{
                too.layout.setVisibility(View.VISIBLE);
                too.layout2.setVisibility(View.GONE);
                too.name.setText(camera.getName());
                too.wz.setText(camera.getWz());
                if(camera.isDy()){
                    too.dy.setText("状态：开启");
                }else{
                    too.dy.setText("状态：关闭");
                }
                if(camera.isJh_zt()){
                    too.name.setTextColor(getResources().getColor(R.color.c_000000));
                    too.wz.setTextColor(getResources().getColor(R.color.c_b32f54e9));
                    too.dy.setTextColor(getResources().getColor(R.color.c_000000));
                    too.wz.setText("在线");
                }else{
                    too.name.setTextColor(getResources().getColor(R.color.main_hui));
                    too.wz.setTextColor(getResources().getColor(R.color.main_hui));
                    too.dy.setTextColor(getResources().getColor(R.color.main_hui));
                    too.wz.setText("离线");
                }

            }

            return view;
        }

        class ListViewTool {

            public TextView name ,wz,dy;
            public ImageView im;
            public RelativeLayout layout;
            public LinearLayout layout2;
        }
    }


    class Goods {
        private String name;//名称
        private String wz;//位置
        private boolean dy; //是否开启
        private boolean add_zt;
        private String Im_url;
        private String Sid;
        private String type;
        private boolean jh_zt;  //设备是否在线

        public boolean isJh_zt() {
            return jh_zt;
        }

        public void setJh_zt(boolean jh_zt) {
            this.jh_zt = jh_zt;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSid() {
            return Sid;
        }

        public void setSid(String sid) {
            Sid = sid;
        }

        public String getIm_url() {
            return Im_url;
        }

        public void setIm_url(String im_url) {
            Im_url = im_url;
        }

        public boolean isAdd_zt() {
            return add_zt;
        }

        public void setAdd_zt(boolean add_zt) {
            this.add_zt = add_zt;
        }

        public String getWz() {
            return wz;
        }

        public void setWz(String wz) {
            this.wz = wz;
        }

        public boolean isDy() {
            return dy;
        }

        public void setDy(boolean dy) {
            this.dy = dy;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }



    private ArrayList<EquipmentData> elist;
    private HashMap<String,String> eMap;

    private void setEquipmentData() {
        String data = SharedPreferencesUtils.getParam(getContext(), MainActivity.MainData, "").toString();
//        Log.e("qqq","data: "+data);
        elist = new ArrayList<>();
        eMap=new HashMap<>();
        if (!data.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray js = jsonObject.getJSONArray("data");
                for (int i = 0; i < js.length(); i++) {
                    JSONObject jsonObject1 = js.getJSONObject(i);
                    String dname = jsonObject1.optString("dname", "");//名称
                    String type = jsonObject1.optString("type", "");//类型
                    String thumbnail = jsonObject1.optString("dname", ""); //预览图
                    String stateall = jsonObject1.optString("stateall", "");
                    EquipmentData equipmentData = new EquipmentData();
                    equipmentData.setDname(dname);
                    equipmentData.setType(type);
                    equipmentData.setStateall(stateall);
                    equipmentData.setThumbnail(thumbnail);
                    elist.add(equipmentData);
                    eMap.put(type,dname);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
