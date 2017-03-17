package com.lvpf.samckdemo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.lvpf.samckdemo.model.Constant;
import com.lvpf.samckdemo.utils.DateUtil;
import com.lvpf.samckdemo.model.IMMessage;
import com.lvpf.samckdemo.manager.MessageManager;
import com.lvpf.samckdemo.manager.XmppConnectionManager;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.Calendar;

/**
 * 聊天服务.
 *
 * @author shimiso
 */
public class IMChatService extends Service {
    private Context context;

    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
        initChatManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initChatManager() {
        XMPPConnection conn = XmppConnectionManager.getInstance()
                .getConnection();
        conn.addPacketListener(pListener, new MessageTypeFilter(
                Message.Type.chat));
    }

    PacketListener pListener = new PacketListener() {

        @Override
        public void processPacket(Packet arg0) {
            Message message = (Message) arg0;
            if (message != null && message.getBody() != null
                    && !message.getBody().equals("null")) {
                IMMessage msg = new IMMessage();
                String time = DateUtil.date2Str(Calendar.getInstance(),
                        Constant.MS_FORMART);
                msg.setTime(time);
                msg.setContent(message.getBody());

                if (Message.Type.error == message.getType()) {
                    msg.setType(IMMessage.ERROR);
                } else {
                    msg.setType(IMMessage.SUCCESS);
                }
                String from = message.getFrom().split("/")[0];
                msg.setFromSubJid(from);
                // 历史记录
                IMMessage newMessage = new IMMessage();
                newMessage.setMsgType(0);
                newMessage.setFromSubJid(from);
                newMessage.setContent(message.getBody());
                newMessage.setTime(time);
                MessageManager.getInstance(context).saveIMMessage(newMessage);

                Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
                intent.putExtra(IMMessage.IMMESSAGE_KEY, msg);
                LocalBroadcastManager.getInstance(context).sendBroadcastSync(intent);

            }

        }

    };
}
