package com.lvpf.samckdemo.manager;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.lvpf.samckdemo.model.ChartHisBean;
import com.lvpf.samckdemo.model.Constant;
import com.lvpf.samckdemo.model.IMMessage;
import com.lvpf.samckdemo.model.Notice;
import com.lvpf.samckdemo.sql.SQLiteTemplate;
import com.lvpf.samckdemo.utils.StringUtils;

import java.util.List;

/**
 * 
 * 消息历史记录，
 * 
 * @author shimiso
 */
public class MessageManager {
	private static MessageManager messageManager = null;
	private static DBManager manager = null;

	private MessageManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		//String databaseName = sharedPre.getString(Constant.USERNAME, null);
		manager = DBManager.getInstance(context, Constant.DB_NAME);
		
	}

	public static MessageManager getInstance(Context context) {

		if (messageManager == null) {
			messageManager = new MessageManager(context);
		}

		return messageManager;
	}

	/**
	 * 
	 * 保存消息.
	 * 
	 * @param msg
	 * @author shimiso
	 * @update 2012-5-16 下午3:23:15
	 */
	public long saveIMMessage(IMMessage msg) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		if (StringUtils.notEmpty(msg.getContent())) {
			contentValues.put("content", StringUtils.doEmpty(msg.getContent()));
		}
		if (StringUtils.notEmpty(msg.getFromSubJid())) {
			contentValues.put("msg_from",
					StringUtils.doEmpty(msg.getFromSubJid()));
		}
		contentValues.put("msg_type", msg.getMsgType());
		contentValues.put("msg_time", msg.getTime());

		return st.insert("im_msg_his", contentValues);
	}

	/**
	 * 
	 * 更新状态.
	 * 
	 * @param status
	 * @author shimiso
	 * @update 2012-5-16 下午3:22:44
	 */
	public void updateStatus(String id, Integer status) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("status", status);
		st.updateById("im_msg_his", id, contentValues);
	}

	/**
	 * 
	 * 查找与某人的聊天记录聊天记录
	 * 
	 * @param pageNum
	 *            第几页
	 * @param pageSize
	 *            要查的记录条数
	 * @return
	 * @author shimiso
	 * @update 2012-7-2 上午9:31:04
	 */
	public List<IMMessage> getMessageListByFrom(String fromUser, int pageNum,
												int pageSize) {
		if (StringUtils.empty(fromUser)) {
			return null;
		}
		int fromIndex = (pageNum - 1) * pageSize;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<IMMessage> list = st.queryForList(
				new SQLiteTemplate.RowMapper<IMMessage>() {
					@Override
					public IMMessage mapRow(Cursor cursor, int index) {
						IMMessage msg = new IMMessage();
						msg.setContent(cursor.getString(cursor
								.getColumnIndex("content")));
						msg.setFromSubJid(cursor.getString(cursor
								.getColumnIndex("msg_from")));
						msg.setMsgType(cursor.getInt(cursor
								.getColumnIndex("msg_type")));
						msg.setTime(cursor.getString(cursor
								.getColumnIndex("msg_time")));
						return msg;
					}
				},
				"select content,msg_from, msg_type,msg_time from im_msg_his where msg_from=? order by msg_time desc limit ? , ? ",
				new String[] { "" + fromUser, "" + fromIndex, "" + pageSize });
		return list;

	}

	/**
	 * 
	 * 查找与某人的聊天记录总数
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-2 上午9:31:04
	 */
	public int getChatCountWithSb(String fromUser) {
		if (StringUtils.empty(fromUser)) {
			return 0;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st
				.getCount(
						"select _id,content,msg_from msg_type from im_msg_his where msg_from=?",
						new String[] { "" + fromUser });
		
	}
	

	
	/**
	 * 删除与某人的聊天记录 author shimiso
	 * 
	 * @param fromUser
	 */
	public int delChatHisWithSb(String fromUser) {
		if (StringUtils.empty(fromUser)) {
			return 0;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.deleteByCondition("im_msg_his", "msg_from=?",
				new String[] { "" + fromUser });
	}

}
