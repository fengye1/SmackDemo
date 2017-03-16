package com.lvpf.samckdemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.lvpf.samckdemo.base.BaseActivity;
import com.lvpf.samckdemo.manager.MessageManager;
import com.lvpf.samckdemo.manager.NoticeManager;
import com.lvpf.samckdemo.manager.XmppConnectionManager;
import com.lvpf.samckdemo.model.Constant;
import com.lvpf.samckdemo.model.IMMessage;
import com.lvpf.samckdemo.model.Notice;
import com.lvpf.samckdemo.model.UserInfo4XMPP;
import com.lvpf.samckdemo.utils.CommonUtils;
import com.lvpf.samckdemo.utils.DateUtil;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * 聊天对话父类.
 *
 * @author lvpf
 */
public abstract class AChatActivity extends BaseActivity {

    private Chat chat = null;
    private List<IMMessage> message_pool = null;
    protected String to;// 聊天人
    private String TAG = "AChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        to = getIntent().getStringExtra("userId");
        if (to == null)
            return;
        XmppConnectionManager instance = XmppConnectionManager.getInstance();
        XMPPConnection connection = instance.getConnection();
        chat = connection
                .getChatManager().createChat(to, null);


        message_pool = new ArrayList<IMMessage>();
        message_pool = MessageManager.getInstance(getApplication())
                .getMessageListByFrom(to, 1, 20);

        if (null != message_pool && message_pool.size() > 0)
            Collections.sort(message_pool);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.NEW_MESSAGE_ACTION);
//        注册
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
//        解除注册
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
//接收消息
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
                final IMMessage message = intent
                        .getParcelableExtra(IMMessage.IMMESSAGE_KEY);
                Log.d(TAG, "onReceive: " + message.getContent());
                AChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message_pool.add(message);
                        receiveNewMessage(message);
                        refreshMessage(message_pool);
                    }
                });

            }
        }

    };

    protected abstract void receiveNewMessage(IMMessage message);

    protected abstract void refreshMessage(List<IMMessage> messages);

    protected List<IMMessage> getMessages() {
        return message_pool;
    }

    /**
     * 发送消息
     * @param messageContent
     * @throws Exception
     */
    protected void sendMessage(String messageContent) throws Exception {
        sendMessage(messageContent, CommonUtils.NORMAL,null);
    }

    /**
     *发送消息(带扩展(url))
     * @param messageContent 消息内容
     * @param subject        设置标签
     * @param userInfo4XMPP  扩展内容
     * @throws Exception
     */
    protected void sendMessage(String messageContent, String subject,UserInfo4XMPP userInfo4XMPP) throws Exception {
        String time = DateUtil.date2Str(Calendar.getInstance(),
                Constant.MS_FORMART);
        Message message = new Message();
        message.setProperty(IMMessage.KEY_TIME, time);
        message.setBody(messageContent);
        IMMessage newMessage = new IMMessage();
//        如果是url则设置此消息
        if (CommonUtils.URL_SIGN.equals(subject)) {
            message.addExtension(userInfo4XMPP);
            newMessage.setUserInfo4XMPP(userInfo4XMPP);
        }
        message.setSubject(subject);
        Log.d(TAG, "sendMessage: " + message.getBody());
        chat.sendMessage(message);

        newMessage.setMsgType(1);
        newMessage.setFromSubJid(chat.getParticipant());
        newMessage.setContent(messageContent);
        newMessage.setTime(time);
        newMessage.setSubject(subject);
        message_pool.add(newMessage);
        MessageManager.getInstance(getApplication()).saveIMMessage(newMessage);
        // 刷新视图
        refreshMessage(message_pool);

    }

    /**
     * 判断好友是否在线
     *
     * @return
     */
    public boolean friendOnLine() {
        Roster roster = XmppConnectionManager.getInstance().getConnection().getRoster();
        Presence presence = roster.getPresence(to);
        if (presence.getType().equals(Presence.Type.available)) {//在线
            System.out.println("-------离线-----" + to);
            return true;
        }
        return false;
    }
}
