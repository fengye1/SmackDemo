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

import com.lvpf.myapplicationd.R;
import com.lvpf.samckdemo.activity.MainActivity;
import com.lvpf.samckdemo.model.Constant;
import com.lvpf.samckdemo.utils.DateUtil;
import com.lvpf.samckdemo.model.Notice;
import com.lvpf.samckdemo.manager.NoticeManager;
import com.lvpf.samckdemo.manager.XmppConnectionManager;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;

import java.util.Calendar;

/**
 * 系统消息服务.
 *
 * @author shimiso
 */
public class IMSystemMsgService extends Service {
    private Context context;
    /* 声明对象变量 */
    private NotificationManager myNotiManager;


    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
        initSysTemMsgManager();
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
        XmppConnectionManager.getInstance().getConnection()
                .removePacketListener(pListener);
        super.onDestroy();
    }

    private void initSysTemMsgManager() {
            /* 初始化对象 */
        myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        XMPPConnection con = XmppConnectionManager.getInstance()
                .getConnection();
        con.addPacketListener(pListener, new MessageTypeFilter(
                Type.normal));
    }

    // 来消息监听
    PacketListener pListener = new PacketListener() {

        @Override
        public void processPacket(Packet packetz) {
            Message message = (Message) packetz;

            if (message.getType() == Type.normal) {

                NoticeManager noticeManager = NoticeManager
                        .getInstance(context);

                Notice notice = new Notice();
                notice.setTitle("系统消息");
                notice.setNoticeType(Notice.SYS_MSG);
                notice.setFrom(packetz.getFrom());
                notice.setContent(message.getBody());
                notice.setNoticeTime(DateUtil.date2Str(Calendar.getInstance(),
                        Constant.MS_FORMART));
                notice.setFrom(packetz.getFrom());
                notice.setTo(packetz.getTo());
                notice.setStatus(Notice.UNREAD);
                long noticeId = noticeManager.saveNotice(notice);

                simpleNotify(notice);
            }
        }
    };

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


}
