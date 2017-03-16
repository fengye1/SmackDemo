package com.lvpf.samckdemo.model;

public class Constant {
	/**
	 * 所有的action的监听的必须要以"ACTION_"开头
	 * 
	 */
	/**
	 * 花名册有删除的ACTION和KEY
	 */
	public static final String ROSTER_DELETED = "roster.deleted";

	/**
	 * 花名册有更新的ACTION和KEY
	 */
	public static final String ROSTER_UPDATED = "roster.updated";

	/**
	 * 花名册有增加的ACTION和KEY
	 */
	public static final String ROSTER_ADDED = "roster.added";
	public static final String ROSTER_ADDED_KEY = "roster.added.key";

	/**
	 * 花名册中成员状态有改变的ACTION和KEY
	 */
	public static final String ROSTER_PRESENCE_CHANGED = "roster.presence.changed";
	public static final String ROSTER_PRESENCE_CHANGED_KEY = "roster.presence.changed.key";

	/**
	 * 收到好友邀请请求
	 */
	public static final String NEW_MESSAGE_ACTION = "roster.newmessage";


	/**
	 * 服务器的配置
	 */
	public static final String LOGIN_SET = "eim_login_set";// 登录设置
	public static final String USERNAME = "username";// 账户
	public static final String PASSWORD = "password";// 密码
	public static final String XMPP_HOST = "xmpp_host";// 地址
	public static final String XMPP_PORT = "xmpp_port";// 端口
	public static final String XMPP_SEIVICE_NAME = "xmpp_service_name";// 服务名
	public static final String IS_AUTOLOGIN = "isAutoLogin";// 是否自动登录
	public static final String IS_NOVISIBLE = "isNovisible";// 是否隐身
	public static final String IS_REMEMBER = "isRemember";// 是否记住账户密码
	public static final String IS_FIRSTSTART = "isFirstStart";// 是否首次启动
	public static final String DB_NAME = "db_name";//

	/**
	 * 登录提示
	 */
	public static final int LOGIN_SECCESS = 0;// 成功
	public static final int LOGIN_ERROR_ACCOUNT_PASS = 3;// 账号或者密码错误
	public static final int SERVER_UNAVAILABLE = 4;// 无法连接到服务器
	public static final int LOGIN_ERROR = 5;// 连接失败
	public static final int STATUS_REGIST_ERROR_USER_EXIST = 6; // 注册失败,用户已存在


	/**
	 * 重连接状态acttion
	 */
	public static final String ACTION_RECONNECT_STATE = "action_reconnect_state";
	public static final String RECONNECT_STATE = "reconnect_state";
	public static final boolean RECONNECT_STATE_SUCCESS = true;
	public static final boolean RECONNECT_STATE_FAIL = false;

	/**
	 * 精确到毫秒
	 */
	public static final String MS_FORMART = "yyyy-MM-dd HH:mm:ss";


    public static final String IS_ONLINE = "is_online";
    public static final int notice_type_chat_ask = 1;// 咨询
}
