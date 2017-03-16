package com.lvpf.samckdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jivesoftware.smack.packet.RosterPacket;

/**
 * intent����Я���Parcel��ݣ���Ҫʵ��������� . 1��describeContents()����0�Ϳ���.
 * 2������Ҫ�����д��Parcel�У���ܵ�����������������. 3����д�ⲿ�෴���л�����ʱ���õķ���.
 * 
 * @author wangdan
 * 
 */
public class User implements Parcelable {

	/**
	 * ��user������intent��ʱ��key
	 */
	public static final String userKey = "lovesong_user";

	private String name;
	private String userName;



	private String JID;
	private static RosterPacket.ItemType type;
	private String status;
	private String from;
	private String groupName;
	/**
	 * �û�״̬��Ӧ��ͼƬ
	 */
	private int imgId;
	/**
	 * group��size
	 */
	private int size;
	private boolean available;

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJID() {
		return JID;
	}

	public void setJID(String jID) {
		JID = jID;
	}

	public RosterPacket.ItemType getType() {
		return type;
	}

	@SuppressWarnings("static-access")
	public void setType(RosterPacket.ItemType type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(JID);
		dest.writeString(name);
		dest.writeString(from);
		dest.writeString(status);
		dest.writeString(userName);
		dest.writeInt(available ? 1 : 0);
	}

	public static final Creator<User> CREATOR = new Creator<User>() {

		@Override
		public User createFromParcel(Parcel source) {
			User u = new User();
			u.JID = source.readString();
			u.name = source.readString();
			u.from = source.readString();
			u.status = source.readString();
			u.status = source.readString();
			u.available = source.readInt() == 1 ? true : false;

			return u;
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}

	};

	public User clone() {
		User user = new User();
		user.setAvailable(User.this.available);
		user.setFrom(User.this.from);
		user.setGroupName(User.this.groupName);
		user.setImgId(User.this.imgId);
		user.setJID(User.this.JID);
		user.setName(User.this.name);
		user.setSize(User.this.size);
		user.setStatus(User.this.status);
		user.setUserName(User.this.userName);
		return user;
	}

}
