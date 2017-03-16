package com.lvpf.samckdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lvpf.myapplicationd.R;
import com.lvpf.samckdemo.manager.XmppConnectionManager;
import com.lvpf.samckdemo.model.User;

import org.jivesoftware.smack.XMPPConnection;

import java.util.List;

import static android.widget.AdapterView.*;

/**
 * 添加好友类
 * @author  lvpf
 */
public class AddFriendActivity extends AppCompatActivity implements OnItemClickListener, OnClickListener {
    private EditText searchView;
    private ListView listView;
    private XMPPConnection con;
    private List<User> users;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        con = XmppConnectionManager.getInstance().getConnection();
        searchView = (EditText) findViewById(R.id.searchView);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View v) {
        users = XmppConnectionManager.getInstance().searchUsers(con.getServiceName(), searchView.getText().toString());
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int pos, View view, ViewGroup arg2) {
            if (null == view) {
                view = LayoutInflater.from(AddFriendActivity.this).inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView txt = (TextView) view.findViewById(android.R.id.text1);
            txt.setText(users.get(pos).getUserName());
            return view;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
//		Toast.makeText(this, users.get(pos).toString(), Toast.LENGTH_SHORT).show();
        if (XmppConnectionManager.getInstance().addUser(con.getRoster(), users.get(pos).getUserName() + "@" + con.getServiceName(), users.get(pos).getName(), null)) {
            XmppConnectionManager.getInstance().addUserToGroup(con.getRoster(), users.get(pos).getUserName() + "@" + con.getServiceName(), "Friends");
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            setResult(1);
            finish();
        } else
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
    }
}
