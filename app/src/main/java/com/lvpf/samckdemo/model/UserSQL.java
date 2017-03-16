package com.lvpf.samckdemo.model;

public class UserSQL {
	// 'add_time' datetime not null default '2001-01-01',
	// 'update_time' datetime not null default '2001-01-01'
	private Integer id;
	private int server_client;// 服务器端ID
	private int from_user_type;//
	private int from_user_id;
	private int notice_type;// 通知类型
	private String notice_content;// 通知对象
	private int other_id;// 
	private String add_time;
	private String update_time;

	public UserSQL() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserSQL(int server_client, int from_uer_type) {
		super();
		this.server_client = server_client;
		this.from_user_type = from_uer_type;
	}

	public UserSQL(Integer id, int server_client, int from_uer_type) {
		super();
		this.id = id;
		this.server_client = server_client;
		this.from_user_type = from_uer_type;
	}

	public UserSQL(int server_client, int from_uer_type, int from_uer_id) {
		super();
		this.server_client = server_client;
		this.from_user_type = from_uer_type;
		this.from_user_id = from_uer_id;
	}

	public UserSQL(int server_client, int from_uer_type, int from_uer_id,
			int notice_type) {
		super();
		this.server_client = server_client;
		this.from_user_type = from_uer_type;
		this.from_user_id = from_uer_id;
		this.notice_type = notice_type;
	}

	public UserSQL(int server_client, int from_uer_type, int from_uer_id,
			int notice_type, String notice_content) {
		super();
		this.server_client = server_client;
		this.from_user_type = from_uer_type;
		this.from_user_id = from_uer_id;
		this.notice_type = notice_type;
		this.notice_content = notice_content;
	}

	public UserSQL(Integer id, int server_client, int from_user_type,
				   int from_user_id, int notice_type, String notice_content,
				   String add_time, String update_time) {
		super();
		this.id = id;
		this.server_client = server_client;
		this.from_user_type = from_user_type;
		this.from_user_id = from_user_id;
		this.notice_type = notice_type;
		this.notice_content = notice_content;
		this.add_time = add_time;
		this.update_time = update_time;
	}

	public int getServer_client() {
		return server_client;
	}

	public void setServer_client(int server_client) {
		this.server_client = server_client;
	}

	public int getNotice_type() {
		return notice_type;
	}

	public void setNotice_type(int notice_type) {
		this.notice_type = notice_type;
	}

	public String getNotice_content() {
		return notice_content;
	}

	public void setNotice_content(String notice_content) {
		this.notice_content = notice_content;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public int getFrom_user_type() {
		return from_user_type;
	}

	public void setFrom_user_type(int from_user_type) {
		this.from_user_type = from_user_type;
	}

	public int getFrom_user_id() {
		return from_user_id;
	}

	public void setFrom_user_id(int from_user_id) {
		this.from_user_id = from_user_id;
	}

	public int getOther_id() {
		return other_id;
	}

	public void setOther_id(int other_id) {
		this.other_id = other_id;
	}

	@Override
	public String toString() {
		return "UserSQL [id=" + id + ", server_client=" + server_client
				+ ", from_uer_type=" + from_user_type + ", from_uer_id="
				+ from_user_id + ", notice_type=" + notice_type
				+ ", notice_content=" + notice_content + ", add_time="
				+ add_time + ", update_time=" + update_time + "]";
	}
}
