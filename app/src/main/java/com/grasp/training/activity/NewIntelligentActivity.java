package com.grasp.training.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liangmutian.mypicker.TimePickerDialog;
import com.grasp.training.R;
import com.grasp.training.service.MqttService;
import com.grasp.training.swipemenulistview.SwipeMenu;
import com.grasp.training.swipemenulistview.SwipeMenuCreator;
import com.grasp.training.swipemenulistview.SwipeMenuItem;
import com.grasp.training.swipemenulistview.SwipeMenuListView;
import com.grasp.training.tool.BaseMqttActivity;
import com.grasp.training.tool.DataStatus;
import com.grasp.training.tool.Goods;
import com.grasp.training.tool.myActivityManage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewIntelligentActivity extends BaseMqttActivity {

    @BindView(R.id.new_intelligent_fh)
    ImageView newIntelligentFh;
    @BindView(R.id.new_intelligent_save)
    TextView newIntelligentSave;
    @BindView(R.id.new_intelligent_if)
    TextView newIntelligentIf;
    @BindView(R.id.new_intelligent_condition_listview)
    SwipeMenuListView listview1;
    @BindView(R.id.new_intelligent_condition_add)
    TextView newIntelligentConditionAdd;
    @BindView(R.id.new_intelligent_carried_out_listview)
    SwipeMenuListView listview2;
    @BindView(R.id.new_intelligent_carried_out_add)
    TextView newIntelligentCarriedOutAdd;
    @BindView(R.id.new_intelligent_time_layout_tv)
    TextView newIntelligentTimeLayoutTv;
    @BindView(R.id.new_intelligent_time_layout_tv2)
    TextView newIntelligentTimeLayoutTv2;
    @BindView(R.id.new_intelligent_name)
    TextView name;
    @BindView(R.id.new_intelligent_time_layout)
    RelativeLayout newIntelligentTimeLayout;
    @BindView(R.id.new_intelligent_time_layout_im)
    ImageView newIntelligentTimeim;


    private String myTopicding = MqttService.myTopicDevice;
    private String myTopic = MqttService.myTopicDevice;

    private DataStatus dataStatus;
    private boolean zt;
    private Context context;

    private boolean zx_zt,next_zt;
    private List<Goods> list1, list2;
    private myListViewAdapter adapter1, adapter2;
    private int posi1 = 0, posi2 = 0;
    private String json;

    public static void startActivity(Context context, DataStatus dataStatus, String  json) {
        Intent in = new Intent(context, NewIntelligentActivity.class);
        in.putExtra("NewIntelligentActivity", dataStatus);
        in.putExtra("NewIntelligentActivity2", json);
        context.startActivity(in);

    }

    public static void startActivity(Context context, DataStatus dataStatus, boolean zt, int tiao_num) {
        Intent in = new Intent(context, NewIntelligentActivity.class);
        in.putExtra("NewIntelligentActivity", dataStatus);
        in.putExtra("NewIntelligentActivity2", zt);
        in.putExtra("NewIntelligentActivity3", tiao_num);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(in);

    }

    @Override
    public String getMyTopic() {
        return myTopic;
    }


    @Override
    public String getMyTopicDing() {
        return myTopicding;
    }

    @Override
    public String getSid() {
        return "";
    }


    @Override
    public int setLayoutId() {
        return R.layout.new_intelligent_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
        myActivityManage.removeA(this);
        Log.e("qqq", "onNewIntent init");
        Intent in = getIntent();
        dataStatus = (DataStatus) in.getSerializableExtra("NewIntelligentActivity");
        json = in.getStringExtra("NewIntelligentActivity2");
        int tiao_num = in.getIntExtra("NewIntelligentActivity3", -1);

        context = getContext();


        if (tiao_num != -1) {
            Toast.makeText(context, "页面被系统回收", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (dataStatus.getIf_num() == 1) {
            newIntelligentConditionAdd.setVisibility(View.GONE);
            newIntelligentTimeLayoutTv.setTextColor(getResources().getColor(R.color.white2));
            newIntelligentTimeLayoutTv2.setTextColor(getResources().getColor(R.color.white2));
            newIntelligentTimeim.setBackgroundResource(R.drawable.zhu_btn_jr);
            zx_zt = true;
        } else {
            newIntelligentConditionAdd.setVisibility(View.VISIBLE);
            newIntelligentTimeLayoutTv.setTextColor(getResources().getColor(R.color.white));
            newIntelligentTimeLayoutTv2.setTextColor(getResources().getColor(R.color.white));
            newIntelligentTimeim.setBackgroundResource(R.drawable.gengduo);
            zx_zt = false;
        }
        if(json==null||json.equals("")){
            zt=false;
        }else{
            zt=true;
        }

        if (zt) {
            name.setText("修改智能");
        } else {
            name.setText("新建智能");
        }
        if(dataStatus.getElse_num()==1||dataStatus.getElse_num()==2){
            next_zt=true;
        }else{
            next_zt=false;
        }
        newIntelligentSave.setEnabled(next_zt);
        initListview2();
        initListview1();
        initJson(json);
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
    public void MyMessageArrived(String message) {

    }


    @OnClick({R.id.new_intelligent_fh, R.id.new_intelligent_save, R.id.new_intelligent_condition_add, R.id.new_intelligent_carried_out_add, R.id.new_intelligent_time_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.new_intelligent_fh:
                cannel();
                break;
            case R.id.new_intelligent_save:
                if (zt) { //set

                } else {  //new
                    setNameZn();
                }
                break;
            case R.id.new_intelligent_condition_add:
                if(list1.size()>=5){
                    Toast.makeText(context,"条件数量不能大于5",Toast.LENGTH_LONG).show();
                    return;
                }
                if (list1.size() == 1) {
                    add();

                } else {
                    if(list1.size() == 0){
                        ConditionActivity.startActivity(getContext(), 0, false);
                    }else{
                        ConditionActivity.startActivity(getContext(), 0, true);
                    }

                }

                break;
            case R.id.new_intelligent_carried_out_add:
//                CarriedOutActivity.startCarriedOutActivity(getContext(),null,0,0,0,0);
                if(list2.size()>10){
                    Toast.makeText(context,"执行数量不能大于10",Toast.LENGTH_LONG).show();
                    return;
                }
                if(list2.size()>0){
                    if(list2.get(list2.size()-1).getDataStatus().getElse_num()==3){
                        CarriedOutActivity.startCarriedOutActivity(context,new DataStatus(),0,0,1,0);
                    }else{
                        CarriedOutActivity.startCarriedOutActivity(context,new DataStatus(),0,0,0,0);
                    }
                }else{
                    CarriedOutActivity.startCarriedOutActivity(context,new DataStatus(),0,0,0,0);
                }

                break;
            case R.id.new_intelligent_time_layout:
                if (zx_zt) {
                    return;
                }
                NewIntelligentTimeActivity.startActivity(context,week,time1,time2);
                break;
        }
    }

    private void initJson(String json){

    }

    private void add() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle("添加满足的条件");
        builder.setPositiveButton("满足任意条件", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                if (list1.size() > 1) {
                    ConditionActivity.startActivity(getContext(), 0, false);
                } else {
                    ConditionActivity.startActivity(getContext(), 0, true);
                }

                newIntelligentIf.setText("☰ 如果满足任意条件");
            }
        });

        builder.setNegativeButton(
                "满足全部条件", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        newIntelligentIf.setText("☰ 如果满足全部条件");
                        if (list1.size() > 1) {
                            ConditionActivity.startActivity(getContext(), 0, false);
                        } else {
                            ConditionActivity.startActivity(getContext(), 0, true);
                        }
                    }
                });
        builder.show();
    }

    private void initListview1() {
        listview1.setCacheColorHint(0);
        adapter1 = new myListViewAdapter(context, list1);
        listview1.setAdapter(adapter1);
        dataListview1();

        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                arg2--;
                DataStatus dataStatus=list1.get(arg2).getDataStatus();
                list_num1 = arg2;
                int num = list1.get(arg2).getNum();
                if (num == 1) {
                    if (list1.size() == 1) {
                        ConditionActivity.startActivity(getContext(), num, false);
                    } else {
                        if(list1.size() == 0){
                            ConditionActivity.startActivity(getContext(), num, false);
                        }else{
                            ConditionActivity.startActivity(getContext(), num, true);
                        }

                    }
                } else if (num == 2){

                    ConditionTimingActivity.stastActivity(getContext(),dataStatus.getNum2_week(),dataStatus.getNum2_time(),num);
                }else if (num == 3){
                    SetOutdoorWeatherActivity.stateActivity(getContext(),dataStatus.getNum3_num(),num);
                }else if (num == 4){
                    DeviceStatusActivity.startActivity(getContext(),false,dataStatus,num);
                }



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
        listview1.setMenuCreator(creator);

        // step 2. listener item click event
        listview1.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Goods item = list1.get(position);
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

                        posi1 = position;

                        del1();


                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        listview1.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

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


    private void del1() {   //删除

        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle(getString(R.string.shuru2) + "“"
                + list1.get(posi1).getLight() + "”");
        builder.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                if (list1.get(posi1).getNum() == 1) {
                    Toast.makeText(context, "“手动执行”不可删除", Toast.LENGTH_LONG).show();
                    return;
                }
                list1.remove(posi1);
                adapter1.setList(list1);
                adapter1.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(listview1);
            }
        });

        builder.setNegativeButton(
                getString(R.string.alert_dialog_cancel), null);
        builder.show();
    }

    public void dataListview1() {  //获取list数据
        list1 = new ArrayList<>();
        if(!zt){
            int num = dataStatus.getIf_num();
            String light = dataStatus.getIf_left();
            String right = dataStatus.getIf_right();
            Goods g = new Goods();
            g.setLight(light);
            g.setRight(right);
            g.setNum(num);
            g.setDataStatus(dataStatus);
            list1.add(g);
            adapter1.setList(list1);
            listview1.onRefreshComplete();
            adapter1.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(listview1);
        }

    }

    public void setListViewHeightBasedOnChildren(SwipeMenuListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    private void initListview2() {
        listview2.setCacheColorHint(0);
        adapter2 = new myListViewAdapter(context, list2);
        listview2.setAdapter(adapter2);
        dataListview2();

        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                arg2 -= 1;
                list_num2 = arg2;
                int num=list2.get(arg2).getDataStatus().getElse_num();
                if(num==1){
                    DeviceStatusActivity.startActivity(getContext(),true,list2.get(arg2).getDataStatus(),num);
                }else  if(num==2){

                    CarriedOutActivity.startCarriedOutActivity(getContext(),new DataStatus(),0,1,0,2);
                }else  if(num==3){//延时
                    int time=list2.get(arg2).getDataStatus().getElse_num3_time();
                    int h=time/60/60;
                    int m=(time%3600)/60;
                    int s=(time%3600)%60;
                    showTimePick(h,m,s,arg2);
                }
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
        listview2.setMenuCreator(creator);

        // step 2. listener item click event
        listview2.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Goods item = list2.get(position);
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

                        posi2 = position;

                        del2();


                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        listview2.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

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



    private TimePickerDialog.Builder builder = null;
    private Dialog timeDialog;

    private void showTimePick(int h, int m, int s, final int post) {

        if (timeDialog == null) {

            builder = new TimePickerDialog.Builder(this);

            timeDialog = builder.setOnTimeSelectedListener(new TimePickerDialog.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(String[] times) {

//                    mTextView.setText(times[0] + ":" + times[1]);
                    int h = Integer.valueOf(times[0]);
                    int m = Integer.valueOf(times[1]);
                    int s = Integer.valueOf(times[2]);
                    int long_l = h * 60 * 60 + m * 60 + s;
                    Goods goods=list2.get(post);
                    goods.setRight(long_l+"s");
                    goods.getDataStatus().setElse_num3_time(long_l);
                    goods.getDataStatus().setElse_right(long_l+"s");
                    list2.set(post,goods);
                    adapter2.setList(list2);
                    adapter2.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(listview2);

                }
            }).create();

        }


        builder.setText_sj(h, m, s, "延时");
        timeDialog.show();

    }

    private void del2() {   //删除

        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle(getString(R.string.shuru2) + "“"
                + list2.get(posi2).getLight() + "”");
        builder.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                list2.remove(posi2);
                adapter2.setList(list2);
                adapter2.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(listview2);
                for(Goods goods:list2){
                    DataStatus dataStatus=goods.getDataStatus();
                    if(dataStatus.getElse_num()==1||dataStatus.getElse_num()==2){
                        next_zt=true;
                        newIntelligentSave.setEnabled(next_zt);
                       return;
                    }
                }
                next_zt=false;
                newIntelligentSave.setEnabled(next_zt);
            }
        });

        builder.setNegativeButton(
                getString(R.string.alert_dialog_cancel), null);
        builder.show();
    }

    public void dataListview2() {  //获取list数据

        list2 = new ArrayList<>();
        if(!zt){
            int num = dataStatus.getElse_num();
            String light = dataStatus.getElse_left();
            String right = dataStatus.getElse_right();
            Goods g = new Goods();
            g.setLight(light);
            g.setRight(right);
            g.setNum(num);
            g.setDataStatus(dataStatus);
            list2.add(g);
            adapter2.setList(list2);
            listview2.onRefreshComplete();
            adapter2.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(listview2);
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
                view = inflater.inflate(R.layout.items_new_intelligent, parent, false);
                too = new ListViewTool();
//
                too.light = (TextView) view.findViewById(R.id.items_new_intelligent_light);

                too.right = (TextView) view.findViewById(R.id.items_new_intelligent_right);
                too.xian = (TextView) view.findViewById(R.id.items_new_intelligent_xian);

                view.setTag(too);
            } else {
                too = (ListViewTool) view.getTag();
            }
            if (camera == null) return null;
            // too.del.setOnClickListener(click);

            //  too.up.setTag(position);
            too.light.setText(camera.getLight());
            too.right.setText(camera.getRight());
            if (position == 0) {
                too.xian.setVisibility(View.GONE);
            } else {
                too.xian.setVisibility(View.VISIBLE);
            }
            return view;
        }

        class ListViewTool {

            public TextView light, right, xian;
        }
    }

    class Goods {
        private String light, right;
        private int num;
        private DataStatus dataStatus;

        public DataStatus getDataStatus() {
            return dataStatus;
        }

        public void setDataStatus(DataStatus dataStatus) {
            this.dataStatus = dataStatus;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getLight() {
            return light;
        }

        public void setLight(String light) {
            this.light = light;
        }

        public String getRight() {
            return right;
        }

        public void setRight(String right) {
            this.right = right;
        }
    }


    private void cannel() {   //删除

        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle("数据未保存，是否退出");
        builder.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        builder.setNegativeButton(
                getString(R.string.alert_dialog_cancel), null);
        builder.show();
    }


    private int list_num1, list_num2;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//设置新的intent

        DataStatus dataStatus = (DataStatus) intent.getSerializableExtra("NewIntelligentActivity");
        boolean zt = intent.getBooleanExtra("NewIntelligentActivity2", false);
        int tiao_num = intent.getIntExtra("NewIntelligentActivity3", -1);
        Log.e("qqq", "onNewIntent tiao_num=" + tiao_num);
        if (zt) {//下


            if (tiao_num == 0) {//add
                Goods goods = new Goods();
                goods.setDataStatus(dataStatus);
                goods.setNum(dataStatus.getElse_num());
                goods.setLight(dataStatus.getElse_left());
                goods.setRight(dataStatus.getElse_right());
                list2.add(goods);
                adapter2.setList(list2);
                adapter2.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(listview2);
                for(Goods g:list2){
                    DataStatus data=g.getDataStatus();
                    if(data.getElse_num()==1||data.getElse_num()==2){
                        next_zt=true;
                        newIntelligentSave.setEnabled(next_zt);
                        return;
                    }
                }
                next_zt=false;
                newIntelligentSave.setEnabled(next_zt);

            } else if (tiao_num > 0) { //set
                Goods goods = new Goods();
                goods.setDataStatus(dataStatus);
                goods.setNum(dataStatus.getElse_num());
                goods.setLight(dataStatus.getElse_left());
                goods.setRight(dataStatus.getElse_right());
                list2.remove(list_num2);
                list2.add(list_num2, goods);
                adapter2.setList(list2);
                adapter2.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(listview2);
                for(Goods g:list2){
                    DataStatus data=g.getDataStatus();
                    if(data.getElse_num()==1||data.getElse_num()==2){
                        next_zt=true;
                        newIntelligentSave.setEnabled(next_zt);
                        return;
                    }
                }
                next_zt=false;
                newIntelligentSave.setEnabled(next_zt);
            }

        } else {//上
            if (dataStatus.getIf_num() == 1) {
                newIntelligentConditionAdd.setVisibility(View.GONE);
                newIntelligentTimeLayoutTv.setTextColor(getResources().getColor(R.color.white2));
                newIntelligentTimeLayoutTv2.setTextColor(getResources().getColor(R.color.white2));
                newIntelligentTimeim.setBackgroundResource(R.drawable.zhu_btn_jr);
                zx_zt = true;
            } else {
                newIntelligentConditionAdd.setVisibility(View.VISIBLE);
                newIntelligentTimeLayoutTv.setTextColor(getResources().getColor(R.color.white));
                newIntelligentTimeLayoutTv2.setTextColor(getResources().getColor(R.color.white));
                newIntelligentTimeim.setBackgroundResource(R.drawable.gengduo);
                zx_zt = false;
            }
            if (tiao_num == 0) {//add
                Goods goods = new Goods();
                goods.setDataStatus(dataStatus);
                goods.setNum(dataStatus.getIf_num());
                goods.setLight(dataStatus.getIf_left());
                goods.setRight(dataStatus.getIf_right());
                list1.add(goods);
                adapter1.setList(list1);
                adapter1.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(listview1);
            } else if (tiao_num > 0) { //set

                Goods goods = new Goods();
                goods.setDataStatus(dataStatus);
                goods.setNum(dataStatus.getIf_num());
                goods.setLight(dataStatus.getIf_left());
                goods.setRight(dataStatus.getIf_right());
                list1.remove(list_num1);
                list1.add(list_num1, goods);
                adapter1.setList(list1);
                adapter1.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(listview1);
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("qqq", "onNewIntent onDestroy");


    }

    private String week="1234567",time1="00:00",time2="00:00";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    week=data.getStringExtra("week");
                    time1=data.getStringExtra("time1");
                    time2=data.getStringExtra("time2");
                    if(week.equals("1234567")&&time1.equals(time2)){
                        newIntelligentTimeLayoutTv2.setText("每天");
                    }else if(week.equals("0000000")){
                        newIntelligentTimeLayoutTv2.setText("永不");
                    }else{
                        newIntelligentTimeLayoutTv2.setText(time1+"-"+time2);
                    }

                }
                break;
        }

    }

    public void setNameZn() {
        final EditText et = new EditText(this);
        et.setText("");
        new AlertDialog.Builder(this).setTitle("设定新建智能名称")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "名称不能为空！" + input, Toast.LENGTH_LONG).show();
                            setNameZn();
                        } else {
                            if (input.length()>10){ //
                                Toast.makeText(getApplicationContext(), "名称不能过长！" + input, Toast.LENGTH_LONG).show();
                                setNameZn();
                                return;
                            }else{

                            }
                        }
                    }
                })
                .setNegativeButton("取消", null).show();

    }
}
