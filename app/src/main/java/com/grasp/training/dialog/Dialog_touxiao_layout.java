package com.grasp.training.dialog;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


import com.grasp.training.R;
import com.grasp.training.tool.BaseFragmentActivity;

import java.io.File;


public class Dialog_touxiao_layout extends LinearLayout {
//	Uri imUri;
	Context c;
	/* 头像名称 */
	public final String PHOTO_FILE_NAME = "grasp_tx.png";
	public final static int PHOTO_REQUEST_CAMERA = 9111;// 拍照
	public final static int PHOTO_REQUEST_GALLERY = 9222;// 从相册中选择
	public final static int PHOTO_REQUEST_CUT = 9333;// 结果
	public Dialog_touxiao_layout(Context context) {
		super(context);
		 LayoutInflater.from(context).inflate(R.layout.dialog_touxiao, this)		;
		c=context;
//		Date d=new Date();
//		long s=d.getTime();
//		String s1="tp"+s+".png";
//		imUri = geturi(s1);     //地址
		findViewById(R.id.kon_pz).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 判断存储卡是否可以用，可用进行存储
				if (hasSdcard()) {
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(Environment
									.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
					((Activity)c).startActivityForResult(intent, PHOTO_REQUEST_CAMERA);



				}

			}
		});
		
         findViewById(R.id.kon_bd).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 激活系统图库，选择一张图片
				Intent intent = new Intent(Intent.ACTION_PICK);
//				intent.setType("image/*");
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						"image/*");
				((Activity)c).startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
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
	public void crop(Uri uri) {  //裁剪图片
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 160);
		intent.putExtra("outputY", 160);
		// 图片格式
		intent.putExtra("outputFormat", "png");
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		intent.putExtra(MediaStore.EXTRA_OUTPUT,   //裁剪后保存
				Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		((BaseFragmentActivity)c).startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}
	
	public boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
}
