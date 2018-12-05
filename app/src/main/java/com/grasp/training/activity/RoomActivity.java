package com.grasp.training.activity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.fragmet.Robot;
import com.grasp.training.service.MqttService;
import com.grasp.training.swipemenulistview.SwipeMenu;
import com.grasp.training.swipemenulistview.SwipeMenuCreator;
import com.grasp.training.swipemenulistview.SwipeMenuItem;
import com.grasp.training.swipemenulistview.SwipeMenuListView;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.Goods;
import com.grasp.training.tool.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomActivity extends BaseMqttActivity {
    private List<Goods> list ;
    private myListViewAdapter adapter;
    private int posi=0;

    @BindView(R.id.room_fh)
    ImageView roomFh;
    @BindView(R.id.room_listview)
    SwipeMenuListView listview;
    @BindView(R.id.room_add)
    ImageView roomAdd;

    private Context context;
    private String sid="";
    private String roomname;
    private boolean zt=false;  //是否要改变设备的房间号

    public static void startActivity(Context context,String sid,String roomname){
        Intent in=new Intent(context,RoomActivity.class);
        in.putExtra("RoomActivity",sid);
        in.putExtra("RoomActivity2",roomname);
        ((Activity)context).startActivityForResult(in,1);

    }

    @Override
    public String getMyTopic() {
        return MqttService.myTopicUser;
    }

    @Override
    public String getMyTopicDing() {
        return MqttService.myTopicUser;
    }

    @Override
    public String getSid() {
        return "";
    }


    @Override
    public int setLayoutId() {
        return R.layout.room_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        context=this;
        initListview();
        sid=getIntent().getStringExtra("RoomActivity");
        if(sid==null){
            zt=false;
        }else{
            zt=true;
        }
        roomname=getIntent().getStringExtra("RoomActivity2");
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

    @Override
    public void MyMessageArrived(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.optString("cmd");
                    String uname = jsonObject.optString("uname", "");  //
                    if (!uname.equals(MainActivity.NameUser)) {
                        return;
                    }
                    String clientid = jsonObject.optString("clientid", "");
                    if (!clientid.equals(Tool.getIMEI(getContext()))) {
                        return;
                    }
                    switch (cmd) {
                        case "queryroomgather_ok":
                            list = new ArrayList<>();
                            String roomgather = jsonObject.optString("roomgather", "");
                            String room[]=roomgather.split("☯");
                            for (int i = 0; i < room.length; i++) {
                                String rid = room[i];
                                Goods g = new Goods();
                                g.setName(rid);
                                list.add(g);
                            }
                            handler.sendEmptyMessageDelayed(999,0);
                            break;
                        case "updateroomgather_ok":
                            handler.sendEmptyMessageDelayed(2000,0);
                            break;
                        case "updateroomgather_failed":
                            String err = jsonObject.optString("err", "");

                            Message m=new Message();
                            m.what=2001;
                            m.obj=err;
                            handler.sendMessageDelayed(m,500);
                            break;
                        case "updatedeviceroomname_ok":
                            if(sid==null){
                                return;
                            }
                            String msid = jsonObject.optString("sid", "");
                            if(!msid.equals(sid)){
                                return;
                            }

                            String roomname = jsonObject.optString("roomname", "");

                            Message m3=new Message();
                            m3.what=3000;
                            m3.obj=roomname;
                            handler.sendMessageDelayed(m3,500);
                            break;
                        case "updatedeviceroomname_failed":
                            String err2 = jsonObject.optString("err", "");

                            Message m2=new Message();
                            m2.what=3001;
                            m2.obj=err2;
                            handler.sendMessageDelayed(m2,500);
                            break;

                        case "updateroomgatherbysingle_ok":
                            handler.sendEmptyMessageDelayed(2000,0);
                            break;
                        case "updateroomgatherbysingle_failed":
                            String err4 = jsonObject.optString("err", "");

                            Message m4=new Message();
                            m4.what=2001;
                            m4.obj=err4;
                            handler.sendMessageDelayed(m4,500);
                            break;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    @OnClick({R.id.room_fh, R.id.room_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.room_fh:
                finish();
                break;
            case R.id.room_add:
                add();
                break;
        }
    }


    public void add( ) {
        final EditText et = new EditText(this);
        et.setText("");
        new AlertDialog.Builder(this).setTitle("添加房间")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "内容不能为空！" + input, Toast.LENGTH_LONG).show();
                        } else {
                            if(input.length()>6){
                                Toast.makeText(getApplicationContext(), "房间名不可超过6个字！" + input, Toast.LENGTH_LONG).show();
                                return;
                            }

                            for(Goods fj:list){
                                String name=fj.getName();
                                if(input.equals(name)){
                                    Toast.makeText(getApplicationContext(), "房间名已存在！" + input, Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }

                            push_add(input);
                            showPro1();
                        }
                    }
                })
                .setNegativeButton("取消", null).show();

    }

    private void initListview() {
        listview.setCacheColorHint(0);
        adapter=new myListViewAdapter(context, list);
        dataListview();
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
                Log.e("qqq",list.get(arg2).getName()+" -------");
                if(zt){
                    showPro2();
                    push_set(list.get(arg2).getName());
                }else{
                    Intent in=new Intent();
                    in.putExtra("name",list.get(arg2).getName());
                    setResult(RESULT_OK,in);
                    finish();
                }

            }
        });


//
//        SwipeMenuCreator creator = new SwipeMenuCreator() {
//
//
//
//            @Override
//            public void create(SwipeMenu menu) {
//
//                // create "delete" item
//                SwipeMenuItem deleteItem = new SwipeMenuItem(
//                        getContext());
//                // set item background
//                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
//                        0x3F, 0x25)));
//                // set item width
//                deleteItem.setWidth(dp2px(70));
//                // set a icon
//                deleteItem.setIcon(R.drawable.ic_delete);
//                // add to menu
//                menu.addMenuItem(deleteItem);
//            }
//        };
//        // set creator
//        listview.setMenuCreator(creator);
//
//        // step 2. listener item click event
//        listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//
//
//
//            @Override
//            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                Goods item = list.get(position);
//                switch (index) {
////				case 0:
////					// open
////					open(item);
////					break;
//                    case 0:
//                        // delete
////					delete(item);
////					list.remove(position);
////					adapter.notifyDataSetChanged();
//
//                        posi=position;
//
//                        del();
//
//
//                        break;
//                }
//                return false;
//            }
//        });
//
//        // set SwipeListener
//        listview.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
//
//            @Override
//            public void onSwipeStart(int position) {
//                // swipe start
//            }
//
//            @Override
//            public void onSwipeEnd(int position) {
//                // swipe end
//            }
//        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    private void del() {   //删除

        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle(getString(R.string.shuru2) + "“"
                + list.get(posi)+ "”");
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

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("删除中...");
        dialog.setCancelable(true);

        dialog.show();
    }

    public void showPro1() {

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("添加房间中...");
        dialog.setCancelable(true);

        dialog.show();
    }

    public void showPro2() {

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("修改设备所在房间中...");
        dialog.setCancelable(true);

        dialog.show();
    }
    public void showPro3() {

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("正在修改房间名...");
        dialog.setCancelable(true);

        dialog.show();
    }

    public void push_del() {  //删除
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
//                    jsonObject.put("rid", toulist.get(posi).getName());
                    String js = jsonObject.toString();

                    publish_String(js);  //主题
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public void push_add(final String name) {
//        Log.e("qqq","消息 push_read");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "updateroomgather");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("roomgather", name);
                    String js = jsonObject.toString();

                    publish_String(js);  //主题
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void push_set(final String name) {
//        Log.e("qqq","消息 push_read");
        final String myTopicding_too =  MqttService.myTopicDevice;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "updatedeviceroomname");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("roomname", name);
                    jsonObject.put("sid", sid);
                    String js = jsonObject.toString();

                    publish_String3(js, myTopicding_too);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void dataListview() {  //获取list数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "queryroomgather");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(999);
        handler.removeMessages(2000);
        handler.removeMessages(2001);
        handler.removeMessages(3000);
        handler.removeMessages(3001);

        handler.removeMessages(4000);
        handler.removeMessages(4001);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 999:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    adapter.setList(list);
                    listview.onRefreshComplete();
                    adapter.notifyDataSetChanged();
                    break;
                case 2000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                    dataListview();
                    break;
                case 2001:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改失败,"+msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;

                case 3000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                    Intent in=new Intent();
                    in.putExtra("name",msg.obj.toString());
                    setResult(RESULT_OK,in);
                    finish();
                    break;
                case 3001:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改失败,"+msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;

                case 4000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                    break;
                case 4001:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改失败,"+msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


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
            onClick onClick=null;
            if(convertView==null){
                view = inflater.inflate(R.layout.items_room, parent, false);
                too=new ListViewTool();
//
                too.name=(TextView) view.findViewById(R.id.items_room_tv);
                too.bj=(ImageView) view.findViewById(R.id.items_room_bj);


                view.setTag(too);
            }
            else {
                too = (ListViewTool) view.getTag();
            }
            if(camera == null) return null;
            if(camera.getName().equals("默认")){
                too.bj.setVisibility(View.GONE);
            }else{
                too.bj.setVisibility(View.VISIBLE);
                onClick=new onClick();
                too.bj.setTag(position);
                too.bj.setOnClickListener(onClick);
            }

            too.name.setText(camera.getName());
            if(roomname.equals(camera.getName())){
                too.name.setTextColor(getResources().getColor(R.color.c_1eac94));
            }else{
                too.name.setTextColor(getResources().getColor(R.color.white));
            }

            return view;
        }

        class ListViewTool {

            public TextView name ;
            public ImageView bj;
        }

        class onClick implements View.OnClickListener{


            @Override
            public void onClick(View v) {
                int position;
                position = (Integer) v.getTag();
                Log.e("qqq","房间="+list.get(position).getName());
                setRoomName(list.get(position).getName());

            }
        }
    }


    class Goods {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }



    public void setRoomName(final String oldName ) {
        final EditText et = new EditText(this);
        et.setText("");
        new AlertDialog.Builder(this).setTitle("修改“"+oldName+"”的房间名")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "内容不能为空！" + input, Toast.LENGTH_LONG).show();
                        } else {
                            if(input.length()>6){
                                Toast.makeText(getApplicationContext(), "房间名不可超过6个字！" + input, Toast.LENGTH_LONG).show();
                                return;
                            }

                            if(oldName.equals("默认")){
                                Toast.makeText(getApplicationContext(), "“默认”房间名不可更改" , Toast.LENGTH_LONG).show();
                                return;
                            }

                            for(Goods fj:list){
                                String name=fj.getName();
                                if(input.equals(name)){
                                    Toast.makeText(getApplicationContext(), "房间名已存在！" + input, Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }

                            push_setName(input,oldName);
                            showPro3();
                        }
                    }
                })
                .setNegativeButton("取消", null).show();

    }



    public void push_setName(final String name,final String oldName) {  //修改房间名
//        Log.e("qqq","消息 push_read");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "updateroomgatherbysingle");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("roomnamenew", name);
                    jsonObject.put("roomnameold", oldName);
                    String js = jsonObject.toString();

                    publish_String(js);  //主题
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
