package com.lvpf.samckdemo.manager;

import com.lvpf.samckdemo.model.User;
import com.lvpf.samckdemo.utils.StringUtils;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

public class ContacterManager {

	/**
	 * 保存着所有的联系人信息
	 */
	public static Map<String, User> contacters = null;

	public static void init(Connection connection) {
		contacters = new HashMap<String, User>();
		for (RosterEntry entry : connection.getRoster().getEntries()) {
			contacters.put(entry.getUser(),
					transEntryToUser(entry, connection.getRoster()));
		}
	}

	public static void destroy() {
		contacters = null;
	}

	/**
	 * 获得所有的联系人列表
	 * 
	 * @return
	 */
	public static List<User> getContacterList() {
		if (contacters == null)
			throw new RuntimeException("contacters is null");

		List<User> userList = new ArrayList<User>();

		for (String key : contacters.keySet())
			userList.add(contacters.get(key));

		return userList;
	}

	/**
	 * 获得所有未分组的联系人列表
	 * 
	 * @return
	 */
	public static List<User> getNoGroupUserList(Roster roster) {
		List<User> userList = new ArrayList<User>();

		// 服务器的用户信息改变后，不会通知到unfiledEntries
		for (RosterEntry entry : roster.getUnfiledEntries()) {
			userList.add(contacters.get(entry.getUser()).clone());
		}

		return userList;
	}

	/**
	 * 根据RosterEntry创建一个User
	 * 
	 * @param entry
	 * @return
	 */
	public static User transEntryToUser(RosterEntry entry, Roster roster) {
		User user = new User();
		if (entry.getName() == null) {
			user.setName(StringUtils.getUserNameByJid(entry.getUser()));
		} else {
			user.setName(entry.getName());
		}
		user.setJID(entry.getUser());
		System.out.println(entry.getUser());
		Presence presence = roster.getPresence(entry.getUser());
		user.setFrom(presence.getFrom());
		user.setStatus(presence.getStatus());
		user.setSize(entry.getGroups().size());
		user.setAvailable(presence.isAvailable());
		user.setType(entry.getType());
		return user;
	}

	/**
	 * 修改这个好友的昵称
	 * 
	 * @param user
	 * @param nickname
	 */
	public static void setNickname(User user, String nickname,
			XMPPConnection connection) {
		RosterEntry entry = connection.getRoster().getEntry(user.getJID());

		entry.setName(nickname);
	}

	/**
	 * 根据用户jid得到用户
	 */
	public static User getByUserJid(String userJId, XMPPConnection connection) {
		Roster roster = connection.getRoster();
		if (roster == null) {
			return null;
		}
		RosterEntry entry = roster.getEntry(userJId);
		if (null == entry) {
			return null;
		}
		User user = new User();
		if (entry.getName() == null) {
			user.setName(StringUtils.getUserNameByJid(entry.getUser()));
		} else {
			user.setName(entry.getName());
		}
		user.setJID(entry.getUser());
		System.out.println(entry.getUser());
		Presence presence = roster.getPresence(entry.getUser());
		user.setFrom(presence.getFrom());
		user.setStatus(presence.getStatus());
		user.setSize(entry.getGroups().size());
		user.setAvailable(presence.isAvailable());
		user.setType(entry.getType());
		return user;

	}

}
