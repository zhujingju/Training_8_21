package com.grasp.training.fragmet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
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
import com.grasp.training.tool.PhotoUtils;
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

import static com.grasp.training.dialog.Dialog_touxiao_layout.CODE_CAMERA_REQUEST;
import static com.grasp.training.dialog.Dialog_touxiao_layout.CODE_GALLERY_REQUEST;
import static com.grasp.training.dialog.Dialog_touxiao_layout.CODE_RESULT_REQUEST;


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


        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
        String userPic = "";
        userPic = (String) SharedPreferencesUtils.getParam(context, MyApplication.NAME_TX, "");
        Log.e("qqq","userPic="+userPic);
        if (!TextUtils.isEmpty(userPic)) {
            ImageLoader.getInstance().displayImage("file://" + userPic, personalTx, MyApplication.options2);
//            ImageLoader.getInstance().displayImage("" + userPic, personalTx, MyApplication.options2);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  //设置返回调用
        // TODO Auto-generated method stub

        super.onActivityResult(requestCode, resultCode, data);
        Log.d("qqq", "onActivityResult   " + requestCode);
        int output_X = 200, output_Y = 200;
        if (resultCode == ((Activity)getContext()).RESULT_OK) {
            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照完成回调
                        layout.cropImageUri = Uri.fromFile(layout.fileCropUri);
                        PhotoUtils.cropImageUri(this, layout.imageUri, layout.cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
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
     * @param bitmap
     */
    private void showImages(Bitmap bitmap) {
//        personalTx.setImageBitmap(bitmap);
        if(builder!=null){
            builder.cancel();
        }
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
        SharedPreferencesUtils.setParam(context, MyApplication.NAME_TX, Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
        Log.e("qqq","cropImageUri="+layout.cropImageUri);
        ImageLoader.getInstance().displayImage("file://" + Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg", personalTx, MyApplication.options2);

    }
    public String getName(){
        String name="";
		Date date=new Date();
		long time=date.getTime();
        name="grasp_"+time+".png";
        return name;
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
            String newName = getName();
            String sdCardDir =layout.Pash();
            File dirFile = new File(sdCardDir);  //目录转化成文件夹
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }                          //文件夹有啦，就可以保存图片啦

            File file = new File(sdCardDir, "" + newName);// 在SDcard的目录下创建图片文,以当前时间为其命名
            Log.e("qqq","userPic="+sdCardDir+newName);
            try {
                FileOutputStream out = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
                SharedPreferencesUtils.setParam(context, MyApplication.NAME_TX, sdCardDir+newName);
                Log.e("qqq","userPic="+sdCardDir+newName);
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
