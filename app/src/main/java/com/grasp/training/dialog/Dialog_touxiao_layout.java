package com.grasp.training.dialog;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.BaseFragmentActivity;
import com.grasp.training.tool.PhotoUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;


public  class Dialog_touxiao_layout extends LinearLayout {
    //	Uri imUri;
    Context c;
    /* 头像名称 */
//	public final String PHOTO_FILE_NAME = "grasp_tx.png";
//    public final static int PHOTO_REQUEST_CAMERA = 9111;// 拍照
//    public final static int PHOTO_REQUEST_GALLERY = 9222;// 从相册中选择
//    public final static int PHOTO_REQUEST_CUT = 9333;// 结果

    public static final int CODE_GALLERY_REQUEST = 0xa0;
    public static final int CODE_CAMERA_REQUEST = 0xa1;
    public static final int CODE_RESULT_REQUEST = 0xa2;
    public File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    public File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    public Uri imageUri;
    public Uri cropImageUri;


    public Dialog_touxiao_layout(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.dialog_touxiao, this);
        c = context;
//		Date d=new Date();
//		long s=d.getTime();
//		String s1="tp"+s+".png";
//		imUri = geturi(s1);     //地址
        findViewById(R.id.kon_pz).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (hasSdcard()) {
                    imageUri = Uri.fromFile(fileUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        //通过FileProvider创建一个content类型的Uri
                        imageUri = FileProvider.getUriForFile(c, "com.grasp.training.cartoonprovider", fileUri);
                    PhotoUtils.takePicture((Activity) c, imageUri, CODE_CAMERA_REQUEST);
//                    dialogTouxiaoLayoutIn.paizhao();
                } else {
                    Toast.makeText(c, "设备没有SD卡！", Toast.LENGTH_SHORT).show();
                }


            }
        });

        findViewById(R.id.kon_bd).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // 激活系统图库，选择一张图片
//                Intent intent = new Intent(Intent.ACTION_PICK);
////				intent.setType("image/*");
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        "image/*");
//                ((Activity) c).startActivityForResult(intent,PHOTO_REQUEST_GALLERY);


                PhotoUtils.openPic((Activity) c, CODE_GALLERY_REQUEST);

//                dialogTouxiaoLayoutIn.shangc();
            }
        });
    }

    //	public Uri geturi(String name) {
//		File fi = new File(Environment.getExternalStorageDirectory().toString()
//				 , name);  //存 在guanke目录下
//		f=fi;
//		return Uri.fromFile(fi);
//
//	}
//    public void crop(Uri uri) {  //裁剪图片
//        // 裁剪图片意图
//        File CropPhoto = new File(Pash(), getName());
//        try {
//            if (CropPhoto.exists()) {
//                CropPhoto.delete();
//            }
//            CropPhoto.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        // 裁剪框的比例，1：1
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // 裁剪后输出图片的尺寸大小
//        intent.putExtra("outputX", 160);
//        intent.putExtra("outputY", 160);
//        // 图片格式
//        intent.putExtra("outputFormat", "png");
////		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//
//        intent.putExtra("noFaceDetection", true);// 取消人脸识别
//        intent.putExtra("return-data", false);// true:不返回uri，false：返回uri
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,   //裁剪后保存
//                Uri.fromFile(new File(Pash(), getName())));
//        ((BaseFragmentActivity) c).startActivityForResult(intent, PHOTO_REQUEST_CUT);
//    }

    public boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

//    public String getName() {
//        String name = "";
////		Date date=new Date();
////		long time=date.getTime();
//        name = "grasp_tx.png";
//        return name;
//    }
//
    public String Pash() {
        String pash = Environment.getExternalStorageDirectory() + "/Training_grasp/";
        File f = new File(pash);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return Environment.getExternalStorageDirectory() + "";
            }
        }


        return pash;
    }

    private Dialog_touxiao_layoutIn dialogTouxiaoLayoutIn;


    public void setDialogTouxiaoLayoutIn(Dialog_touxiao_layoutIn dialogTouxiaoLayoutIn) {
        this.dialogTouxiaoLayoutIn = dialogTouxiaoLayoutIn;
    }

    public interface   Dialog_touxiao_layoutIn{
        public void paizhao();
        public void shangc();
    }
}
