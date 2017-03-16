package com.lvpf.samckdemo.manager;

import android.util.Log;

import com.lvpf.samckdemo.model.LoginConfig;
import com.lvpf.samckdemo.model.User;
import com.lvpf.samckdemo.utils.LogUtils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * XMPP服务器连接工具类.
 * 
 * @author shimiso
 */
public class XmppConnectionManager {



	private XMPPConnection connection;
	public static final String DOMAIN = "@debian";
	private static ConnectionConfiguration connectionConfig;
	private static XmppConnectionManager xmppConnectionManager;
	private String tag = "XmppConnectionManager";
	private final String CONFERENCE = "@conference.";
		
	private XmppConnectionManager() {

	}

	private MultiUserChat muc;

	public MultiUserChat getMuc() {
		return muc;
	}

	public void setMuc(MultiUserChat muc) {
		this.muc = muc;
	}
	
	public static XmppConnectionManager getInstance() {
		if (xmppConnectionManager == null) {
			xmppConnectionManager = new XmppConnectionManager();
		}
		return xmppConnectionManager;
	}
	
	// init
	public XMPPConnection init(LoginConfig loginConfig) {
		ProviderManager pm = ProviderManager.getInstance();
		configure(pm);
		connectionConfig = new ConnectionConfiguration(
				loginConfig.getXmppHost(), loginConfig.getXmppPort());
		connectionConfig.setSASLAuthenticationEnabled(true);
		// 允许自动连接
		connectionConfig.setReconnectionAllowed(true);
		Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
		connection = new XMPPConnection(connectionConfig);
		return connection;
	}

	/**
	 * 
	 * 返回一个有效的xmpp连接,如果无效则返回空.
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-4 下午6:54:31
	 */
	public XMPPConnection getConnection() {
		if (connection == null) {
			throw new RuntimeException("请先初始化XMPPConnection连接");
		}
		return connection;
	}
	/**
	 * 
	 * 销毁xmpp连接.
	 * 
	 * @author shimiso
	 * @update 2012-7-4 下午6:55:03
	 */
	public void disconnect() {
		if (connection != null) {
			connection.disconnect();
			connection=null;
		}
	}

	/**
	 * 获取所有分组
	 *
	 * @param roster
	 * @return
	 */
	public List<RosterGroup> getGroups(Roster roster) {
		List<RosterGroup> list = new ArrayList<RosterGroup>();
		list.addAll(roster.getGroups());
		return list;
	}

	/**
	 * Android读不到/META-INF下的配置文件，需要手工配置。
	 *
	 * @param pm
	 */
	private void configure(ProviderManager pm) {
		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());
		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());
		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());
		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());
		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());
		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());
		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());
		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());
		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());
		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());
		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
			e.printStackTrace();
		}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());
		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());
		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());
		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());
		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}

	/**
	 * 添加分组
	 * 
	 * @param roster
	 * @param groupName
	 * @return
	 */
	public boolean addGroup(Roster roster, String groupName) {
		try {
			roster.createGroup(groupName);
			return true;
		} catch (Exception e) {
			LogUtils.e(tag, Log.getStackTraceString(e));
		}
		return false;
	}

	/**
	 * 添加到分组
	 * 
	 * @param roster
	 * @param userName
	 * @param groupName
	 */
	public void addUserToGroup(Roster roster, String userName, String groupName) {
		RosterGroup group = roster.getGroup(groupName);
		if (null == group) {
			group = roster.createGroup(groupName);
		}
		RosterEntry entry = roster.getEntry(userName);
		try {
			group.addEntry(entry);
		} catch (XMPPException e) {
			LogUtils.e(tag, Log.getStackTraceString(e));
		}
	}

	/**
	 * 获取所有成员
	 * 
	 * @param roster
	 * @return
	 */
	public List<RosterEntry> getAllEntrys(Roster roster) {
		List<RosterEntry> list = new ArrayList<RosterEntry>();
		list.addAll(roster.getEntries());
		return list;
	}

	/**
	 * 获取某一个分组的成员
	 * 
	 * @param roster
	 * @param groupName
	 * @return
	 */
	public List<RosterEntry> getEntrysByGroup(Roster roster, String groupName) {
		List<RosterEntry> list = new ArrayList<RosterEntry>();
		RosterGroup group = roster.getGroup(groupName);
		list.addAll(group.getEntries());
		return list;
	}

	/**
	 * 获取用户VCard信息
	 * 
	 * @param user
	 * @return
	 */
	public VCard getVCard(String user) {
		VCard vCard = new VCard();
		try {
			vCard.load(connection, user);
		} catch (XMPPException e) {
			LogUtils.e(tag, Log.getStackTraceString(e));
			return null;
		}
		return vCard;
	}

	/**
	 * 添加好友
	 * 
	 * @param roster
	 * @param userName
	 * @param name
	 * @param groupName
	 *            是否有分组
	 * @return
	 */
	public boolean addUser(Roster roster, String userName, String name,
						   String groupName) {
		try {
			roster.createEntry(userName, name, null == groupName ? null
					: new String[] { groupName });
			return true;
		} catch (XMPPException e) {
			LogUtils.e(tag, Log.getStackTraceString(e));
		}
		return false;
	}

	/**
	 * 删除好友
	 * 
	 * @param roster
	 * @param userName
	 * @return
	 */
	public boolean removeUser(Roster roster, String userName) {
		try {
			if (userName.contains("@"))
				userName = userName.split("@")[0];
			RosterEntry entry = roster.getEntry(userName);
			if (null != entry)
				roster.removeEntry(entry);
			return true;
		} catch (XMPPException e) {
			LogUtils.e(tag, Log.getStackTraceString(e));
		}
		return false;
	}

	/**
	 * 查找用户
	 *
	 * @param serverDomain
	 * @param userName
	 * @return
	 */
	public List<User> searchUsers(String serverDomain, String userName) {
		List<User> list = new ArrayList<User>();
		UserSearchManager userSearchManager = new UserSearchManager(connection);
		try {
			Form searchForm = userSearchManager.getSearchForm("search."
					+ serverDomain);
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("Name", true);
			answerForm.setAnswer("search", userName);
			ReportedData data = userSearchManager.getSearchResults(answerForm,
					"search." + serverDomain);
			Iterator<Row> rows = data.getRows();
			while (rows.hasNext()) {
				User user = new User();
				Row row = rows.next();
				user.setUserName(row.getValues("Username").next().toString());
				user.setName(row.getValues("Name").next().toString());
				LogUtils.i(tag, user.toString());
				list.add(user);
			}
		} catch (XMPPException e) {
			LogUtils.e(tag, Log.getStackTraceString(e));
		}
		return list;
	}

	/**
	 * 获取离线消息
	 * 
	 * @return
	 */
	public List<Message> getOffLineMessages() {
		List<Message> msgs = new ArrayList<Message>();
		OfflineMessageManager offLineMessageManager = new OfflineMessageManager(
				connection);
		try {
			Iterator<Message> it = offLineMessageManager.getMessages();
			while (it.hasNext()) {
				Message msg = it.next();
				LogUtils.i(tag, msg.toXML());
				msgs.add(msg);
			}
			offLineMessageManager.deleteMessages();
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return msgs;
	}

    /**
	 * 用户是否支持聊天室
	 * @param user
	 * @return
	 */
	public boolean isUserSupportMUC(String user){
		return MultiUserChat.isServiceEnabled(connection, user);
	}
}
