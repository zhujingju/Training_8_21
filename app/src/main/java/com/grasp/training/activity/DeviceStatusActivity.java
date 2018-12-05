package com.grasp.training.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.DataStatus;
import com.grasp.training.tool.EquipmentData;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.myActivityManage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceStatusActivity extends BaseActivity {
    @BindView(R.id.device_status_fh)
    ImageView deviceStatusFh;
    @BindView(R.id.device_status_tv)
    TextView deviceStatusTv;
    @BindView(R.id.device_status_listview)
    ListView listview;//设备 if  else 的状态
    private Context context;
    private List<Goods> list;
    private myListViewAdapter adapter;

    private boolean if_else;
    private String type;
    private DataStatus dataStatus;
    private int num;

    public static void startActivity(Context context, boolean if_else, DataStatus dataStatus,int num) {  //if_else：判断是if进来还是else进来  if是false  qian else用到
        Intent in = new Intent(context, DeviceStatusActivity.class);
        in.putExtra("DeviceStatusActivity", if_else);
        in.putExtra("DeviceStatusActivity2", dataStatus);
        in.putExtra("DeviceStatusActivity3", num);
        context.startActivity(in);
    }

    @Override
    public int setLayoutId() {
        return R.layout.device_status_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context = getContext();
        setEquipmentData();
        Intent in = getIntent();
        if_else = in.getBooleanExtra("DeviceStatusActivity", false);
        dataStatus = (DataStatus) in.getSerializableExtra("DeviceStatusActivity2");
        num=in.getIntExtra("DeviceStatusActivity3",-1);
        if (if_else) {
            deviceStatusTv.setText(dataStatus.getElse_num1_name());
            type=dataStatus.getElse_num1_type();
        } else {
            deviceStatusTv.setText(dataStatus.getNum4_name());
            type=dataStatus.getNum4_type();
        }
        Log.e("DeviceStatusActivity","type="+type);
        initListview();
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


    @OnClick(R.id.device_status_fh)
    public void onViewClicked() {
        finish();
    }

    private void initListview() {
        listview.setCacheColorHint(0);
        adapter = new myListViewAdapter(context, list);
        dataListview();
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                if(num!=-1){
                    if (if_else) {//跳到新建页
                        dataStatus.setElse_right(list.get(arg2).getName());
                        myActivityManage.removeAll();
                        NewIntelligentActivity.startActivity(getContext(), dataStatus, true,num);
                    }else{
                        dataStatus.setIf_right(list.get(arg2).getName());
                        myActivityManage.removeAll();
                        NewIntelligentActivity.startActivity(getContext(), dataStatus, false,num);
                    }

                    return;
                }
                if (if_else) {//跳到新建页
                    dataStatus.setElse_right(list.get(arg2).getName());
                    myActivityManage.removeAll();
                    NewIntelligentActivity.startActivity(getContext(), dataStatus, "");
                } else {  //跳到else页
                    dataStatus.setIf_right(list.get(arg2).getName());
                    myActivityManage.removeAll();
                    CarriedOutActivity.startCarriedOutActivity(getContext(), dataStatus, 0,0,0);
                }
            }
        });
    }

    public void dataListview() {  //获取list数据

        new Thread() {
            @Override
            public void run() {

                if (if_else) {
                    list = type_list_z.get(type);
                } else {
                    list = type_list_t.get(type);
                }

                adapter.setList(list);
                ha.sendEmptyMessageDelayed(100, 0);

            }
        }.start();
    }


    private ArrayList<EquipmentData> elist;
    private HashMap<String, String> eMap;
    private HashMap<String, ArrayList<Goods>> type_list_t;
    private HashMap<String, ArrayList<Goods>> type_list_z;

    private void setEquipmentData() {
        String data = SharedPreferencesUtils.getParam(getContext(), MainActivity.MainData, "").toString();
//        Log.e("DeviceStatusActivity","data="+data);
//        Log.e("qqq","data: "+data);
        elist = new ArrayList<>();
        eMap = new HashMap<>();
        type_list_t = new HashMap<>();
        type_list_z = new HashMap<>();

        if (!data.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray js = jsonObject.getJSONArray("data");
                for (int i = 0; i < js.length(); i++) {
                    ArrayList<Goods> prerequisitesList = new ArrayList<>();
                    ArrayList<Goods> carryoutsList = new ArrayList<>();
                    JSONObject jsonObject1 = js.getJSONObject(i);
                    String dname = jsonObject1.optString("dname", "");//名称
                    String type = jsonObject1.optString("type", "");//类型
                    String thumbnail = jsonObject1.optString("dname", ""); //预览图
                    String stateall = jsonObject1.optString("stateall", "");


                    JSONArray prerequisites = jsonObject1.optJSONArray("prerequisites");

                    if(prerequisites!=null){
                        for (int j = 0; j < prerequisites.length(); j++) {
                            JSONObject prerequisites_js = prerequisites.getJSONObject(j);
                            String prerequisite = prerequisites_js.optString("prerequisite", "");
                            int conid = prerequisites_js.optInt("conid", -1);
                            if (!prerequisite.equals("")) {
                                Goods ds = new Goods();
                                ds.setId(conid);
                                ds.setName(prerequisite);
                                prerequisitesList.add(ds);

                            }

                        }
                    }



                    JSONArray carryouts = jsonObject1.optJSONArray("carryouts");
//                    Log.e("DeviceStatusActivity","carryouts="+carryouts);
                    if(carryouts!=null){
                        for (int j = 0; j < carryouts.length(); j++) {
                            JSONObject carryouts_js = carryouts.getJSONObject(j);
                            String carryout = carryouts_js.optString("carryout", "");
                            int carrid = carryouts_js.optInt("carrid", -1);
                            if (!carryout.equals("")) {
                                Goods ds = new Goods();
                                ds.setId(carrid);
                                ds.setName(carryout);
                                carryoutsList.add(ds);
                                Log.e("DeviceStatusActivity","carryout="+carryout);
                            }
                        }
                    }


                    EquipmentData equipmentData = new EquipmentData();
                    equipmentData.setDname(dname);
                    equipmentData.setType(type);
                    equipmentData.setStateall(stateall);
                    equipmentData.setThumbnail(thumbnail);
                    elist.add(equipmentData);
                    eMap.put(type, dname);
                    type_list_t.put(type, prerequisitesList);
                    type_list_z.put(type, carryoutsList);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }


    Handler ha = new Handler() {
        String xx;

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:

                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

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
                view = inflater.inflate(R.layout.items_device_status, parent, false);
                too = new ListViewTool();
//
                too.name = (TextView) view.findViewById(R.id.items_device_status_tv);


                view.setTag(too);
            } else {
                too = (ListViewTool) view.getTag();
            }
            if (camera == null) return null;
            // too.del.setOnClickListener(click);

            //  too.up.setTag(position);
            too.name.setText(camera.getName());

            return view;
        }

        class ListViewTool {

            public TextView name;
        }
    }


    class Goods {
        private String name;
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
