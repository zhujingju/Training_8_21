package com.grasp.training.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.grasp.training.MainActivity;
import com.grasp.training.R;
import com.grasp.training.tool.BaseActivity;
import com.grasp.training.tool.MyApplication;
import com.grasp.training.tool.Utility;
import com.grasp.training.tool.myActivityManage;

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
