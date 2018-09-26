package com.grasp.training.fragmet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.activity.ChangePasswordActivity;
import com.grasp.training.activity.LoginActivity;
import com.grasp.training.activity.SetPersonalActivity;
import com.grasp.training.dialog.Dialog_touxiao_layout;
import com.grasp.training.tool.AutoInstall;
import com.grasp.training.tool.BaseMqttFragment;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.PhotoUtils;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.grasp.training.tool.Tool;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.grasp.training.dialog.Dialog_touxiao_layout.CODE_CAMERA_REQUEST;
import static com.grasp.training.dialog.Dialog_touxiao_layout.CODE_GALLERY_REQUEST;
import static com.grasp.training.dialog.Dialog_touxiao_layout.CODE_RESULT_REQUEST;
import static com.grasp.training.tool.MyApplication.imageLoader;


public class Personal extends BaseMqttFragment {
    @BindView(R.id.personal_tx)
    ImageView personalTx;
    Unbinder unbinder;
    @BindView(R.id.personal_tv_name)
    TextView personalTvName;
    @BindView(R.id.personal_tv_sb)
    TextView personalTvSb;
    @BindView(R.id.personal_layout)
    RelativeLayout personalTvLayout1;
    @BindView(R.id.per_sys_im)
    ImageView per_sys_im;
    private Context context;
    private boolean dian_zt = false;
    private String nc;


    @Override
    public int getInflate() {
        return R.layout.personal;
    }

    @Override
    public void init(View view) {
        context = getActivity();

        unbinder = ButterKnife.bind(this, view);

        handler.sendEmptyMessageDelayed(2222, 0);
        per_sys_im.setVisibility(View.INVISIBLE);

//        ImageLoader.getInstance().clearDiskCache();
//        ImageLoader.getInstance().clearMemoryCache();
        String uthumbnail = SharedPreferencesUtils.getParam(context, LoginActivity.c_im, "").toString();
        if (!TextUtils.isEmpty(uthumbnail)) {
            ImageLoader.getInstance().displayImage("" + uthumbnail, personalTx, MyApplication.options2);
        } else {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.personalcenter_tabbar_portrait_selected, personalTx, MyApplication.options2);
        }
    }

    private String myTopic = "iotbroad/iot/user";

    @Override
    public String getMyTopic() {
        return myTopic;
    }

    @Override
    public String getMyTopicDing() {
        return myTopic;
    }

    @Override
    public void onStart() {
        super.onStart();
//        public static final String c_im="Login_im";
        Log.e("qqq", "ver=" + getAppVersion("com.grasp.training"));
        nc = SharedPreferencesUtils.getParam(context, LoginActivity.c_nc, "").toString();
        personalTvName.setText(nc);
        handler.sendEmptyMessageDelayed(4001, 0);


    }

    @Override
    public void MyMessageArrived(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message me;
                    String uthumbnail;
                    JSONObject jsonObject = new JSONObject(message);
                    String cmd = jsonObject.optString("cmd", "");
                    String uname = jsonObject.optString("uname", "");
                    if (!uname.equals(MainActivity.NameUser)) {
                        return;
                    }
                    String clientid = jsonObject.optString("clientid", "");
                    if (!clientid.equals(Tool.getIMEI(getContext()))) {
                        return;
                    }
                    switch (cmd) {
                        case "queryuserpicture_ok":
                            uthumbnail = jsonObject.optString("uthumbnail", "");
                            me = new Message();
                            if (uthumbnail.equals("")) {
                            } else {
                                me.what = 4000;
                                me.obj = uthumbnail;
                                String dname = jsonObject.optString("nickname", "");
//                                Log.e("qqq","nickname="+dname+" nc="+nc);
                                if (!dname.equals("")) {
                                    nc = dname;
//                                    Log.e("qqq","nickname="+dname+" nc="+nc);
                                    SharedPreferencesUtils.setParam(context, LoginActivity.c_nc, nc);
                                }

                                SharedPreferencesUtils.setParam(context, LoginActivity.c_im, uthumbnail);
                            }
                            handler.sendMessageDelayed(me, 500);
                            break;
                        case "uploaduserpicture_ok":
                            uthumbnail = jsonObject.optString("uthumbnail", "");
                            me = new Message();
                            if (uthumbnail.equals("")) {
                                me.what = 3001;
                                me.obj = "图片地址为空";
                            } else {
                                me.what = 3000;
                                me.obj = uthumbnail;
                                SharedPreferencesUtils.setParam(context, LoginActivity.c_im, uthumbnail);
                            }

                            handler.sendMessageDelayed(me, 500);
                            break;
                        case "uploaduserpicture_failed":
                            me = new Message();
                            me.what = 3001;
                            me.obj = jsonObject.optString("err", "");
                            handler.sendMessageDelayed(me, 500);
                            break;

                        case "querysoftwareversion_ok"://获取服务器版本号和url

                            updata_url = jsonObject.optString("url", "");
                            int ver = jsonObject.optInt("version", 0);
                            int myVer = getAppVersion("com.grasp.training");
                            if (myVer < ver) {//更新
                                updata_zt=true;
                                handler.sendEmptyMessageDelayed(2223, 500);
                            } else {//
                                handler.sendEmptyMessageDelayed(2224, 500);
                            }


                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2222:
                    handler.removeMessages(2222);
                    if (!isConnected()) {
                        handler.sendEmptyMessageDelayed(2222, 500);
                        return;
                    }
                    if (!updata_zt) {
                        push_val();
                    }
                    break;

                case 2223:  //打开判断更新窗口
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (updata_url.equals("")) {
                        Toast.makeText(context, "url为空,无法更新", Toast.LENGTH_LONG).show();
                        return;
                    }
                    per_sys_im.setVisibility(View.VISIBLE);
                    if (dian_zt) {
                        if (!normalDialog_zt) {
                            upDate(updata_url);
                        }
                    }
                    break;
                case 2224:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (dian_zt) {
                        Toast.makeText(context, "当前已是最高版本！", Toast.LENGTH_LONG).show();
                    }

                    break;
                case 1000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "修改成功！", Toast.LENGTH_LONG).show();

                    break;
                case 1001:
                    if (dialog != null) {
                        dialog.dismiss();
                    }

                    Toast.makeText(context, "修改失败！" + msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;

                case 3000:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    //清除缓存的某一张图片
                    DiskCacheUtils.removeFromCache(msg.obj.toString(), ImageLoader.getInstance().getDiskCache());
                    MemoryCacheUtils.removeFromCache(msg.obj.toString(), ImageLoader.getInstance().getMemoryCache());

                    ImageLoader.getInstance().displayImage(msg.obj.toString(), personalTx, MyApplication.options2);
                    Toast.makeText(context, "头像修改成功！", Toast.LENGTH_LONG).show();

                    break;

                case 3001:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "头像修改失败！" + msg.obj.toString(), Toast.LENGTH_LONG).show();

                    break;
                case 4000:
                    //清除缓存的某一张图片
                    DiskCacheUtils.removeFromCache(msg.obj.toString(), ImageLoader.getInstance().getDiskCache());
                    MemoryCacheUtils.removeFromCache(msg.obj.toString(), ImageLoader.getInstance().getMemoryCache());

                    ImageLoader.getInstance().displayImage(msg.obj.toString(), personalTx, MyApplication.options2);
//                    Log.e("qqq"," nc="+nc);
                    personalTvName.setText(nc);
                    break;
                case 4001:
                    handler.removeMessages(4001);
                    if (isConnected()) {
                        push_tx();
                    } else {
                        handler.sendEmptyMessageDelayed(4001, 1000);
                    }
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        handler.removeMessages(1000);
        handler.removeMessages(1001);
        handler.removeMessages(2222);
        handler.removeMessages(2223);
        handler.removeMessages(2224);
        handler.removeMessages(3000);
        handler.removeMessages(3001);
        handler.removeMessages(4001);
        handler.removeMessages(4000);


        notificationHandler.removeMessages(1000);
        notificationHandler.removeMessages(2000);
        notificationHandler.removeMessages(3000);
        super.onDestroyView();
        unbinder.unbind();

    }


    @OnClick({R.id.per_xgsj, R.id.per_xgmm, R.id.per_gx, R.id.personal_tx, R.id.personal_layout, R.id.per_tui})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.per_xgsj:
                SetPersonalActivity.stastSetPersonal(context, 1);
                break;
            case R.id.personal_tx:
                getTp();

                break;
            case R.id.personal_layout:
                SetPersonalActivity.stastSetPersonal(context, 0);
                break;
            case R.id.per_xgmm:
                startActivity(new Intent(context, ChangePasswordActivity.class));
                break;
            case R.id.per_tui:
                SharedPreferencesUtils.setParam(getContext(), LoginActivity.c_im, "");
                SharedPreferencesUtils.setParam(getContext(), LoginActivity.c_nc, "");
                SharedPreferencesUtils.setParam(getContext(), MyApplication.NAME_USER, "");
                startActivity(new Intent(context, LoginActivity.class));
                ((Activity) context).finish();
                break;
            case R.id.per_gx:
                dian_zt = true;
                if (updata_zt) {
                    if (updata_url.equals("")) {
                        push_val();
                        showPro2();
                    } else {
                        upDate(updata_url);
                    }
                } else {
                    push_val();
                    showPro2();
                }

                break;
        }
    }

    int a = 0;

    private void getTp() {   //获得图片
        // TODO Auto-generated method stub

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        layout = new Dialog_touxiao_layout((Activity) context);
        layout.setDialogTouxiaoLayoutIn(new Dialog_touxiao_layout.Dialog_touxiao_layoutIn() {
            @Override
            public void paizhao() {
                Log.e("qqq", "paizhao");
//                imageUri = FileProvider.getUriForFile(context, "com.grasp.training.cartoonprovider", fileUri);
//                takePhoto.onPickFromCaptureWithCrop(imageUri, cropOptions);
            }

            @Override
            public void shangc() {
//                cropImageUri = FileProvider.getUriForFile(context, "com.grasp.training.cartoonprovider", fileCropUri);
//                takePhoto.onPickFromDocumentsWithCrop(imageUri, cropOptions);
            }
        });
        dialog.setView(layout);
        builder = dialog.show();

        //aaa
    }

    private File tempFile;
    private Dialog builder, timeDialog2;
    private Dialog_touxiao_layout layout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  //设置返回调用
        // TODO Auto-generated method stub

        super.onActivityResult(requestCode, resultCode, data);
        Log.d("qqq", "onActivityResult   " + requestCode);
        int output_X = 200, output_Y = 200;
        if (resultCode == ((Activity) getContext()).RESULT_OK) {
            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照完成回调
                    if (layout.fileCropUri != null) {
                        layout.cropImageUri = Uri.fromFile(layout.fileCropUri);
                        PhotoUtils.cropImageUri(Personal.this, layout.imageUri, layout.cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                    } else {
                        Toast.makeText(context, "拍照失败,本手机内存不足，请使用本地上传", Toast.LENGTH_LONG).show();
                    }

                    break;
                case CODE_GALLERY_REQUEST://访问相册完成回调
                    if (layout.hasSdcard()) {
                        layout.cropImageUri = Uri.fromFile(layout.fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(getContext(), data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            newUri = FileProvider.getUriForFile(getContext(), "com.MainActivity.provider", new File(newUri.getPath()));
                        PhotoUtils.cropImageUri(getActivity(), newUri, layout.cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                    } else {
                        Toast.makeText(getContext(), "设备没有SD卡!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(layout.cropImageUri, getContext());
//                    Bitmap bitmap = null;
//                    try {
//                        bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(layout.cropImageUri));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }

                    if (bitmap != null) {
                        showImages(bitmap);
                    }
                    break;

                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }


    }


    /**
     * 展示图片
     *
     * @param bitmap
     */
    private void showImages(Bitmap bitmap) {
//        personalTx.setImageBitmap(bitmap);
        if (builder != null) {
            builder.cancel();
        }
//        ImageLoader.getInstance().clearDiskCache();
//        ImageLoader.getInstance().clearMemoryCache();
//        SharedPreferencesUtils.setParam(context, MyApplication.NAME_TX, Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
//        Log.e("qqq","cropImageUri="+layout.cropImageUri);
//        ImageLoader.getInstance().displayImage("file://" + Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg", personalTx, MyApplication.options2);

        showPro();
        push_name(bitmap);
    }

    public String getName() {
        String name = "";
        Date date = new Date();
        long time = date.getTime();
        name = "grasp_" + time + ".png";
        return name;
    }


    /**
     * 图片转成string *  * @param bitmap * @return
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }


    public void push_name(final Bitmap bitmap) { //修改头像
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "uploaduserpicture");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("format", "jpg");
                    jsonObject.put("data", convertIconToString(bitmap));


                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }


    public void push_tx() { //获取头像
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "queryuserpicture");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    String js = jsonObject.toString();
                    publish_String(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }

    private boolean updata_zt = false;
    private String updata_url = "";

    public void push_val() { //检查版本更新
        final String fs = "iotbroad/iot/software";
        subscribe(fs);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //发送请求所有数据消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "querysoftwareversion");
                    jsonObject.put("uname", MainActivity.NameUser);
                    jsonObject.put("clientid", Tool.getIMEI(getContext()));
                    jsonObject.put("name", "app-smarthome");
                    String js = jsonObject.toString();
                    publish_String2(js, fs);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

    }

    private ProgressDialog dialog;

    public void showPro() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("上传头像中...");
        dialog.setCancelable(true);
        dialog.show();
    }


    public void showPro2() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("检查更新版本...");
        dialog.setCancelable(true);
        dialog.show();
    }


    private boolean normalDialog_zt = false;

    private void upDate(final String url) {
        normalDialog_zt = true;
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        normalDialog.setTitle("版本升级");
        normalDialog.setMessage("确认有新版本！！！");
        normalDialog.setNegativeButton("升级",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        Update(url);
                        normalDialog_zt = false;
                    }
                });
        normalDialog.setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        normalDialog_zt = false;
                    }
                });

        // 显示
        normalDialog.setCancelable(false);
        normalDialog.show();
    }


    public void Update(String url) {//更新
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification=new Notification.Builder(context).setSmallIcon(R.drawable.icon).build();
//        notification = new Notification(R.drawable.icon, "下载进度...", System.currentTimeMillis());
        download(url, Path, apkName);
    }


    /**
     * 发送通知
     */
    public void sendNotification() {
        contentView = new RemoteViews(context.getPackageName(), R.layout.notify_view);
        contentView.setProgressBar(R.id.pb, 100, 0, false);
        notification.contentView = contentView;
        manager.notify(0, notification);
    }

    private RemoteViews contentView;
    private Notification notification;
    private NotificationManager manager;
    private long progress;
    private Message msg;
    Handler notificationHandler = new Handler() {// 更改进度条的进度

        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            Log.e("qqq","msg.w="+msg);
            switch (msg.what) {
                case 1000:
                    notificationHandler.removeMessages(1000);
                    contentView.setProgressBar(R.id.pb, 100, (int) progress, false);

                    notification.contentView = contentView;

                    manager.notify(0, notification);
                    notificationHandler.sendEmptyMessageDelayed(1000,1000);
                    break;

                case 2000:
                    notificationHandler.removeMessages(1000);
                    Toast.makeText(context, "下载失败", Toast.LENGTH_LONG).show();
                    manager.cancel(0);
                    break;
                case 3000:
                    notificationHandler.removeMessages(1000);
                    Toast.makeText(context, "下载成功", Toast.LENGTH_LONG).show();
                    manager.cancel(0);
                    AutoInstall.setUrl(Path + apkName);
//                    Log.e("qqq",Path + apkName);
                    AutoInstall.install(context);
                    break;
            }


        }

        ;

    };
    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称
     */

    String Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    String apkName = "Training.apk";


    OkHttpClient client;

    public void download(final String url, final String destFileDir, final String destFileName) {
        Log.e("tag", url + "  " + destFileDir + "  " + destFileName);
        client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败监听回调
//                ha.sendEmptyMessageDelayed(MyServer_start,1000);
                notificationHandler.sendEmptyMessageDelayed(2000, 0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendNotification();
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    progress=0;
                    notificationHandler.sendEmptyMessage(1000);
                    while ((len = is.read(buf)) != -1&&progress<=100) {
                        fos.write(buf, 0, len);
                        sum += len;
                        progress = (int) (sum * 1.0f / total * 100);
                        Log.e("tag", "progress="+progress);
                        // 下载中更新进度条
                    }
                    fos.flush();
                    Log.e("tag", "下载完成");
//                    ha.sendEmptyMessageDelayed(MyServer_ok,0);
                    notificationHandler.sendEmptyMessageDelayed(3000, 500);
                    // 下载完成
                } catch (Exception e) {
                    Log.e("tag", "下载 失败");
//                    ha.sendEmptyMessageDelayed(MyServer_start,1000);
                    notificationHandler.sendEmptyMessageDelayed(2000, 500);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }


    /*
     *获取程序的版本号
     */
    public int getAppVersion(String packname) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(packname, 0);
            return packinfo.versionCode;
        } catch (Exception e) {
        }
        return 0;

    }


}
