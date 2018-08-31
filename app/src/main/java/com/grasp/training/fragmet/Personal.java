package com.grasp.training.fragmet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.activity.LoginActivity;
import com.grasp.training.dialog.Dialog_touxiao_layout;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.SharedPreferencesUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class Personal extends Fragment {
    @BindView(R.id.personal_tx)
    ImageView personalTx;
    Unbinder unbinder;
    @BindView(R.id.personal_tv_name)
    TextView personalTvName;
    @BindView(R.id.personal_tv_sb)
    TextView personalTvSb;
    @BindView(R.id.personal_layout)
    RelativeLayout personalTvLayout1;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.personal, container, false);
        context = getActivity();

        unbinder = ButterKnife.bind(this, view);


        String userPic = "";
        userPic = (String) SharedPreferencesUtils.getParam(context, MyApplication.NAME_TX, "");
        if (!TextUtils.isEmpty(userPic)) {
            Log.d("logo", "+++" + userPic);
            ImageLoader.getInstance().displayImage("file://" + userPic, personalTx, MyApplication.options2);
        } else {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.personalcenter_tabbar_portrait_selected, personalTx, MyApplication.options2);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        personalTvName.setText(MainActivity.NameUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.personal_tx, R.id.personal_layout, R.id.per_tui})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.personal_tx:
                getTp();
                break;
            case R.id.personal_layout:
                break;

            case R.id.per_tui:
                startActivity(new Intent(context, LoginActivity.class));
                break;
        }
    }

    int a=0;
    private void getTp() {   //获得图片
        // TODO Auto-generated method stub
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        layout = new Dialog_touxiao_layout((Activity) context);
        dialog.setView(layout);
        builder = dialog.show();

        //aaa
    }

    private File tempFile;
    private Dialog builder, timeDialog2;
    private Dialog_touxiao_layout layout;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  //设置返回调用
        // TODO Auto-generated method stub

        super.onActivityResult(requestCode, resultCode, data);
        Log.d("qqq", "onActivityResult   " + requestCode);
        switch (requestCode) {

            case Dialog_touxiao_layout.PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        layout.crop(uri);
                    }

                }
                break;

            case Dialog_touxiao_layout.PHOTO_REQUEST_CAMERA:

                if (layout.hasSdcard()) {
                    tempFile = new File(Environment.getExternalStorageDirectory(),
                            layout.PHOTO_FILE_NAME);

                    layout.crop(Uri.fromFile(tempFile));

                } else {
                    Toast.makeText(context, R.string.nocz, Toast.LENGTH_SHORT).show();
                }
                break;
            case Dialog_touxiao_layout.PHOTO_REQUEST_CUT:

                builder.dismiss();
                Log.d("bitmap", data + "+++++++" + data.getData());
//                if(data==null){
//                    return;
//                }
//                if(data.getData()==null){
//
//
//                    break;
//                }
//                Bundle extras = data.getExtras();
//                if (extras != null) {
//                    Bitmap bitmap =extras.getParcelable("data");
//                    saveBitmap(bitmap);
////                    personalTx.setImageBitmap(bitmap);
//                    Log.d("qqq","bitmap  "+bitmap);

                String newName = layout.PHOTO_FILE_NAME;
                String uploadFile = Environment.getExternalStorageDirectory() + "/"
                        + newName;
                String url = uploadFile;


                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                ImageLoader.getInstance().displayImage("file://" + url, personalTx, MyApplication.options2);
                SharedPreferencesUtils.setParam(context, MyApplication.NAME_TX, url);
//                }


//                bitmap=getBitmapFromUri(data.getData(),this);

//                    Drawable drawable = new BitmapDrawable(getResources(), photo);


//                Log.d("qqq","good "+url);

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    /**
     * 保存方法
     */
    public void saveBitmap(Bitmap bm) {
        if (bm == null) {
            return;
        }

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) // 判断是否可以对SDcard进行操作
        {    // 获取SDCard指定目录下
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String t = format.format(new Date());
            String newName = layout.PHOTO_FILE_NAME;
            String sdCardDir = Environment.getExternalStorageDirectory() + "";
            File dirFile = new File(sdCardDir);  //目录转化成文件夹
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }                          //文件夹有啦，就可以保存图片啦

            File file = new File(sdCardDir, "" + newName);// 在SDcard的目录下创建图片文,以当前时间为其命名

            try {
                FileOutputStream out = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


}
