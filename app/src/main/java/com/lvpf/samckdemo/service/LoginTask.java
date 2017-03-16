package com.lvpf.samckdemo.service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.lvpf.samckdemo.activity.ChatItemActivity;
import com.lvpf.samckdemo.base.BaseActivity;
import com.lvpf.samckdemo.manager.XmppConnectionManager;
import com.lvpf.samckdemo.model.Constant;
import com.lvpf.samckdemo.model.LoginConfig;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.OfflineMessageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * 登录异步任务.
 * 
 * @author shimiso
 */
public class LoginTask extends AsyncTask<String, Integer, Integer> {
	private Context context;
	private BaseActivity activitySupport;
	private LoginConfig loginConfig;

	public LoginTask(BaseActivity activitySupport, LoginConfig loginConfig) {
		this.activitySupport = activitySupport;
		this.loginConfig = loginConfig;
		this.context = activitySupport.getApplication();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		return login();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(Integer result) {
		switch (result) {
		case Constant.LOGIN_SECCESS: // 登录成功
			Log.d("TAG", "onPostExecute: 登录成功");
			Intent intent = new Intent();
			intent.setClass(context,ChatItemActivity.class);
			activitySupport.saveLoginConfig(loginConfig);// 保存用户配置信息
			activitySupport.startService(); // 初始化各项服务
			activitySupport.startActivity(intent);
			activitySupport.finish();
			break;
		case Constant.LOGIN_ERROR_ACCOUNT_PASS:// 账户或者密码错误
			Log.d("TAG", "onPostExecute: 帐户或者密码错误");
			break;
		case Constant.SERVER_UNAVAILABLE:// 服务器连接失败
			Log.d("TAG", "onPostExecute: 服务器连接失败");
			break;
		case Constant.LOGIN_ERROR:// 未知异常
			Log.d("TAG", "onPostExecute: 未知异常");
			break;
		}
		super.onPostExecute(result);
	}

	// 登录
	private Integer login() {
		String username = loginConfig.getUsername();
		String password = loginConfig.getPassword();
		Log.d("TAG", "======================="+username+"+++"+password+"----"+loginConfig.getXmppHost()+":"+loginConfig.getXmppPort());
		try {
			XMPPConnection connection = XmppConnectionManager.getInstance()
					.getConnection();
			connection.connect();

			connection.login(username, password); // 登录
			GetLiXianinformation(XmppConnectionManager.getInstance().getConnection());
			Presence presence = new Presence(Presence.Type.available);
			connection.sendPacket(presence);
			loginConfig.setUsername(username);
			loginConfig.setPassword(password);
			loginConfig.setOnline(true);
			return Constant.LOGIN_SECCESS;
		} catch (Exception xee) {
			if (xee instanceof XMPPException) {
				XMPPException xe = (XMPPException) xee;
				final XMPPError error = xe.getXMPPError();
				int errorCode = 0;
				if (error != null) {
					errorCode = error.getCode();
				}
				if (errorCode == 401) {
					Log.d("TAG", "++"+error.getMessage()+error.getCondition()+error.getCode()+"==="+xee.getMessage()+"----"+xe.getMessage());
					return Constant.LOGIN_ERROR_ACCOUNT_PASS;
				} else if (errorCode == 403) {
					Log.d("TAG", "++"+error.getMessage()+error.getCondition()+error.getCode()+"==="+xee.getMessage()+"----"+xe.getMessage());
					return Constant.LOGIN_ERROR_ACCOUNT_PASS;
				} else {
					Log.e("TAG", "dddd"+xee.getMessage());
					return Constant.SERVER_UNAVAILABLE;
				}
			} else {
				return Constant.LOGIN_ERROR;
			}
		}
	}
	/**
	 * 获取离线信息
	 */
	private void GetLiXianinformation(XMPPConnection connection){
	    OfflineMessageManager offlineManager = new OfflineMessageManager(connection
	    		);  
        try {  
            Iterator<Message> it = offlineManager
                    .getMessages();  
  
            System.out.println(offlineManager.supportsFlexibleRetrieval());
            System.out.println("离线消息数量: " + offlineManager.getMessageCount());
  
              
            Map<String,ArrayList<Message>> offlineMsgs = new HashMap<String,ArrayList<Message>>();
              
            while (it.hasNext()) {  
                Message message = it.next();
                System.out
                        .println("收到离线消息, Received from 【" + message.getFrom()  
                                + "】 message: " + message.getBody());  
                String fromUser = message.getFrom().split("/")[0];
  
                if(offlineMsgs.containsKey(fromUser))  
                {  
                    offlineMsgs.get(fromUser).add(message);  
                }else{  
                    ArrayList<Message> temp = new ArrayList<Message>();
                    temp.add(message);  
                    offlineMsgs.put(fromUser, temp);  
                }  
            }  
  

            offlineManager.deleteMessages();  
        } catch (Exception e) {
        	System.out.println("===================================================");
            e.printStackTrace();  
        }  
	}
	
	
}
