package com.lvpf.samckdemo.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.lvpf.myapplicationd.R;
import com.lvpf.samckdemo.model.Constant;
import com.lvpf.samckdemo.model.LoginConfig;
import com.lvpf.samckdemo.service.IMChatService;
import com.lvpf.samckdemo.service.IMContactService;
import com.lvpf.samckdemo.service.IMSystemMsgService;
import com.lvpf.samckdemo.service.ReConnectService;

/**
 * 父类
 */
public class BaseActivity extends AppCompatActivity {
	private LMApplication application;
	protected SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		application= LMApplication.getInstance();
		preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void startService() {
		// 好友联系人服务
		Intent server = new Intent(BaseActivity.this, IMContactService.class);
		this.startService(server);

		// 聊天服务
		Intent chatServer = new Intent(BaseActivity.this, IMChatService.class);
		this.startService(chatServer);
		// 自动恢复连接服务
		Intent reConnectService = new Intent(BaseActivity.this,
				ReConnectService.class);
		this.startService(reConnectService);
		// 系统消息连接服务
		Intent imSystemMsgService = new Intent(BaseActivity.this,
				IMSystemMsgService.class);
		this.startService(imSystemMsgService);

	}

	/**
	 *
	 * 销毁服务.
	 *
	 */
	public void stopService() {
		// 好友联系人服务
		Intent server = new Intent(application, IMContactService.class);
		this.stopService(server);
		// 聊天服务
		Intent chatServer = new Intent(application, IMChatService.class);
		this.stopService(chatServer);

		// 自动恢复连接服务
		Intent reConnectService = new Intent(application,
				ReConnectService.class);
		this.stopService(reConnectService);

		// 系统消息连接服务
		Intent imSystemMsgService = new Intent(application,
				IMSystemMsgService.class);
		this.stopService(imSystemMsgService);

	}


	public LoginConfig getLoginConfig() {
		LoginConfig loginConfig = new LoginConfig();
		loginConfig.setXmppHost(preferences.getString(Constant.XMPP_HOST,
				getResources().getString(R.string.xmpp_host)));
		loginConfig.setXmppPort(preferences.getInt(Constant.XMPP_PORT,
				getResources().getInteger(R.integer.xmpp_port)));

		loginConfig.setUsername(preferences.getString(Constant.USERNAME, null));
		loginConfig.setPassword(preferences.getString(Constant.PASSWORD, null));
		loginConfig.setXmppServiceName(preferences.getString(
				Constant.XMPP_SEIVICE_NAME,
				getResources().getString(R.string.xmpp_service_name)));
		loginConfig.setAutoLogin(preferences.getBoolean(Constant.IS_AUTOLOGIN,
				getResources().getBoolean(R.bool.is_autologin)));
		loginConfig.setNovisible(preferences.getBoolean(Constant.IS_NOVISIBLE,
				getResources().getBoolean(R.bool.is_novisible)));
		loginConfig.setRemember(preferences.getBoolean(Constant.IS_REMEMBER,
				getResources().getBoolean(R.bool.is_remember)));
		loginConfig.setFirstStart(preferences.getBoolean(
				Constant.IS_FIRSTSTART, true));


		return loginConfig;
	}
	public void saveLoginConfig(LoginConfig loginConfig) {
		preferences.edit()
				.putString(Constant.XMPP_HOST, loginConfig.getXmppHost())
				.commit();
		preferences.edit()
				.putInt(Constant.XMPP_PORT, loginConfig.getXmppPort()).commit();
		preferences
				.edit()
				.putString(Constant.XMPP_SEIVICE_NAME,
						loginConfig.getXmppServiceName()).commit();
		preferences.edit()
				.putString(Constant.USERNAME, loginConfig.getUsername())
				.commit();
		preferences.edit()
				.putString(Constant.PASSWORD, loginConfig.getPassword())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_AUTOLOGIN, loginConfig.isAutoLogin())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_NOVISIBLE, loginConfig.isNovisible())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_REMEMBER, loginConfig.isRemember())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_ONLINE, loginConfig.isOnline())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_FIRSTSTART, loginConfig.isFirstStart())
				.commit();
	}
}