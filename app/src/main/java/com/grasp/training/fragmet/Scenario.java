package com.grasp.training.fragmet;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.grasp.training.R;
import com.grasp.training.activity.ConditionActivity;
import com.grasp.training.swipemenulistview.SwipeMenu;
import com.grasp.training.swipemenulistview.SwipeMenuCreator;
import com.grasp.training.swipemenulistview.SwipeMenuItem;
import com.grasp.training.swipemenulistview.SwipeMenuListView;
import com.grasp.training.tool.BaseMqttFragment;

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

    private List<Goods> list ;
    private myListViewAdapter adapter;
    private int posi=0;
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
        return null;
    }

    @Override
    public String getMyTopicDing() {
        return null;
    }

    @Override
    public void MyMessageArrived(String message) {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.scenario_add)
    public void onViewClicked() {
        context.startActivity(new Intent(context, ConditionActivity.class));
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

                        posi=position;

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
    private void del(){   //删除

        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setTitle(getString(R.string.shuru2) + "“"
                + list.get(posi).getName() + "”");
        builder.setPositiveButton(getString(R.string.alert_dialog_ok),new DialogInterface.OnClickListener() {

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

    }


    public void dataListview() {  //获取list数据
        list=new ArrayList<>();
        Goods goods=new Goods();
        goods.setName("起床场景");
        goods.setZt(true);
        Goods goods2=new Goods();
        goods2.setName("离家场景");
        Goods goods3=new Goods();
        goods3.setName("回家场景");

        Goods goods4=new Goods();
        goods4.setName("用餐场景");

        Goods goods5=new Goods();
        goods5.setName("会客场景");

        Goods goods6=new Goods();
        goods6.setName("睡眠场景");
        list.add(goods);
        list.add(goods2);
        list.add(goods3);
        list.add(goods4);
        list.add(goods5);
        list.add(goods6);
        adapter.setList(list);
        ha.sendEmptyMessageDelayed(100,500);


    }

    Handler ha=new Handler() {
        String xx;

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:

                    adapter.notifyDataSetChanged();
                    listview.onRefreshComplete();
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
                view = inflater.inflate(R.layout.items_scenario, parent, false);
                too=new ListViewTool();
//
                too.name=(TextView) view.findViewById(R.id.items_scenario_tv);
                too.zx=(TextView) view.findViewById(R.id.items_scenario_tv2);


                view.setTag(too);
            }
            else {
                too = (ListViewTool) view.getTag();
            }
            if(camera == null) return null;
            // too.del.setOnClickListener(click);

            //  too.up.setTag(position);
            onClick=new onClick();
            too.zx.setTag(position);
            too.zx.setOnClickListener(onClick);
            too.name.setText(camera.getName());
            if(camera.isZt()){
                too.zx.setVisibility(View.VISIBLE);
            }else{
                too.zx.setVisibility(View.GONE);
            }

            return view;
        }

        class ListViewTool {

            public TextView name, zx ;
        }

        class onClick implements View.OnClickListener{


            @Override
            public void onClick(View v) {
                int position;
                position = (Integer) v.getTag();
                Log.e("qqq","执行="+list.get(position).getName());

            }
        }
    }


    class Goods {
        private String name;
        private boolean zt;

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
}
