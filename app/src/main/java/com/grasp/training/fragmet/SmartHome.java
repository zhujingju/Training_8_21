package com.grasp.training.fragmet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.BaseMqttFragment;
import com.grasp.training.view.SlideSwitch;
import com.zs.easy.mqtt.EasyMqttService;
import com.zs.easy.mqtt.IEasyMqttCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by zhujingju on 2018/5/30.
 */

public class SmartHome extends BaseMqttFragment {


    @BindView(R.id.smart_tv_bfb)
    TextView smartTvBfb;
    @BindView(R.id.smart_byc_seekbar)
    SeekBar smartBycSeekbar;
    @BindView(R.id.smart_tv_seekbarByc)
    TextView smartTvSeekbarByc;
    @BindView(R.id.smart_layout_kt)
    RelativeLayout smartLayoutKt;
    @BindView(R.id.smart_layout_jhq)
    LinearLayout smartLayoutJhq;
    @BindView(R.id.smart_layout_byc)
    RelativeLayout smartLayoutByc;
    @BindView(R.id.smart_home_im_sx)
    ImageView smartHomeImSx;
    @BindView(R.id.smart_layout1)
    LinearLayout smartLayout1;
    @BindView(R.id.smart_layout2)
    LinearLayout smartLayout2;
    @BindView(R.id.smart_layout3)
    LinearLayout smartLayout3;
    @BindView(R.id.smart_layout4)
    LinearLayout smartLayout4;
    @BindView(R.id.smart_layout5)
    LinearLayout smartLayout5;
    @BindView(R.id.smart_layout6)
    RelativeLayout smartLayout6;
    /**
     * 回调时使用
     */
    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    @BindView(R.id.smart_home_pro)
    ProgressBar smartHomePro;
    @BindView(R.id.smart_tv_wd)
    TextView smartTvWd;
    @BindView(R.id.smart_tv_sd)
    TextView smartTvSd;
    @BindView(R.id.smart_tv_pm)
    TextView smartTvPm;
    @BindView(R.id.smart_tv_ktwd)
    TextView smartTvKtwd;
    @BindView(R.id.swit_sx)
    SlideSwitch switSx;
    @BindView(R.id.swit_kt)
    SlideSwitch switKt;
    @BindView(R.id.smart_fs)
    ImageView smartFs;
    @BindView(R.id.swit_jhq)
    SlideSwitch switJhq;
    @BindView(R.id.swit_cl)
    SlideSwitch switCl;
    @BindView(R.id.swit_byc)
    SlideSwitch switByc;
    @BindView(R.id.swit_dd)
    SlideSwitch switDd;

    @BindView(R.id.smart_seekbar)
    SeekBar smartSeekbar;
    @BindView(R.id.smart_tv_ktwd2)
    TextView smartTvKtwd2;

    @BindView(R.id.smart_tv_fs)
    TextView smartTvfs;

    @BindView(R.id.smart_tv_wd2)
    TextView smartTvWd2;
    @BindView(R.id.smart_tv_sd2)
    TextView smartTvSd2;
    @BindView(R.id.smart_tv_sx)
    TextView smartTvSx;
    @BindView(R.id.smart_tv_kt)
    TextView smartTvKt;
    @BindView(R.id.smart_tv_jhq)
    TextView smartTvJhq;
    @BindView(R.id.smart_tv_cl)
    TextView smartTvCl;
    @BindView(R.id.smart_tv_byc)
    TextView smartTvByc;
    @BindView(R.id.smart_tv_dd)
    TextView smartTvDd;
    @BindView(R.id.smart_gs1)
    LinearLayout smartGs1;
    @BindView(R.id.smart_gs2)
    LinearLayout smartGs2;
    @BindView(R.id.smart_xh1)
    ImageView smartXh1;
    @BindView(R.id.smart_xh2)
    ImageView smartXh2;
    @BindView(R.id.smart_gs1_tv)
    TextView smartGs1Tv;
    @BindView(R.id.smart_gs1_im)
    ImageView smartGs1Im;
    @BindView(R.id.smart_gs2_tv)
    TextView smartGs2Tv;
    @BindView(R.id.smart_gs2_im)
    ImageView smartGs2Im;
    private Context context;
    Unbinder unbinder;

    private String myTopic ="iotbroad/iot";


    @Override
    public int getInflate() {
        return R.layout.smart_home;
    }

    @Override
    public void init(View view) {
        context = getActivity();

        unbinder = ButterKnife.bind(this, view);
        AssetManager mgr = context.getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/FZLTCXHJW.TTF");//根据路径得到Typeface
        smartTvWd.setTypeface(tf);
        smartTvSd.setTypeface(tf);
        smartTvPm.setTypeface(tf);
        smartTvWd2.setTypeface(tf);
        smartTvSd2.setTypeface(tf);
        smartTvKtwd.setVisibility(View.INVISIBLE);
        initView();
        smartHomePro.setVisibility(View.VISIBLE);
        switSx.setSlideable(false);
        switKt.setSlideable(false);
        switJhq.setSlideable(false);
        switCl.setSlideable(false);
        switByc.setSlideable(false);
        switDd.setSlideable(false);
        smartTvSx.setTextColor(getResources().getColor(R.color.white2));
        smartTvKt.setTextColor(getResources().getColor(R.color.white2));
        smartTvJhq.setTextColor(getResources().getColor(R.color.white2));
        smartTvCl.setTextColor(getResources().getColor(R.color.white2));
        smartTvByc.setTextColor(getResources().getColor(R.color.white2));
        smartTvDd.setTextColor(getResources().getColor(R.color.white2));

        smartTvKtwd2.setTextColor(getResources().getColor(R.color.white2));
        smartTvfs.setTextColor(getResources().getColor(R.color.white2));

        smartSeekbar.setClickable(false);
        smartSeekbar.setEnabled(false);
        smartSeekbar.setSelected(false);
        smartSeekbar.setFocusable(false);

        smartFs.setClickable(false);
        smartXh1.setClickable(false);
        smartXh2.setClickable(false);
        smartGs1.setClickable(false);
        smartGs2.setClickable(false);

        smartTvBfb.setTextColor(getResources().getColor(R.color.white2));
        smartBycSeekbar.setClickable(false);
        smartBycSeekbar.setEnabled(false);
        smartBycSeekbar.setSelected(false);
        smartBycSeekbar.setFocusable(false);
        smartTvSeekbarByc.setVisibility(View.INVISIBLE);

        smartLayoutKt.setVisibility(View.GONE);
        smartLayoutJhq.setVisibility(View.GONE);
        smartLayoutByc.setVisibility(View.GONE);

        smartLayout1.setVisibility(View.GONE);
        smartLayout2.setVisibility(View.GONE);
        smartLayout3.setVisibility(View.GONE);
        smartLayout4.setVisibility(View.GONE);
        smartLayout5.setVisibility(View.GONE);
        smartLayout6.setVisibility(View.GONE);
        ha.sendEmptyMessageDelayed(setZERO, 5 * 60 * 1000);  //5分钟获取一次
    }

    @Override
    public String  getMyTopic() {
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
                Log.e("qqq", "messageArrived  message= " + message);
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

                    switch (cmd) {
                        case "read_ok":
                            String data = jsonObject.optString("data");
                            me = new Message();
                            me.what = setTWO;
                            me.obj = data;
                            ha.sendMessage(me);
                            break;

                        case "training_mode_ok":
                            channel_0 = jsonObject.optString("channel_0");
                            me = new Message();
                            me.what = setTHREE;
                            me.obj = channel_0;
                            ha.sendMessage(me);
                            break;
                        case "air_conditioning_ok":
                            channel_0 = jsonObject.optString("channel_0");
                            me = new Message();
                            me.what = setFOUR;
                            me.obj = channel_0;
                            ha.sendMessage(me);
                            break;
                        case "air_conditioning_temperature_ok":
                            var = jsonObject.optInt("var");
                            me = new Message();
                            me.what = setFIVE;
                            me.arg1 = var;
                            ha.sendMessage(me);
                            break;
                        case "air_conditioning_windSpeed_ok":
                            var = jsonObject.optInt("var");
                            me = new Message();
                            me.what = setSIX;
                            me.arg1 = var;
                            ha.sendMessage(me);
                            break;

                        case "filter_ok":
                            channel_0 = jsonObject.optString("channel_0");
                            me = new Message();
                            me.what = setSEVEN;
                            me.obj = channel_0;
                            ha.sendMessage(me);

                            break;
                        case "filter_mode_ok":
                            channel_0 = jsonObject.getString("channel_0");
                            me = new Message();
                            me.what = setEIGHT;
                            me.obj = channel_0;
                            ha.sendMessage(me);
                            break;
                        case "filter_cycle_n_ok":
                            channel_0 = jsonObject.getString("channel_0");
                            me = new Message();
                            me.what = setNINE;
                            me.obj = channel_0;
                            ha.sendMessage(me);
                            break;
                        case "filter_cycle_w_ok":
                            channel_0 = jsonObject.getString("channel_0");
                            me = new Message();
                            me.what = setNINE2;
                            me.obj = channel_0;
                            ha.sendMessage(me);
                            break;
                        case "window_ok":
                            channel_0 = jsonObject.optString("channel_0");
                            me = new Message();
                            me.what = setTEN;
                            me.obj = channel_0;
                            ha.sendMessage(me);
                            break;
                        case "blinds_ok":
                            channel_0 = jsonObject.optString("channel_0");
                            me = new Message();
                            me.what = setELEVEN;
                            me.obj = channel_0;
                            ha.sendMessage(me);
                            break;

                        case "blinds_percentage_ok":
                            var = jsonObject.optInt("var");
                            me = new Message();
                            me.what = setTHIRTEEN;
                            me.arg1 = var;
                            ha.sendMessage(me);
                            break;

                        case "electric_light_ok":
                            channel_0 = jsonObject.optString("channel_0");
                            me = new Message();
                            me.what = setTWELVE;
                            me.obj = channel_0;
                            ha.sendMessage(me);
                            break;

                        case "dht12_ok":
                            String dht = jsonObject.optString("data");
                            me = new Message();
                            me.what = setFOURTEENTH;
                            me.obj = dht;
                            ha.sendMessage(me);
                            break;


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        ha.sendEmptyMessageDelayed(setONE, 1000);
//            ha.sendEmptyMessageDelayed(setONE9, 500);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    smartTvKtwd.setVisibility(View.INVISIBLE);
                    break;

                case 2000:
                    smartTvSeekbarByc.setVisibility(View.INVISIBLE);
                    break;
            }


        }
    };


    private boolean smartBycSeekbar_zt = false;
    private boolean smartBycSeekbar_zt2 = true;
    private int bu_in;

    @SuppressLint("ResourceAsColor")
    public void initView() {

        smartBycSeekbar.setMax(99);
        smartBycSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!smartBycSeekbar_zt) {
                    return;
                }

                Log.e("seek", seekBar.getProgress() + " i=" + i + " b=" + b);
                if (!smartBycSeekbar_zt2) {
                    return;
                }
                int seek_in = i;
                if (seek_in == 99) {
                    seek_in = 100;
                }
                smartTvSeekbarByc.setText(seek_in + "%");
                smartTvSeekbarByc.setVisibility(View.VISIBLE);
                handler.removeMessages(2000);
                handler.sendEmptyMessageDelayed(2000, 1500);
                push_var("blinds_percentage", i);
                smartBycSeekbar_zt2 = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(00);
                            smartBycSeekbar_zt2 = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                smartBycSeekbar_zt = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                smartBycSeekbar_zt = false;
//                if (!smartBycSeekbar_zt2) {
                Log.d("qqq", "smartTvSeekbarByc onStopTrackingTouch");
                int seek_in = (seekBar.getProgress());
                if (seek_in == 99) {
                    seek_in = 100;
                }
                smartTvSeekbarByc.setText(seek_in + "%");
                smartTvSeekbarByc.setVisibility(View.VISIBLE);
                handler.removeMessages(2000);
                handler.sendEmptyMessageDelayed(2000, 1500);
                bu_in = seekBar.getProgress();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(50);
                            push_var("blinds_percentage_off", bu_in);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
//                }
//                smartTvSeekbarByc.setText(( seekBar.getProgress()) + "");
//                smartTvSeekbarByc.setVisibility(View.VISIBLE);
//                handler.removeMessages(2000);
//                handler.sendEmptyMessageDelayed(2000, 1500);
//                push_var("blinds_percentage",  seekBar.getProgress());
            }
        });


        smartSeekbar.setMax(14);
        smartSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                smartTvKtwd.setText((30 - seekBar.getProgress()) + "");
                smartTvKtwd.setVisibility(View.VISIBLE);
                handler.removeMessages(1000);
                handler.sendEmptyMessageDelayed(1000, 1500);
                push_var("air_conditioning_temperature", 30 - seekBar.getProgress());
            }
        });

//        switSx.setState(true); //设置开关
        switSx.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
//                switSx.setState(false);
//                smartTvSx.setText(getString(R.string.smart2_no));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }
                push_channel("training_mode", true);
            }

            @Override
            public void close() {
//                switSx.setState(true);
//                smartTvSx.setText(getString(R.string.smart2));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }

                push_channel("training_mode", false);
            }
        });
        switKt.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
//                switKt.setState(true);
//                smartTvKt.setText(getString(R.string.smart4_no));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }
                push_channel("air_conditioning", true);
            }

            @Override
            public void close() {
//                switKt.setState(false);
//                smartTvKt.setText(getString(R.string.smart4));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }
                push_channel("air_conditioning", false);
            }
        });
        switJhq.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
//                switJhq.setState(true);
//                smartTvJhq.setText(getString(R.string.smart7));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }
                push_channel("filter", true);
            }

            @Override
            public void close() {
//                switJhq.setState(false);
//                smartTvJhq.setText(getString(R.string.smart7_no));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }
                push_channel("filter", false);
            }
        });


        switCl.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
//                switCl.setState(true);
//                smartTvCl.setText(getString(R.string.smart11_no));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }
                push_channel("window", true);
            }

            @Override
            public void close() {
//                switCl.setState(false);
//                smartTvCl.setText(getString(R.string.smart11));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }
                push_channel("window", false);
            }
        });
        switByc.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
//                switByc.setState(true);
//                smartTvByc.setText(getString(R.string.smart13));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }
                push_channel("blinds", true);
            }

            @Override
            public void close() {
//                switByc.setState(false);
//                smartTvByc.setText(getString(R.string.smart13_no));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }
                push_channel("blinds", false);
            }
        });
        switDd.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
//                switDd.setState(true);
//                smartTvDd.setText(getString(R.string.smart15));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }
                push_channel("electric_light", true);
            }

            @Override
            public void close() {
//                switDd.setState(false);
//                smartTvDd.setText(getString(R.string.smart15_no));
                if (!isConnected()) {
//                    mqttService.connect(iEasyMqttCallBack);
                    return;
                }
                push_channel("electric_light", false);
            }
        });

        smartGs1Tv.setTextColor(context.getResources().getColorStateList(R.color.white));
        smartGs2Tv.setTextColor(context.getResources().getColorStateList(R.color.white2));
        smartGs1Im.setBackgroundResource(R.drawable.control_gaosu_selected);
        smartGs2Im.setBackgroundResource(R.drawable.control_jingyin_normal);
//        smartXh1.setBackgroundResource(R.drawable.control_neixunhuan_selected);
        smartXh1.setBackgroundResource(R.drawable.control_neixunhuan_normal);
        smartXh2.setBackgroundResource(R.drawable.control_waixunhuan_normal);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeMessages(1000);
        handler.removeMessages(2000);
        handler.removeMessages(setZERO);
        handler.removeMessages(setONE);
        handler.removeMessages(setONE9);
        handler.removeMessages(setTWO);
        handler.removeMessages(setTHREE);
        handler.removeMessages(setFOUR);
        handler.removeMessages(setFIVE);
        handler.removeMessages(setSIX);
        handler.removeMessages(setSEVEN);
        handler.removeMessages(setEIGHT);
        handler.removeMessages(setNINE);
        handler.removeMessages(setNINE2);
        handler.removeMessages(setTEN);
        handler.removeMessages(setELEVEN);
        handler.removeMessages(setTWELVE);
        handler.removeMessages(setTHIRTEEN);
        handler.removeMessages(setFOURTEENTH);
        handler.removeCallbacks(null);
        unbinder.unbind();
    }

    int fs_num = 0;
    private boolean gs_zt = true, xh_zt1, xh_zt2 = true;

    @SuppressLint("ResourceAsColor")
    @OnClick({R.id.smart_home_im_sx, R.id.smart_fs, R.id.smart_gs1, R.id.smart_gs2, R.id.smart_xh1, R.id.smart_xh2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.smart_home_im_sx:  //刷新数据
                ha.sendEmptyMessageDelayed(setONE, 0000);
                smartHomePro.setVisibility(View.VISIBLE);
                break;

            case R.id.smart_fs:
                if (fs_num == 0) {
                    Toast.makeText(context, "当前无法设置风速", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fs_num == 3) {
                    fs_num = 1;
                } else {
                    fs_num++;
                }
                push_var("air_conditioning_windSpeed", fs_num);


                break;
            case R.id.smart_gs1:
//                gs_zt = true;
                push_channel("filter_mode", true);
                break;
            case R.id.smart_gs2:
                push_channel("filter_mode", false);
//                gs_zt = false;
                break;
            case R.id.smart_xh1:
                xh_zt1 = !xh_zt1;
                push_channel("filter_cycle_n", xh_zt1);
                xh_zt1 = !xh_zt1;
                break;
            case R.id.smart_xh2:
                xh_zt2 = !xh_zt2;
                push_channel("filter_cycle_w", xh_zt2);
                xh_zt2 = !xh_zt2;
                break;
        }
    }


    public void setXh1(boolean xh) {
        if (xh) {
            smartXh1.setBackgroundResource(R.drawable.control_neixunhuan_selected);
        } else {
            smartXh1.setBackgroundResource(R.drawable.control_neixunhuan_normal);
        }
    }

    public void setXh2(boolean xh) {
        if (xh) {
            smartXh2.setBackgroundResource(R.drawable.control_waixunhuan_selected);
        } else {
            smartXh2.setBackgroundResource(R.drawable.control_waixunhuan_normal);
        }
    }

    public void setGs(boolean gs) {
        if (gs) {
            smartGs1Tv.setTextColor(context.getResources().getColorStateList(R.color.white));
            smartGs2Tv.setTextColor(context.getResources().getColorStateList(R.color.white2));
            smartGs1Im.setBackgroundResource(R.drawable.control_gaosu_selected);
            smartGs2Im.setBackgroundResource(R.drawable.control_jingyin_normal);
        } else {
            smartGs1Tv.setTextColor(context.getResources().getColorStateList(R.color.white2));
            smartGs2Tv.setTextColor(context.getResources().getColorStateList(R.color.white));
            smartGs1Im.setBackgroundResource(R.drawable.control_gaosu_noemal);
            smartGs2Im.setBackgroundResource(R.drawable.control_jingyin_selected);
        }

    }


    private final int setONE9 = 999;
    private final int setZERO = 1000;
    private final int setONE = 1001;

    private final int setTWO = 1002;
    private final int setTHREE = 1003;
    private final int setFOUR = 1004;
    private final int setFIVE = 1005;
    private final int setSIX = 1006;
    private final int setSEVEN = 1007;
    private final int setEIGHT = 1008;
    private final int setNINE = 1009;
    private final int setNINE2 = 1109;
    private final int setTEN = 1010;
    private final int setELEVEN = 1011;
    private final int setTWELVE = 1012;
    private final int setTHIRTEEN = 1013;
    private final int setFOURTEENTH = 1014;

    @SuppressLint("HandlerLeak")
    Handler ha = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (smartHomePro == null) {
                return;
            }
            JSONObject jsonObject;
            switch (msg.what) {
                case setONE9:
                    if (isConnected()) {

                    } else {
//                        mqttService.connect(iEasyMqttCallBack);
                        ha.sendEmptyMessageDelayed(1000, 3000);
                    }
                    break;

                case setZERO:  //自动刷新
                    ha.sendEmptyMessageDelayed(setONE, 0);
                    ha.sendEmptyMessageDelayed(setZERO, 5 * 60 * 1000);  //5分钟获取一次
                    break;
                case setONE:
                    Log.e("qqq", "isConnected()= " + isConnected());
                    if (isConnected()) {
//                        subscribe();
                        try {

                            //发送请求所有数据消息
                            jsonObject = new JSONObject();
                            jsonObject.put("cmd", "read");
                            jsonObject.put("sid", sid);
                            String js = jsonObject.toString();
                            publish_String(js);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                        }

//                        PowerManager powerManager = (PowerManager) context
//                                .getSystemService(Context.POWER_SERVICE);
//                        boolean ifOpen = powerManager.isScreenOn();


                    } else {
//                        mqttService.connect(iEasyMqttCallBack);
                        ha.sendEmptyMessageDelayed(setONE, 3000);
                    }
                    break;
                case setTWO:
                    try {
                        smartHomePro.setVisibility(View.GONE);
                        jsonObject = new JSONObject(msg.obj.toString());
                        String temperature = jsonObject.optString("temperature", "??");
                        String humidity = jsonObject.optString("humidity", "??");
                        String pm = jsonObject.optString("pm2.5", "??");
                        String TrainingMode = jsonObject.optString("training_mode", "??");
                        String airConditioning = jsonObject.optString("air_conditioning", "??");
                        int airConditioningTemperature = jsonObject.optInt("air_conditioning_temperature", -99);
                        int airConditioningWindSpeed = jsonObject.optInt("air_conditioning_windSpeed", 0);
                        String filter = jsonObject.optString("filter", "??");
                        String filter_mode = jsonObject.optString("filter_mode", "??");
                        String filter_cycle_w = jsonObject.optString("filter_cycle_w", "??");
                        String filter_cycle_n = jsonObject.optString("filter_cycle_n", "??");
                        String window = jsonObject.optString("window", "??");
                        String blinds = jsonObject.optString("blinds", "??");
                        String electricLight = jsonObject.optString("electric_light", "??");
                        int blinds_percentage = jsonObject.optInt("blinds_percentage", -99);
                        Log.e("qqq","blinds_percentage="+blinds_percentage);

                        smartTvWd.setText(temperature);
                        smartTvSd.setText(humidity);
                        smartTvPm.setText(pm);

                        smartLayoutKt.setVisibility(View.VISIBLE);
                        smartLayoutJhq.setVisibility(View.VISIBLE);
                        smartLayoutByc.setVisibility(View.VISIBLE);

                        smartLayout1.setVisibility(View.VISIBLE);
                        smartLayout2.setVisibility(View.VISIBLE);
                        smartLayout3.setVisibility(View.VISIBLE);
                        smartLayout4.setVisibility(View.VISIBLE);
                        smartLayout5.setVisibility(View.VISIBLE);
                        smartLayout6.setVisibility(View.VISIBLE);

                        if (TrainingMode.equals("on")) {
                            switSx.setState(true);
                            switSx.setSlideable(true);
                            smartTvSx.setText(getString(R.string.smart2_no));
                            smartTvSx.setTextColor(getResources().getColor(R.color.white));
                        } else if (TrainingMode.equals("??")) {
                            switSx.setSlideable(false);
                            smartTvSx.setTextColor(getResources().getColor(R.color.white2));
                            smartLayout1.setVisibility(View.GONE);
                        } else {
                            switSx.setState(false);
                            switSx.setSlideable(true);
                            smartTvSx.setText(getString(R.string.smart2));
                            smartTvSx.setTextColor(getResources().getColor(R.color.white));
                        }

                        if (airConditioning.equals("on")) {
                            switKt.setState(true);
                            switKt.setSlideable(true);
                            smartTvKt.setText(getString(R.string.smart4_no));
                            smartTvKt.setTextColor(getResources().getColor(R.color.white));
                        } else if (airConditioning.equals("??")) {
                            smartLayout2.setVisibility(View.GONE);
                            switKt.setSlideable(false);
                            smartTvKt.setTextColor(getResources().getColor(R.color.white2));
                            smartLayoutKt.setVisibility(View.GONE);

                        } else {
                            switKt.setState(false);
                            switKt.setSlideable(true);
                            smartTvKt.setText(getString(R.string.smart4));
                            smartTvKt.setTextColor(getResources().getColor(R.color.white));
                            smartLayoutKt.setVisibility(View.GONE);
                        }
                        if (filter.equals("on")) {
                            switJhq.setState(true);
                            switJhq.setSlideable(true);
                            smartTvJhq.setText(getString(R.string.smart7_no));
                            smartTvJhq.setTextColor(getResources().getColor(R.color.white));
                            smartLayoutJhq.setVisibility(View.VISIBLE);

                        } else if (filter.equals("??")) {
                            smartLayout3.setVisibility(View.GONE);
                            switJhq.setSlideable(false);
                            smartTvJhq.setTextColor(getResources().getColor(R.color.white2));
                            smartLayoutJhq.setVisibility(View.GONE);
                        } else {
                            smartTvJhq.setText(getString(R.string.smart7));
                            smartTvJhq.setTextColor(getResources().getColor(R.color.white));
                            switJhq.setState(false);
                            switJhq.setSlideable(true);
                            smartLayoutJhq.setVisibility(View.GONE);
                        }

                        if (window.equals("on")) {
                            smartTvCl.setText(getString(R.string.smart11_no));
                            smartTvCl.setTextColor(getResources().getColor(R.color.white));
                            switCl.setState(true);
                            switCl.setSlideable(true);
                        } else if (window.equals("??")) {
                            smartLayout4.setVisibility(View.GONE);
                            switCl.setSlideable(false);
                            smartTvCl.setTextColor(getResources().getColor(R.color.white2));

                        } else {
                            smartTvCl.setTextColor(getResources().getColor(R.color.white));
                            smartTvCl.setText(getString(R.string.smart11));
                            switCl.setState(false);
                            switCl.setSlideable(true);
                        }


                        if (blinds.equals("on")) {
                            switByc.setSlideable(true);
                            smartTvByc.setText(getString(R.string.smart13_no));
                            smartTvByc.setTextColor(getResources().getColor(R.color.white));
                            switByc.setState(true);
                        } else if (blinds.equals("??")) {
                            smartLayout5.setVisibility(View.GONE);
                            switByc.setSlideable(false);
                            smartTvByc.setTextColor(getResources().getColor(R.color.white2));
                            smartLayoutByc.setVisibility(View.GONE);
                        } else {
                            switByc.setSlideable(true);
                            smartTvByc.setTextColor(getResources().getColor(R.color.white));
                            smartTvByc.setText(getString(R.string.smart13));
                            switByc.setState(false);
                            smartLayoutByc.setVisibility(View.GONE);
                        }


                        if (electricLight.equals("on")) {
                            switDd.setSlideable(true);
                            switDd.setState(true);
                            smartTvDd.setText(getString(R.string.smart15_no));
                            smartTvDd.setTextColor(getResources().getColor(R.color.white));
                        } else if (electricLight.equals("??")) {
                            smartLayout6.setVisibility(View.GONE);
                            switDd.setSlideable(false);
                            smartTvDd.setTextColor(getResources().getColor(R.color.white2));
                        } else {
                            switDd.setSlideable(true);
                            switDd.setState(false);
                            smartTvDd.setText(getString(R.string.smart15));
                            smartTvDd.setTextColor(getResources().getColor(R.color.white));
                        }

                        smartTvKtwd2.setTextColor(getResources().getColor(R.color.white));
                        smartTvfs.setTextColor(getResources().getColor(R.color.white));

                        smartSeekbar.setClickable(true);
                        smartSeekbar.setEnabled(true);
                        smartSeekbar.setSelected(true);
                        smartSeekbar.setFocusable(true);

                        smartFs.setClickable(true);

                        if (airConditioningTemperature == -99) {
                            smartSeekbar.setClickable(false);
                            smartSeekbar.setEnabled(false);
                            smartSeekbar.setSelected(false);
                            smartSeekbar.setFocusable(false);
                            smartTvKtwd2.setTextColor(getResources().getColor(R.color.white2));
                        } else {
                            int num = 30 - airConditioningTemperature;
                            if (num > 30) {
                                smartSeekbar.setProgress(14);
                            } else if (num >= 0) {
                                smartSeekbar.setProgress(num);
                            } else {
                                smartSeekbar.setProgress(0);
                            }

                        }
                        fs_num = airConditioningWindSpeed;
                        if (fs_num == 1) {
                            smartFs.setBackgroundResource(R.drawable.control_fengsua);
                        } else if (fs_num == 2) {
                            smartFs.setBackgroundResource(R.drawable.control_fengsub);
                        } else if (fs_num == 3) {
                            smartFs.setBackgroundResource(R.drawable.control_fengsuc);
                        }

                        smartXh1.setClickable(true);
                        smartXh2.setClickable(true);
                        if (filter_cycle_n.equals("on")) {
                            setXh1(true);
                            xh_zt1 = true;
                        } else if (filter_cycle_n.equals("??")) {
                            setXh1(false);
                            smartXh1.setClickable(false);
                        } else {
                            setXh1(false);
                            xh_zt1 = false;
                        }

                        if (filter_cycle_w.equals("on")) {
                            setXh2(true);
                            xh_zt2 = true;
                        } else if (filter_cycle_w.equals("??")) {
                            setXh2(false);
                            smartXh2.setClickable(false);
                        } else {
                            setXh2(false);
                            xh_zt2 = false;
                        }

                        smartGs1.setClickable(true);
                        smartGs2.setClickable(true);
                        if (filter_mode.equals("on")) {
                            setGs(true);
                            gs_zt = true;
                        } else if (filter_mode.equals("??")) {
                            smartGs1.setClickable(false);
                            smartGs2.setClickable(false);
                        } else {
                            setGs(false);
                            gs_zt = false;
                        }


                        smartTvBfb.setTextColor(getResources().getColor(R.color.white));
                        smartBycSeekbar.setClickable(true);
                        smartBycSeekbar.setEnabled(true);
                        smartBycSeekbar.setSelected(true);
                        smartBycSeekbar.setFocusable(true);
                        if (blinds_percentage == -99) {
                            smartTvBfb.setTextColor(getResources().getColor(R.color.white2));
                            smartBycSeekbar.setClickable(false);
                            smartBycSeekbar.setEnabled(false);
                            smartBycSeekbar.setSelected(false);
                            smartBycSeekbar.setFocusable(false);
                        } else {
                            if (blinds_percentage > 100) {
                                smartBycSeekbar.setProgress(100);
                            } else if (blinds_percentage >= 0) {
                                smartBycSeekbar.setProgress(blinds_percentage);
                            } else {
                                smartBycSeekbar.setProgress(0);
                            }

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "JSONException_two", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case setTHREE:
                    smartLayout1.setVisibility(View.VISIBLE);
                    switSx.setSlideable(true);
                    smartTvSx.setTextColor(getResources().getColor(R.color.white));
                    if (msg.obj.toString().equals("on")) {
                        switSx.setState(true);
                        smartTvSx.setText(getString(R.string.smart2_no));
                    } else {
                        switSx.setState(false);
                        smartTvSx.setText(getString(R.string.smart2));
                    }

                    break;
                case setFOUR:
                    smartLayout2.setVisibility(View.VISIBLE);
                    switKt.setSlideable(true);
                    smartTvKt.setTextColor(getResources().getColor(R.color.white));
                    if (msg.obj.toString().equals("on")) {
                        switKt.setState(true);
                        smartTvKt.setText(getString(R.string.smart4_no));
                        smartLayoutKt.setVisibility(View.VISIBLE);
                    } else {
                        switKt.setState(false);
                        smartTvKt.setText(getString(R.string.smart4));
                        smartLayoutKt.setVisibility(View.GONE);

                    }

                    break;
                case setFIVE:
                    smartLayout2.setVisibility(View.VISIBLE);
                    smartSeekbar.setClickable(true);
                    smartSeekbar.setEnabled(true);
                    smartSeekbar.setSelected(true);
                    smartSeekbar.setFocusable(true);
                    smartTvKtwd2.setTextColor(getResources().getColor(R.color.white));
                    int num = 30 - msg.arg1;
                    if (num > 30) {
                        smartSeekbar.setProgress(14);
                    } else if (num >= 0) {
                        smartSeekbar.setProgress(num);
                    } else {
                        smartSeekbar.setProgress(0);
                    }
                    break;
                case setSIX:
                    smartLayout2.setVisibility(View.VISIBLE);
                    fs_num = msg.arg1;
                    smartFs.setClickable(true);
                    smartTvfs.setTextColor(getResources().getColor(R.color.white));
                    if (fs_num == 1) {
                        smartFs.setBackgroundResource(R.drawable.control_fengsua);
                    } else if (fs_num == 2) {
                        smartFs.setBackgroundResource(R.drawable.control_fengsub);
                    } else if (fs_num == 3) {
                        smartFs.setBackgroundResource(R.drawable.control_fengsuc);
                    }
                    break;
                case setSEVEN:
                    smartLayout3.setVisibility(View.VISIBLE);
                    switJhq.setSlideable(true);
                    smartTvJhq.setTextColor(getResources().getColor(R.color.white));
                    if (msg.obj.toString().equals("on")) {
                        switJhq.setState(true);
                        smartTvJhq.setText(getString(R.string.smart7_no));
                        smartLayoutJhq.setVisibility(View.VISIBLE);
                    } else {
                        switJhq.setState(false);
                        smartTvJhq.setText(getString(R.string.smart7));
                        smartLayoutJhq.setVisibility(View.GONE);

                    }

                    break;
                case setEIGHT:
                    smartLayout3.setVisibility(View.VISIBLE);
                    smartGs1.setClickable(true);
                    smartGs2.setClickable(true);
                    if (msg.obj.toString().equals("on")) {
                        setGs(true);
                        gs_zt = true;
                    } else {
                        setGs(false);
                        gs_zt = false;
                    }

                    break;
                case setNINE:
                    smartLayout3.setVisibility(View.VISIBLE);
                    smartXh1.setClickable(true);
                    if (msg.obj.toString().equals("on")) {
                        setXh1(true);
                        xh_zt1 = true;
                    } else {
                        setXh1(false);
                        xh_zt1 = false;
                    }
                    break;
                case setNINE2:
                    smartLayout3.setVisibility(View.VISIBLE);
                    smartXh2.setClickable(true);
                    if (msg.obj.toString().equals("on")) {
                        setXh2(true);
                        xh_zt2 = true;
                    } else {
                        setXh2(false);
                        xh_zt2 = false;
                    }
                    break;
                case setTEN:
                    smartLayout4.setVisibility(View.VISIBLE);

                    switCl.setSlideable(true);
                    smartTvCl.setTextColor(getResources().getColor(R.color.white));
                    if (msg.obj.toString().equals("on")) {

                        smartTvCl.setText(getString(R.string.smart11_no));
                        switCl.setState(true);
                    } else {
                        smartTvCl.setText(getString(R.string.smart11));
                        switCl.setState(false);
                    }
                    break;
                case setELEVEN:

                    smartLayout4.setVisibility(View.VISIBLE);
                    switByc.setSlideable(true);
                    smartTvByc.setTextColor(getResources().getColor(R.color.white));
                    if (msg.obj.toString().equals("on")) {
                        smartTvByc.setText(getString(R.string.smart13_no));
                        switByc.setState(true);
                        smartLayoutByc.setVisibility(View.VISIBLE);
                    } else {
                        smartTvByc.setText(getString(R.string.smart13));
                        switByc.setState(false);
                        smartLayoutByc.setVisibility(View.GONE);
                    }
                    break;
                case setTWELVE:
                    smartLayout6.setVisibility(View.VISIBLE);
                    switDd.setSlideable(true);
                    smartTvDd.setTextColor(getResources().getColor(R.color.white));
                    if (msg.obj.toString().equals("on")) {
                        switDd.setState(true);
                        smartTvDd.setText(getString(R.string.smart15_no));
                    } else {
                        switDd.setState(false);
                        smartTvDd.setText(getString(R.string.smart15));
                    }
                    break;

                case setTHIRTEEN:
                    smartLayout5.setVisibility(View.VISIBLE);
                    smartTvBfb.setTextColor(getResources().getColor(R.color.white));
                    smartBycSeekbar.setClickable(true);
                    smartBycSeekbar.setEnabled(true);
                    smartBycSeekbar.setSelected(true);
                    smartBycSeekbar.setFocusable(true);
                    int blinds_percentage = msg.arg1;
                    if (blinds_percentage > 100) {
                        smartBycSeekbar.setProgress(100);
                    } else if (blinds_percentage >= 0) {
                        smartBycSeekbar.setProgress(blinds_percentage);
                    } else {
                        smartBycSeekbar.setProgress(0);
                    }
                    break;

                case setFOURTEENTH:
                    try {
                        jsonObject = new JSONObject(msg.obj.toString());
                        String temperature = jsonObject.optString("temperature", "??");
                        String humidity = jsonObject.optString("humidity", "??");
                        String pm = jsonObject.optString("pm2.5", "??");

                        smartTvWd.setText(temperature);
                        smartTvSd.setText(humidity);
                        smartTvPm.setText(pm);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
            }
        }
    };


    private String sid = "12345678";

    public void push_channel(String cmd, boolean channel) {
        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", cmd);
            if (channel) {
                jsonObject.put("channel_0", "on");
            } else {
                jsonObject.put("channel_0", "off");
            }
            jsonObject.put("sid", sid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    public void push_var(String cmd, int var) {
        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", cmd);
            jsonObject.put("var", var);
            jsonObject.put("sid", sid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    public void push_var(String cmd, String var) {
        try {

            //发送请求所有数据消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", cmd);
            jsonObject.put("var", var);
            jsonObject.put("sid", sid);
            String js = jsonObject.toString();
            publish_String(js);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
        }
    }

}
