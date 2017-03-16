package com.lvpf.samckdemo.service;


import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.lvpf.samckdemo.base.BaseActivity;
import com.lvpf.samckdemo.manager.XmppConnectionManager;
import com.lvpf.samckdemo.model.Constant;
import com.lvpf.samckdemo.model.LoginConfig;

//注册
public class RegisterTask extends AsyncTask<String, Integer, Integer> {

	private ProgressDialog pd;
	private Context context;
	private BaseActivity activitySupport;
	private LoginConfig loginConfig;

	public RegisterTask(BaseActivity activitySupport, LoginConfig loginConfig) {
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
		return regist(loginConfig.getUsername(), loginConfig.getPassword());
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(Integer result) {
		Log.d("result", "==" + result);
		switch (result) {
		case Constant.LOGIN_SECCESS: // 注册成功
		{
			System.out.println("-----------------------------------");
			Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();

			break;
		}
		case Constant.LOGIN_ERROR_ACCOUNT_PASS:// 账户或者密码错误
		{
			System.out.println("--------------账户密码错误---------------------");
			Toast.makeText(context, "账户密码错误", Toast.LENGTH_SHORT).show();
		}
		case Constant.SERVER_UNAVAILABLE:// 服务器连接失败
		{
			System.out.println("--------------服务器连接失败---------------------");
			Toast.makeText(context, "服务器连接失败", Toast.LENGTH_SHORT).show();
			break;
		}
		case Constant.LOGIN_ERROR:// 未知异常
		{
			System.out.println("-------------- 未知异常--------------------");
			Toast.makeText(context, "未知异常", Toast.LENGTH_SHORT).show();

			break;
		}
		case Constant.STATUS_REGIST_ERROR_USER_EXIST:
			 Toast.makeText(context, "注册失败，用户已存在", Toast.LENGTH_SHORT).show();
			break;
		}
		super.onPostExecute(result);
	}

	/**
	 * 注册
	 * 
	 * @param account
	 *            注册帐号
	 * @param password
	 *            注册密码
	 * @return 1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败
	 */
	public int regist(String account, String password) {
		XMPPConnection connection = XmppConnectionManager.getInstance()
				.getConnection();
		try {
			connection.connect();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("REGISTER", e.getMessage());
		}
		if (connection == null)
			return Constant.SERVER_UNAVAILABLE;
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(connection.getServiceName());
		reg.setUsername(account);// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
		reg.setPassword(password);
		reg.addAttribute("android", "geolo_createUser_android");// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
		PacketFilter filter = new AndFilter(new PacketIDFilter(
				reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = connection.createPacketCollector(filter);
		connection.sendPacket(reg);
		System.out.println("-------------" + reg.toString());
		IQ result = (IQ) collector.nextResult(SmackConfiguration
				.getPacketReplyTimeout());
		// Stop queuing results
		collector.cancel();// 停止请求results（是否成功的结果）
		if (result == null) {
			Log.e("RegistActivity", "No response from server.");
			return Constant.SERVER_UNAVAILABLE;// 服务器连接失败
		} else if (result.getType() == IQ.Type.RESULT) {
			return Constant.LOGIN_SECCESS;// 注册成功
		} else {
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				Log.e("RegistActivity", "IQ.Type.ERROR: "
						+ result.getError().toString());
				return Constant.LOGIN_ERROR;//
			} else {
				return Constant.STATUS_REGIST_ERROR_USER_EXIST;// 注册失败
			}
		}
	}


}
