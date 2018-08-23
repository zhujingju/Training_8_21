package com.grasp.training.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.Player.Core.PlayerClient;
import com.Player.web.websocket.ClientCore;
import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.Umeye_sdk.Constants;
import com.grasp.training.Umeye_sdk.ShowProgress;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.til_username)
    TextInputLayout tilUsername;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.login)
    Button login;
    EditText userEditText;
    EditText pwdEditText;
    @BindView(R.id.login_y)
    ConstraintLayout loginY;
    @BindView(R.id.login_zc)
    TextView loginZc;

    private ClientCore clientCore;
    private PlayerClient playClient;
    private MyApplication appMain;

    @Override
    public int setLayoutId() {
        return R.layout.login_activity;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        initPlay();
        userEditText = tilUsername.getEditText();
        pwdEditText = tilPassword.getEditText();
    }

    @Override
    public void initObject() {
//EditText添加文本变化监听
        userEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("qqq", "beforeTextChanged执行了....s = " + s + "---start = " + start + "---count = " + count + "---after = " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("qqq", "onTextChanged执行了....s = " + s + "---start = " + start + "---count = " + count + "---before = " + before);
//                if (s.length() > 7) {
//                    tilUsername.setErrorEnabled(true);//设置是否打开错误提示
//                    tilUsername.setError("用户名长度不能超过8个");//设置错误提示的信息
//                } else {
//                    tilUsername.setErrorEnabled(false);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("qqq", "afterTextChanged执行了....s = " + s);
            }
        });

        pwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 6) {
                    tilPassword.setErrorEnabled(true);
                    tilPassword.setError("密码长度不能小于6个");
                } else {
                    tilPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void initListener() {

    }

    @Override
    public void init() {

    }




    ShowProgress pd;

    void initPlay() {
        appMain = (MyApplication) this.getApplicationContext();
        playClient = appMain.getPlayerclient();
//        startBestServer();
    }

    /**
     * 新接口，获取最优P2P服务器，然后连接
     */
    void startBestServer() {
        clientCore = ClientCore.getInstance();
        int language = 1;
        clientCore.setupHost(this, Constants.server, 0, Utility.getImsi(this),
                language, Constants.CustomName, Utility.getVersionName(this),
                "");
        clientCore.getCurrentBestServer(this, handler);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);
            Log.e("test", "startBestServer");
//            ha.sendEmptyMessageDelayed(222, 500);
            if (pd != null) {
                pd.dismiss();
            }
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }

    };


    @OnClick({R.id.login_zc, R.id.login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_zc:
                startActivity(new Intent(LoginActivity.this, RegisteredActivity.class));
                break;
            case R.id.login:

                Log.d("qqq", "login");

                if (pd == null) {
                    pd = new ShowProgress(getContext());
                    pd.setMessage("登录中...");
                    pd.setCanceledOnTouchOutside(true);
                }
                if (pd != null) {
                    pd.show();
                }
                startBestServer();
                break;
        }
    }
}
