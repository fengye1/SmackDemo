package com.lvpf.samckdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.lvpf.myapplicationd.R;
import com.lvpf.samckdemo.adapter.MyFriendExpadableAdapter;
import com.lvpf.samckdemo.base.BaseActivity;
import com.lvpf.samckdemo.manager.XmppConnectionManager;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好友列表类
 *@author lvpf
 */
public class ChatItemActivity extends BaseActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private Toolbar toolbar;
    private ExpandableListView exList;
    private BottomSheetDialog mBottomSheetDialog;


    private List<RosterGroup> groups;
    private Map<RosterGroup, List<RosterEntry>> childs;
    private MyFriendExpadableAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_item);
        initView();
        initFriends();
    }

    //初始化
    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //添加溢出菜单
        toolbar.inflateMenu(R.menu.setting_menu);
        // 添加菜单点击事件
        toolbar.setOnMenuItemClickListener(this);
        exList = (ExpandableListView) findViewById(R.id.exList);
        groups = new ArrayList<RosterGroup>();
        childs = new HashMap<RosterGroup, List<RosterEntry>>();
        adapter = new MyFriendExpadableAdapter(this, groups, childs);
        exList.setAdapter(adapter);
        exList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                RosterEntry child = (RosterEntry) adapter.getChild(groupPosition, childPosition);
                Intent intent = new Intent(ChatItemActivity.this,ChatActivity.class);
                intent.putExtra("userId", child.getUser());
                startActivity(intent);
                return false;
            }
        });

    }


    /**
     * 退出应用
     */
    public void exitApp() {
        stopService();
        XmppConnectionManager.getInstance().disconnect();
        finish();
        System.exit(0);
    }

    //     好友列表
    private void initFriends() {
        Roster roster = XmppConnectionManager.getInstance().getConnection().getRoster();
        groups.addAll(roster.getGroups());
        for (RosterGroup group : groups) {
            List<RosterEntry> list = new ArrayList<RosterEntry>();
            list.addAll(group.getEntries());
            childs.put(group, list);
        }
        adapter.notifyDataSetChanged();


    }

    //右侧菜单
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_status:
                showShareDialog();
                break;
//            添加好友
            case R.id.item_searchfrend:
                Intent intent=new Intent(ChatItemActivity.this,AddFriendActivity.class);
                startActivity(intent);
                break;
            case R.id.item_room:

                break;
//                    退出登录
            case R.id.item_exit:
                exitApp();
                break;
        }
        return false;
    }

    //用户注销
    private void cancel() {
        try {
            XmppConnectionManager.getInstance().getConnection().getAccountManager().deleteAccount();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    /**
     * share toobar Dialog
     */
    private void showShareDialog() {
        if (mBottomSheetDialog == null) {
            mBottomSheetDialog = new BottomSheetDialog(this);
            View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_share_dialog, null);
            initDalogView(view);
            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.setCanceledOnTouchOutside(true);
            // 解决下滑隐藏dialog 后，再次调用show 方法显示时，不能弹出Dialog
            View view1 = mBottomSheetDialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet);
            final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view1);
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        Log.i("BottomSheet", "onStateChanged");
                        mBottomSheetDialog.dismiss();
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
            mBottomSheetDialog.show();
        } else {
            mBottomSheetDialog.show();
        }

    }
//初始底部view
    private void initDalogView(View view) {
        TextView setting_avalibe = (TextView) view.findViewById(R.id.setting_avalibe);
        TextView setting_busy = (TextView) view.findViewById(R.id.setting_busy);
        TextView setting_Qme = (TextView) view.findViewById(R.id.setting_Qme);
        TextView setting_leave = (TextView) view.findViewById(R.id.setting_leave);
        TextView setting_hiding = (TextView) view.findViewById(R.id.setting_hiding);
        TextView setting_offline = (TextView) view.findViewById(R.id.setting_offline);
        setting_avalibe.setOnClickListener(this);
        setting_busy.setOnClickListener(this);
        setting_Qme.setOnClickListener(this);
        setting_leave.setOnClickListener(this);
        setting_hiding.setOnClickListener(this);
        setting_offline.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_avalibe:
                setPresence(0);
                break;
            case R.id.setting_Qme:
                setPresence(1);
                break;
            case R.id.setting_busy:
                setPresence(2);
                break;
            case R.id.setting_leave:
                setPresence(3);
                break;
            case R.id.setting_hiding:
                setPresence(4);
                break;
            case R.id.setting_offline:
                setPresence(5);
                break;
        }


    }

    /**
     * 更改用户状态
     */
    public void setPresence(int code) {
        Connection connection = XmppConnectionManager.getInstance().getConnection();
        if (connection == null)
            return;
        Presence presence;
        switch (code) {
            case 0:
                presence = new Presence(Presence.Type.available);
                connection.sendPacket(presence);
                Log.v("state", "设置在线");
                mBottomSheetDialog.dismiss();
                break;
            case 1:
                presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.chat);
                connection.sendPacket(presence);
                Log.v("state", "设置Q我吧");
                System.out.println(presence.toXML());
                mBottomSheetDialog.dismiss();
                break;
            case 2:
                presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.dnd);
                connection.sendPacket(presence);
                Log.v("state", "设置忙碌");
                System.out.println(presence.toXML());
                mBottomSheetDialog.dismiss();
                break;
            case 3:
                presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.away);
                connection.sendPacket(presence);
                Log.v("state", "设置离开");
                System.out.println(presence.toXML());
                mBottomSheetDialog.dismiss();
                break;
            case 4:
                Roster roster = connection.getRoster();
                Collection<RosterEntry> entries = roster.getEntries();
                for (RosterEntry entry : entries) {
                    presence = new Presence(Presence.Type.unavailable);
                    presence.setPacketID(Packet.ID_NOT_AVAILABLE);
                    presence.setFrom(connection.getUser());
                    presence.setTo(entry.getUser());
                    connection.sendPacket(presence);
                    System.out.println(presence.toXML());
                }
//                // 向同一用户的其他客户端发送隐身状态
//                presence = new Presence(Presence.Type.unavailable);
//                presence.setPacketID(Packet.ID_NOT_AVAILABLE);
//                presence.setFrom(connection.getUser());
//                presence.setTo(StringUtils.parseBareAddress(connection.getUser()));
//                connection.sendPacket(presence);
                Log.v("state", "设置隐身");
                mBottomSheetDialog.dismiss();
                break;
            case 5:
                presence = new Presence(Presence.Type.unavailable);
                connection.sendPacket(presence);
                Log.v("state", "设置离线");
                mBottomSheetDialog.dismiss();
                break;
            default:
                break;
        }
    }
}
