package com.lvpf.samckdemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.lvpf.myapplicationd.R;
import com.lvpf.samckdemo.activity.MainActivity;
import com.lvpf.samckdemo.model.Constant;
import com.lvpf.samckdemo.manager.ContacterManager;
import com.lvpf.samckdemo.utils.DateUtil;
import com.lvpf.samckdemo.model.Notice;
import com.lvpf.samckdemo.manager.NoticeManager;
import com.lvpf.samckdemo.utils.StringUtils;
import com.lvpf.samckdemo.model.User;
import com.lvpf.samckdemo.manager.XmppConnectionManager;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.Calendar;
import java.util.Collection;

/**
 * 联系人服务.
 *
 * @author shimiso
 */
public class IMContactService extends Service {

    private Roster roster = null;
    private Context context;
    private String TAG = "IMContactService";
    /* 声明对象变量 */
    private NotificationManager myNotiManager;

    @Override
    public void onCreate() {
        context = this;

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        /* 初始化对象 */
        myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        initRoster();
        PacketFilter filter = new AndFilter(new PacketTypeFilter(Presence.class));
        XmppConnectionManager.getInstance().getConnection()
                .addPacketListener(subscriptionPacketListener, filter);
    }

    /**
     * 初始化花名册 服务重启时，更新花名册
     */
    private void initRoster() {
        roster = XmppConnectionManager.getInstance().getConnection()
                .getRoster();
        roster.removeRosterListener(rosterListener);
        roster.addRosterListener(rosterListener);
        ContacterManager.init(XmppConnectionManager.getInstance()
                .getConnection());
    }




    private PacketListener subscriptionPacketListener = new PacketListener() {

        @Override
        public void processPacket(Packet packet) {
            String user = getSharedPreferences(Constant.LOGIN_SET, 0)
                    .getString(Constant.USERNAME, null);
            if (packet.getFrom().contains(user))
                return;
            // 如果是自动接收所有请求，则回复一个添加信息
            if (Roster.getDefaultSubscriptionMode().equals(
                    SubscriptionMode.accept_all)) {
                Presence subscription = new Presence(Presence.Type.subscribe);
                subscription.setTo(packet.getFrom());
                XmppConnectionManager.getInstance().getConnection()
                        .sendPacket(subscription);
                Log.d(TAG, "----------------------------++++");
            } else {
                if (packet instanceof Presence) {
                    Presence presence = (Presence) packet;
                    // Presence还有很多方法，可查看API
                    String from = presence.getFrom();// 发送方
                    String to = presence.getTo();// 接收方
                    if (presence.getType().equals(Presence.Type.subscribe)) {// 好友申请
                        Log.d(TAG, "processPacket: " + "对方请求好友申请");
                        agareeFriend(presence);
                    } else if (presence.getType().equals(
                            Presence.Type.subscribed)) {// 同意添加好友
                        Log.d(TAG, "processPacket: " + "对方同意好友申请");

                    } else if (presence.getType().equals(
                            Presence.Type.unsubscribe)) {// 拒绝添加好友
                        Log.d(TAG, "processPacket: " + "对方拒绝好友申请");
                    } else if (presence.getType().equals(
                            Presence.Type.unsubscribed)) {// 这个我没用到
                    } else if (presence.getType().equals(
                            Presence.Type.unavailable)) {// 好友下线
                        Log.d(TAG, "processPacket: " + "好友下线");
                    } else if (presence.getType().equals(
                            Presence.Type.available)) {// 好友上线
                        Log.d(TAG, "processPacket: " + "好友上线");

                    }
                }


            }
        }
    };

    private void agareeFriend( Presence packet){
        {
            NoticeManager noticeManager = NoticeManager
                    .getInstance(context);
            Notice notice = new Notice();
            notice.setTitle("好友请求");
            notice.setNoticeType(Notice.ADD_FRIEND);
            String name = StringUtils.getUserNameByJid(packet.getFrom());
            notice.setContent(name
                    + "申请加您为好友");
            notice.setFrom(packet.getFrom());
            notice.setTo(packet.getTo());
            notice.setNoticeTime(DateUtil.date2Str(Calendar.getInstance(),
                    Constant.MS_FORMART));
            notice.setStatus(Notice.UNREAD);
            noticeManager.saveNotice(notice);

            System.out.println("添加好友成功！！");
            // 接受请求
            sendSubscribe(Presence.Type.subscribed, packet.getFrom());
            try {
                roster.createEntry(name.trim() + "@debain", name, new String[]{"Friends"});
            } catch (XMPPException e) {
                e.printStackTrace();
            }
            noticeManager.updateAddFriendStatus(
                    notice.getId(),
                    Notice.READ,
                    "已经同意"
                            + StringUtils.getUserNameByJid(packet.getFrom()) + "的好友申请");
            simpleNotify(notice);
        }
    }

    /**
     * 回复一个presence信息给用户
     *
     * @param type
     * @param to
     */
    protected void sendSubscribe(Presence.Type type, String to) {
        Presence presence = new Presence(type);
        presence.setTo(to);
        XmppConnectionManager.getInstance().getConnection()
                .sendPacket(presence);
    }

    /**
     * 通知
     *
     * @param notice
     */
    private void simpleNotify(Notice notice) {
        //为了版本兼容  选择V7包下的NotificationCompat进行构造
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //Ticker是状态栏显示的提示
        builder.setTicker("简单Notification");
        //第一行内容  通常作为通知栏标题
        builder.setContentTitle(notice.getTitle());
        //第二行内容 通常是通知正文
        builder.setContentText(notice.getContent());
//        //第三行内容 通常是内容摘要什么的 在低版本机器上不一定显示
//        builder.setSubText("这里显示的是通知第三行内容！");
        //ContentInfo 在通知的右侧 时间的下面 用来展示一些其他信息
        //builder.setContentInfo("2");
        //number设计用来显示同种通知的数量和ContentInfo的位置一样，如果设置了ContentInfo则number会被隐藏
        builder.setNumber(2);
        //可以点击通知栏的删除按钮删除
        builder.setAutoCancel(true);
        //系统状态栏显示的小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //下拉显示的大图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 1, intent, 0);
        //点击跳转的intent
        builder.setContentIntent(pIntent);
        //通知默认的声音 震动 呼吸灯
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Notification notification = builder.build();
        myNotiManager.notify(1, notification);
    }

    @Override
    public void onDestroy() {
        // 释放资源
        XmppConnectionManager.getInstance().getConnection()
                .removePacketListener(subscriptionPacketListener);
        ContacterManager.destroy();
        super.onDestroy();
    }

    private RosterListener rosterListener = new RosterListener() {

        @Override
        public void presenceChanged(Presence presence) {
            Intent intent = new Intent();
            intent.setAction(Constant.ROSTER_PRESENCE_CHANGED);
            String subscriber = presence.getFrom().substring(0,
                    presence.getFrom().indexOf("/"));
            RosterEntry entry = roster.getEntry(subscriber);
            if (ContacterManager.contacters.containsKey(subscriber)) {
                // 将状态改变之前的user广播出去
                intent.putExtra(User.userKey,
                        ContacterManager.contacters.get(subscriber));
                ContacterManager.contacters.remove(subscriber);
                ContacterManager.contacters.put(subscriber,
                        ContacterManager.transEntryToUser(entry, roster));
            }
            sendBroadcast(intent);
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            for (String address : addresses) {
                Intent intent = new Intent();
                intent.setAction(Constant.ROSTER_UPDATED);
                // 获得状态改变的entry
                RosterEntry userEntry = roster.getEntry(address);
                User user = ContacterManager
                        .transEntryToUser(userEntry, roster);
                if (ContacterManager.contacters.get(address) != null) {
                    // 这里发布的是更新前的user
                    intent.putExtra(User.userKey,
                            ContacterManager.contacters.get(address));
                    // 将发生改变的用户更新到userManager
                    ContacterManager.contacters.remove(address);
                    ContacterManager.contacters.put(address, user);
                }
                sendBroadcast(intent);
                // 用户更新，getEntries会更新
                // roster.getUnfiledEntries中的entry不会更新
            }
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            for (String address : addresses) {
                Intent intent = new Intent();
                intent.setAction(Constant.ROSTER_DELETED);
                User user = null;
                if (ContacterManager.contacters.containsKey(address)) {
                    user = ContacterManager.contacters.get(address);
                    ContacterManager.contacters.remove(address);
                }
                intent.putExtra(User.userKey, user);
                sendBroadcast(intent);
            }
        }

        @Override
        public void entriesAdded(Collection<String> addresses) {
            for (String address : addresses) {
                Intent intent = new Intent();
                intent.setAction(Constant.ROSTER_ADDED);
                RosterEntry userEntry = roster.getEntry(address);
                User user = ContacterManager
                        .transEntryToUser(userEntry, roster);
                ContacterManager.contacters.put(address, user);
                intent.putExtra(User.userKey, user);
                sendBroadcast(intent);
            }
        }
    };

}
