package com.lvpf.samckdemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lvpf.samckdemo.base.BaseActivity;
import com.lvpf.samckdemo.model.LoginConfig;
import com.lvpf.samckdemo.service.LoginTask;
import com.lvpf.myapplicationd.R;
import com.lvpf.samckdemo.service.RegisterTask;
import com.lvpf.samckdemo.sql.SQLite;
import com.lvpf.samckdemo.manager.XmppConnectionManager;

/**
 * 登录注册类
 */
public class MainActivity extends BaseActivity {
    private EditText acount;
    private EditText password;
    private Button sign_in_button;
    private Button register_button;
    private LoginConfig loginConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        int version = 1;
        SQLite dbOpenHelper = new SQLite(getApplicationContext(),
                "smackSQLite.db", null, version);
        loginConfig = getLoginConfig();
        XmppConnectionManager.getInstance().init(loginConfig);
    }
    private void initView(){
        acount= (EditText) findViewById(R.id.acount);
        password= (EditText) findViewById(R.id.password);
        sign_in_button= (Button) findViewById(R.id.sign_in_button);
        register_button= (Button) findViewById(R.id.register_button);
        acount.setText("lvpf");
        password.setText("123456");
//        登录
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strAcout=acount.getText().toString();
                String strPassword=password.getText().toString();
                loginConfig.setPassword(strPassword);
                loginConfig.setUsername(strAcout);
                LoginTask loginTask = new LoginTask(
                        MainActivity.this, loginConfig);
                loginTask.execute();


            }
        });
//注册
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strAcout=acount.getText().toString();
                String strPassword=password.getText().toString();
                loginConfig.setPassword(strPassword);
                loginConfig.setUsername(strAcout);
                RegisterTask registerTask = new RegisterTask(
                        MainActivity.this, loginConfig);
                registerTask.execute();
            }
        });

    }
}
